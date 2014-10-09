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
import java.io.InputStream;
import java.io.OutputStream;

import de.apporiented.hermesftp.exception.FtpCmdException;
import de.apporiented.hermesftp.exception.FtpException;
import de.apporiented.hermesftp.exception.FtpPermissionException;
import de.apporiented.hermesftp.exception.FtpUniqueConstraintException;
import de.apporiented.hermesftp.streams.RafOutputStream;
import de.apporiented.hermesftp.streams.RecordReadSupport;
import de.apporiented.hermesftp.utils.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract STOR command implementation that saves files to the local disk.
 * 
 * @author Lars Behnke
 */
public abstract class AbstractFtpCmdStorFile extends AbstractFtpCmdStor {

    private static Log log = LogFactory.getLog(AbstractFtpCmdStorFile.class);

    /**
     * {@inheritDoc}
     */
    public void execute(boolean unique) throws FtpCmdException {
        super.execute(unique);
    }

    /**
     * {@inheritDoc}
     */
    protected void doPerformAccessChecks(boolean unique, File file, long fileOffset) throws FtpException {
        if ((getPermission() & PRIV_WRITE) == 0) {
            throw new FtpPermissionException();
        }

        String[] limits = new String[] {STAT_FILES_UPLOADED, STAT_BYTES_UPLOADED};
        getCtx().getUserManager().checkResourceConsumption(getCtx().getUser(), limits);

        if (file.isDirectory()) {
            throw new FtpPermissionException("Cannot store directory path.");
        }
        if (file.exists()) {
            if (unique) {
                throw new FtpUniqueConstraintException();
            } else if (fileOffset == 0) {
                try {
                    file.delete();
                } catch (SecurityException e) {
                    throw new FtpPermissionException("System access rights have been violated.");
                }
            }
        }
        getCtx().updateIncrementalStat(STAT_FILES_UPLOADED, 1);

    }

    /**
     * {@inheritDoc}
     */
    protected void doStoreFileData(InputStream is, File file, long offset) throws IOException {
        OutputStream os = new RafOutputStream(file, offset);
        int bufferSize = getCtx().getOptions().getBufferSize();
        byte[] buffer = new byte[bufferSize];
        int count;
        try {
            while ((count = is.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
                getCtx().updateIncrementalStat(STAT_BYTES_UPLOADED, count);
                incCompleted(count);
                if (isAbortRequested()) {
                    log.debug("File transfer aborted");
                    msgOut(MSG426);
                    return;
                }
                getTransferRateLimiter().execute(count);

            }
            getCtx().updateAverageStat(STAT_UPLOAD_RATE,
                (int) getTransferRateLimiter().getCurrentTransferRate());
            msgOut(MSG226);
        } finally {
            IOUtils.closeGracefully(is);
            IOUtils.closeGracefully(os);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doStoreRecordData(RecordReadSupport rrs, File file, long offset) throws IOException {
        RafOutputStream os = new RafOutputStream(file, offset);
        byte[] recordBuffer = null;
        byte[] lastRecordBuffer = null;
        try {
            while ((recordBuffer = rrs.readRecord()) != null) {
                writeRecord(os, lastRecordBuffer, false);
                lastRecordBuffer = recordBuffer;
                if (isAbortRequested()) {
                    log.debug("Record transfer aborted");
                    msgOut(MSG426);
                    return;
                }
                getTransferRateLimiter().execute(recordBuffer.length);
            }
            writeRecord(os, lastRecordBuffer, true);
            getCtx().updateAverageStat(STAT_UPLOAD_RATE,
                (int) getTransferRateLimiter().getCurrentTransferRate());
            msgOut(MSG226);
        } finally {
            IOUtils.closeGracefully(rrs);
            IOUtils.closeGracefully(os);
        }
    }

    private void writeRecord(RafOutputStream os, byte[] lastRecordBuffer, boolean eof) throws IOException {
        if (lastRecordBuffer != null) {
            os.writeRecord(lastRecordBuffer, eof);
            getCtx().updateIncrementalStat(STAT_BYTES_UPLOADED, lastRecordBuffer.length);
            incCompleted(lastRecordBuffer.length);
        }
    }
}
