/*
 ------------------------------
 Hermes FTP Server
 Copyright (c) 2006 Lars Behnke
 ------------------------------

 This file is part of Hermes FTP Server.

 Hermes FTP Server is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Foobar; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sf.hermesftp.cmd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.exception.FtpException;
import net.sf.hermesftp.exception.FtpPermissionException;
import net.sf.hermesftp.exception.FtpQuotaException;
import net.sf.hermesftp.exception.FtpUniqueConstraintException;
import net.sf.hermesftp.streams.BlockModeInputStream;
import net.sf.hermesftp.streams.RecordInputStream;
import net.sf.hermesftp.streams.RecordReadSupport;
import net.sf.hermesftp.streams.TextInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ancestor command class that is extended by commands that store data on the remote file system.
 * 
 * @author Lars Behnke
 */
public abstract class AbstractFtpCmdStor
    extends AbstractFtpCmd {

    private static Log log = LogFactory.getLog(AbstractFtpCmdStor.class);

    private long fileSize;

    private long completed;

    private boolean abortRequested;

    /**
     * Executes the command. This operation acts as a template method calling primitive operations
     * implemented by the sub classes.
     * 
     * @param unique True, if file that is supposed to be stored may not exist on the remote file
     *            system.
     * @throws FtpCmdException Wrapper class for any exception thrown in the command.
     */
    public void execute(boolean unique) throws FtpCmdException {

        /* Get relevant information from context */
        File file = new File(getPathArg());
        int mode = getCtx().getTransmissionMode();
        int struct = getCtx().getStorageStructure();
        int type = getCtx().getDataType();
        String charset = type == DT_ASCII || type == DT_EBCDIC ? getCtx().getCharset() : null;
        long fileOffset = getAndResetFileOffset();

        try {
            /* Check availability and access rights */
            doPerformAccessChecks(unique, file, fileOffset);

            /* Initialize restart markers (block transfer mode) */
            Map restartMarkers = new HashMap();
            getCtx().setAttribute(ATTR_RESTART_MARKERS, restartMarkers);

            /* Wrap inbound data stream and call handler method */
            msgOut(MSG150);
            
            Socket dataSocket = getCtx().getDataSocketProvider().provideSocket();
            InputStream dataIn = dataSocket.getInputStream();
            if (struct == STRUCT_RECORD) {
                RecordReadSupport recordIn = createRecInputStream(dataIn,
                                                                  mode,
                                                                  charset,
                                                                  restartMarkers);
                doStoreRecordData(recordIn, file, fileOffset);
            } else if (struct == STRUCT_FILE) {
                InputStream fileIn = createInputStream(dataIn, mode, restartMarkers, charset);
                doStoreFileData(fileIn, file, fileOffset);
            } else {
                log.error("Unknown data type");
                msgOut(MSG550, "Unsupported data type");
            }
            // TODO delegate event to FtpEventListener
            // getCtx().getEventListener().

        } catch (FtpUniqueConstraintException e) {
            msgOut(MSG553);
        } catch (FtpPermissionException e) {
            msgOut(MSG550_PERM);
        } catch (FtpException e) {
            msgOut(MSG550_MSG, e.getMessage());
            log.warn(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            msgOut(MSG550_MSG, "Unsupported Encoding: " + charset);
            log.error(e.toString());
        } catch (IOException e) {
            msgOut(MSG550);
            log.error(e.toString());
        } catch (RuntimeException e) {
            msgOut(MSG550);
            log.error(e.toString());
        } finally {
            getCtx().getDataSocketProvider().closeSocket();
        }
    }

    /**
     * Creates an input stream that supports unstructured file data.
     * 
     * @param is The nested input stream.
     * @param mode The transmission mode.
     * @param charset The encoding or null if binary.
     * @param restartMarkers Optional map that stores restart markers.
     * @return The stream object.
     * @throws UnsupportedEncodingException Thrown if encoding is unknown.
     */
    private InputStream createInputStream(InputStream is, int mode, Map restartMarkers,
                                          String charset) throws UnsupportedEncodingException {
        InputStream result = null;
        if (mode == MODE_BLOCK) {
            byte[] eorBytes = getEorBytes(null);
            result = new BlockModeInputStream(is, eorBytes, restartMarkers);
        } else if (mode == MODE_STREAM) {
            result = is;
        } else {
            log.error("Unsupported mode: " + mode);
        }
        if (charset != null) {
            result = new TextInputStream(is, charset);
        }
        return result;

    }

    // /**
    // * Creates a reader instance that supports unstructured text file data.
    // *
    // * @param is The nested input stream.
    // * @param mode The transmission mode.
    // * @param charset The encoding or null if binary.
    // * @param restartMarkers Optional map that stores restart markers.
    // * @return The stream object.
    // * @throws UnsupportedEncodingException Thrown if encoding is unknown.
    // */
    // private Reader createReader(InputStream is, int mode, Map restartMarkers, String charset)
    // throws UnsupportedEncodingException {
    // if (mode == MODE_BLOCK) {
    // byte[] eorBytes = getEorBytes(charset);
    // is = new BlockModeInputStream(is, eorBytes, restartMarkers);
    // } else if (mode != MODE_STREAM) {
    // log.error("Unsupported mode: " + mode);
    // return null;
    // }
    // return new InputStreamReader(is, charset);
    // }

    /**
     * Creates an input stream that supports reading records.
     * 
     * @param is The nested input stream.
     * @param mode The transmission mode.
     * @param charset The encoding or null if binary.
     * @param restartMarkers Optional map that stores restart markers.
     * @return The stream object.
     * @throws UnsupportedEncodingException Thrown if encoding unknown.
     */
    private RecordReadSupport createRecInputStream(InputStream is, int mode, String charset,
                                                   Map restartMarkers)
            throws UnsupportedEncodingException {
        RecordReadSupport result = null;
        byte[] eorBytes = charset == null ? new byte[0] : getEorBytes(charset);
        if (mode == MODE_BLOCK) {
            result = new BlockModeInputStream(is, eorBytes, restartMarkers);
        } else if (mode == MODE_STREAM) {
            result = new RecordInputStream(is, getEorBytes(charset));
        } else {
            log.error("Unsupported mode: " + mode);
        }
        if (charset != null) {
            result = new TextInputStream((InputStream) result, charset);
        }
        return result;

    }

    /**
     * Returns the EOR-byte representation in non-record text files, which corresponds to the line
     * break sequence of the passed character set.
     * 
     * @param charset The character set.
     * @return The EOR marker.
     */
    private static byte[] getEorBytes(String charset) {
        String lineSep = System.getProperty("line.separator");
        try {
            if (charset == null) {
                return lineSep.getBytes();
            } else {
                return lineSep.getBytes(charset);
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e);
            return lineSep.getBytes();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean handleAsyncCmd(String req) {
        boolean result;
        if (req == null || isResponded()) {
            result = false;
        } else if (req.toUpperCase().startsWith("STAT")) {
            String stat = "STAT: " + getCompleted() + " from " + getFileSize() + " completed";
            log.info(stat);
            // TODO Return statistics response.
            result = true;
        } else if (req.toUpperCase().startsWith("ABOR")) {
            abortRequested = true;
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Checks availability and access rights for the current folder and passed file. The methods
     * acts as a primitive operation that is called by the template method
     * <code>execute(boolean)</code>;
     * 
     * @param unique True, if destination file may not exist already.
     * @param file The destination file.
     * @param offset The file offset (-1 on append).
     * @throws FtpException Thrown if permission rules have been violated or resource limits have
     *             been exceeded.
     */
    protected abstract void doPerformAccessChecks(boolean unique, File file, long offset)
            throws FtpException;

    /**
     * Stores record based data as file. The method acts as a primitive operation that is called by
     * the template method <code>execute(boolean)</code>;
     * 
     * @param rrs The wrapped input stream.
     * @param file Destination file.
     * @param offset The file offset (-1 on append).
     * @throws IOException Thrown if IO fails.
     * @throws FtpQuotaException Thrown if at least one resource limit was reached.
     */
    protected abstract void doStoreRecordData(RecordReadSupport rrs, File file, long offset)
            throws IOException, FtpQuotaException;

    /**
     * Stores unstructured data as file. The method acts as a primitive operation that is called by
     * the template method <code>execute(boolean)</code>;
     * 
     * @param is The input stream.
     * @param file Destination file.
     * @param offset The file offset (-1 on append).
     * @throws IOException Thrown if IO fails.
     * @throws FtpQuotaException Thrown if at least one resource limit was reached.
     */
    protected abstract void doStoreFileData(InputStream is, File file, long offset)
            throws IOException, FtpQuotaException;

    /**
     * Getter method for the java bean <code>completed</code>.
     * 
     * @return Returns the value of the java bean <code>completed</code>.
     */
    public synchronized long getCompleted() {
        return completed;
    }

    /**
     * Setter method for the java bean <code>completed</code>.
     * 
     * @param completed The value of completed to set.
     */
    public synchronized void incCompleted(long completed) {
        this.completed += completed;
    }

    /**
     * Getter method for the java bean <code>fileSize</code>.
     * 
     * @return Returns the value of the java bean <code>fileSize</code>.
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Setter method for the java bean <code>fileSize</code>.
     * 
     * @param fileSize The value of fileSize to set.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    //
    // private void storeZlib(File file, ByteArrayOutputStream dataOut, boolean append)
    // throws IOException {
    // byte[] uncompressedData = dataOut.toByteArray();
    // byte[] compressedData = new byte[uncompressedData.length];
    // Deflater compressor = new Deflater();
    // compressor.setInput(uncompressedData);
    // compressor.finish();
    // int compressedDataLength = compressor.deflate(compressedData);
    // OutputStream zlibOut = new BufferedOutputStream(new FileOutputStream(file, append));
    // try {
    // zlibOut.write(compressedData, 0, compressedDataLength);
    // zlibOut.flush();
    // } finally {
    // IOUtils.closeGracefully(zlibOut);
    // }
    // }

    /**
     * @return True if abort has been requested.
     */
    protected boolean isAbortRequested() {
        return abortRequested;
    }

}
