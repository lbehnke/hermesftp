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

package net.sf.hermesftp.cmd.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.hermesftp.cmd.AbstractFtpCmdRetr;
import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.exception.FtpPermissionException;
import net.sf.hermesftp.exception.FtpQuotaException;
import net.sf.hermesftp.streams.RafInputStream;
import net.sf.hermesftp.streams.RecordWriteSupport;
import net.sf.hermesftp.utils.IOUtils;

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
public class FtpCmdRetr
    extends AbstractFtpCmdRetr {

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
    protected void doPerformAccessChecks(File file) throws FtpQuotaException,
            FtpPermissionException, IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("File not found");
        }
        if ((getPermission() & PRIV_READ) == 0) {
            throw new FtpPermissionException();
        }
        incConsumption(STAT_FILES_DOWNLOADED, 1);
        incConsumption(STAT_BYTES_DOWNLOADED, file.length());
    }

    /**
     * {@inheritDoc}
     */
    protected void doRetrieveRecordData(RecordWriteSupport rws, File file, long fileOffset)
            throws IOException, FtpQuotaException {
        RafInputStream ris = new RafInputStream(file, fileOffset);
        byte[] recordBuffer = null;
        byte[] lastRecordBuffer = null;
        try {
            while ((recordBuffer = ris.readRecord()) != null) {
                writeRecord(rws, lastRecordBuffer, false);
                lastRecordBuffer = recordBuffer;
                if (isAbortRequested()) {
                    msgOut(MSG426);
                    log.debug("Transfer aborted");
                    return;
                }
            }
            writeRecord(rws, lastRecordBuffer, true);
            msgOut(MSG226);

        } finally {
            IOUtils.closeGracefully(rws);
            IOUtils.closeGracefully(ris);
        }
    }

    private void writeRecord(RecordWriteSupport rws, byte[] lastRecordBuffer, boolean eof)
            throws IOException, FtpQuotaException {
        if (lastRecordBuffer != null) {
            rws.writeRecord(lastRecordBuffer, eof);
            rws.flush();
            incCompleted(lastRecordBuffer.length);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doRetrieveFileData(OutputStream os, File file, long fileOffset)
            throws IOException, FtpQuotaException {
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
                    log.debug("Transfer aborted");
                    return;
                }
            }
            msgOut(MSG226);
        } finally {
            IOUtils.closeGracefully(is);
            IOUtils.closeGracefully(os);
        }
    }
}
