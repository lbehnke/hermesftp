/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2014 Lars Behnke
 * ------------------------------------------------------------------------------
 * 
 * This file is part of Hermes FTP Server.
 * 
 * Hermes FTP Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Hermes FTP Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hermes FTP Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * ------------------------------------------------------------------------------
 */

package de.apporiented.hermesftp.cmd;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.zip.DeflaterOutputStream;

import de.apporiented.hermesftp.common.FtpConstants;
import de.apporiented.hermesftp.exception.FtpCmdException;
import de.apporiented.hermesftp.exception.FtpPermissionException;
import de.apporiented.hermesftp.exception.FtpQuotaException;
import de.apporiented.hermesftp.streams.BlockModeOutputStream;
import de.apporiented.hermesftp.streams.RecordOutputStream;
import de.apporiented.hermesftp.streams.RecordWriteSupport;
import de.apporiented.hermesftp.streams.TextOutputStream;
import de.apporiented.hermesftp.utils.TransferRateLimiter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract base class for RETR command implementations.
 * 
 * @author Lars Behnke
 */
public abstract class AbstractFtpCmdRetr extends AbstractFtpCmd implements FtpConstants {

    private static Log          log                 = LogFactory.getLog(AbstractFtpCmdRetr.class);

    private TransferRateLimiter transferRateLimiter = new TransferRateLimiter();

    private long                fileSize;

    private long                completed;

    private boolean             abortRequested;

    /**
     * Checks availability and access rights for the current folder and passed file. The methods
     * acts as a primitive operation that is called by the template method
     * <code>execute(boolean)</code>;
     * 
     * @param file The destination file.
     * @throws IOException Thrown if one of the following conditions occurred: (1) IO failed or (3)
     *             access rights have been violated or (3) resource limits have been reached.
     */
    protected abstract void doPerformAccessChecks(File file) throws IOException;

    /**
     * Retrieves record based data. Since native files generally do not support records, the
     * assumption is made that each line of a text file corresponds to a record. The method acts as
     * a primitive operation that is called by the template method <code>execute()</code>;
     * Futhermore, text record data must be encoded by an 1-byte character set (ACII, ANSI or
     * EBCDIC).
     * 
     * @param out The output stream.
     * @param file The source file.
     * @param fileOffset The file offset.
     * @throws IOException Thrown if IO fails or if a resource limit has been reached.
     */
    protected abstract void doRetrieveRecordData(RecordWriteSupport out, File file, long fileOffset)
            throws IOException;

    /**
     * Retrieves file based data. The method acts as a primitive operation that is called by the
     * template method <code>execute()</code>;
     * 
     * @param out The output stream.
     * @param file The source file.
     * @param fileOffset The file offset.
     * @throws IOException Thrown if IO fails or if a resource limit has been reached.
     */
    protected abstract void doRetrieveFileData(OutputStream out, File file, long fileOffset)
            throws IOException;

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
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {

        /* Get relevant information from context */
        File file = new File(getPathArg());
        int mode = getCtx().getTransmissionMode();
        int struct = getCtx().getStorageStructure();
        int type = getCtx().getDataType();
        String charset = type == DT_ASCII || type == DT_EBCDIC ? getCtx().getCharset() : null;
        long fileOffset = getAndResetFileOffset();
        getTransferRateLimiter().init(getCtx().getMaxDownloadRate());
        try {

            /* Check availability and access rights */
            doPerformAccessChecks(file);

            msgOut(MSG150);

            /* Wrap outbound data stream and call handler method */
            Socket dataSocket = getCtx().getDataSocketProvider().provideSocket();
            OutputStream dataOut = dataSocket.getOutputStream();
            if (struct == STRUCT_RECORD) {
                RecordWriteSupport recordOut = createRecOutputStream(dataOut, mode, charset);
                doRetrieveRecordData(recordOut, file, fileOffset);
            } else if (struct == STRUCT_FILE) {
                OutputStream fileOut = createOutputStream(dataOut, mode, charset);
                doRetrieveFileData(fileOut, file, fileOffset);
            } else {
                log.error("Unknown data type");
                msgOut(MSG550, "Unsupported data type");
                //dataSocket.shutdownOutput();
                return;
            }
            // TODO delegate event to FtpEventListener

            dataOut.flush();

        } catch (FtpQuotaException e) {
            msgOut(MSG550, e.getMessage());
            log.warn(e.getMessage());
        } catch (FtpPermissionException e) {
            msgOut(MSG550_PERM);
        } catch (UnsupportedEncodingException e) {
            msgOut(MSG550, "Unsupported Encoding: " + charset);
            log.error(e.toString());
        } catch (IOException e) {
            msgOut(MSG550);
            log.error(e.toString());
        } catch (RuntimeException e) {
            msgOut(MSG550);
            log.error(e.toString());
        } finally {
            getCtx().closeSockets();
        }
    }

    private OutputStream createOutputStream(OutputStream dataOut, int mode, String charset)
            throws UnsupportedEncodingException {
        OutputStream result = null;
        if (mode == MODE_BLOCK) {
            result = new BlockModeOutputStream(dataOut);
        } else if (mode == MODE_STREAM) {
            result = dataOut;
        } else if (mode == MODE_ZIP) {
            result = new DeflaterOutputStream(dataOut);
        } else {
            log.error("Unsupported file mode: " + mode);
        }
        if (charset != null) {
            result = new TextOutputStream(result, charset);
        }
        return result;
    }

    private RecordWriteSupport createRecOutputStream(OutputStream dataOut, int mode, String charset)
            throws UnsupportedEncodingException {
        RecordWriteSupport result = null;
        if (mode == MODE_BLOCK) {
            result = new BlockModeOutputStream(dataOut);
        } else if (mode == MODE_STREAM) {
            result = new RecordOutputStream(dataOut);
        } else if (mode == MODE_ZIP) {
            result = new RecordOutputStream(new DeflaterOutputStream(dataOut));
        } else {
            log.error("Unsupported record mode: " + mode);
        }
        if (charset != null) {
            result = new TextOutputStream((OutputStream) result, charset);
        }
        return result;
    }

    /**
     * @return True, if transfer has been aborted.
     */
    protected boolean isAbortRequested() {
        return abortRequested;
    }

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

    /**
     * Getter Methode fuer die Eigenschaft <code>transferRateLimiter</code>.
     * 
     * @return Wert der Eigenschaft <code>transferRateLimiter</code>.
     */
    public TransferRateLimiter getTransferRateLimiter() {
        return transferRateLimiter;
    }

    /**
     * @param transferRateLimiter the transferRateLimiter to set
     */
    public void setTransferRateLimiter(TransferRateLimiter transferRateLimiter) {
        this.transferRateLimiter = transferRateLimiter;
    }

}
