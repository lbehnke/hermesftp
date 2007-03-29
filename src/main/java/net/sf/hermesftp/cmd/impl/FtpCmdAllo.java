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

import java.io.IOException;

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ALLOCATE (ALLO):
 * <p>
 * This command may be required by some servers to reserve sufficient storage to accommodate the new
 * file to be transferred. The argument shall be a decimal integer representing the number of bytes
 * (using the logical byte size) of storage to be reserved for the file. For files sent with record
 * or page structure a maximum record or page size (in logical bytes) might also be necessary; this
 * is indicated by a decimal integer in a second argument field of the command. This second argument
 * is optional, but when present should be separated from the first by the three Telnet characters "
 * R ". This command shall be followed by a STORe or APPEnd command. The ALLO command should be
 * treated as a NOOP (no operation) by those servers which do not require that the maximum size of
 * the file be declared beforehand, and those servers interested in only the maximum record or page
 * size should accept a dummy value in the first argument and ignore it.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdAllo
    extends AbstractFtpCmd {

    private static Log log = LogFactory.getLog(FtpCmdAllo.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String response;
        try {
            long requiredSize = Long.parseLong(getArguments().trim());
            long availSize = FileSystemUtils.freeSpace(getCtx().getRemoteDir());
            if (requiredSize <= availSize) {
                response = msg(MSG200_SIZE, new Object[] {new Long(availSize)});
            } else {
                response = msg(MSG501_SIZE, new Object[] {new Long(availSize)});
            }
        } catch (NumberFormatException e1) {
            response = msg(MSG501);
        } catch (IOException e) {
            log.error("Could not read disk space: " + e);
            response = msg(MSG550);
        }
        out(response);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Returns the available disk space.";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
