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

package de.apporiented.hermesftp.cmd.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.apporiented.hermesftp.cmd.AbstractFtpCmdRetr;
import de.apporiented.hermesftp.exception.FtpCmdException;
import de.apporiented.hermesftp.exception.FtpPermissionException;
import de.apporiented.hermesftp.streams.RafInputStream;
import de.apporiented.hermesftp.streams.RecordWriteSupport;
import de.apporiented.hermesftp.utils.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>RETRIEVE (RETR)</b>
 * <p>
 * This command causes the server-DTP to transfer a copy of the file, specified in the pathname, to
 * the server- or user-DTP at the other end of the data connection. The status and contents of the
 * file at the server site shall be unaffected.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdRetr extends AbstractFtpCmdRetr {

    private static Log log = LogFactory.getLog(FtpCmdRetr.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        super.execute();
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Download file from server";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void doPerformAccessChecks(File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("File not found");
        }
        if ((getPermission() & PRIV_READ) == 0) {
            throw new FtpPermissionException();
        }
        getCtx().updateIncrementalStat(STAT_FILES_DOWNLOADED, 1);
        getCtx().updateIncrementalStat(STAT_BYTES_DOWNLOADED, file.length());
    }

    /**
     * {@inheritDoc}
     */
    protected void doRetrieveRecordData(RecordWriteSupport rws, File file, long fileOffset)
            throws IOException {
        RafInputStream ris = new RafInputStream(file, fileOffset);
        byte[] recordBuffer;
        byte[] lastRecordBuffer = null;
        try {
            while ((recordBuffer = ris.readRecord()) != null) {
                writeRecord(rws, lastRecordBuffer, false);
                lastRecordBuffer = recordBuffer;
                if (isAbortRequested()) {
                    msgOut(MSG426);
                    log.debug("Record transfer aborted");
                    return;
                }
                getTransferRateLimiter().execute(recordBuffer.length);
            }
            writeRecord(rws, lastRecordBuffer, true);
            getCtx().updateAverageStat(STAT_DOWNLOAD_RATE,
                (int) getTransferRateLimiter().getCurrentTransferRate());
            msgOut(MSG226);

        } finally {
            IOUtils.closeGracefully(rws);
            IOUtils.closeGracefully(ris);
        }
    }

    private void writeRecord(RecordWriteSupport rws, byte[] lastRecordBuffer, boolean eof) throws IOException {
        if (lastRecordBuffer != null) {
            rws.writeRecord(lastRecordBuffer, eof);
            rws.flush();
            incCompleted(lastRecordBuffer.length);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doRetrieveFileData(OutputStream os, File file, long fileOffset) throws IOException {
        InputStream is = new RafInputStream(file, fileOffset);
        int bufferSize = getCtx().getOptions().getBufferSize();
        byte[] buffer = new byte[bufferSize];
        int count;
        try {
            while ((count = is.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
                incCompleted(count);
                if (isAbortRequested()) {
                    msgOut(MSG426);
                    log.debug("File transfer aborted");
                    return;
                }
                getTransferRateLimiter().execute(count);
            }
            getCtx().updateAverageStat(STAT_DOWNLOAD_RATE,
                (int) getTransferRateLimiter().getCurrentTransferRate());
            msgOut(MSG226);
        } finally {
            IOUtils.closeGracefully(is);
            IOUtils.closeGracefully(os);
        }
    }
}
