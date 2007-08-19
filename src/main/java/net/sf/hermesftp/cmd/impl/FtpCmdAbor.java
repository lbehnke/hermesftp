/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
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

package net.sf.hermesftp.cmd.impl;

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;

/**
 * <b>ABORT (ABOR)</b>
 * <p>
 * This command tells the server to abort the previous FTP service command and any associated
 * transfer of data. The abort command may require "special action", as discussed in the Section on
 * FTP Commands, to force recognition by the server. No action is to be taken if the previous
 * command has been completed (including data transfer). The control connection is not to be closed
 * by the server, but the data connection must be closed.
 * <p>
 * There are two cases for the server upon receipt of this command: (1) the FTP service command was
 * already completed, or (2) the FTP service command is still in progress.
 * <p>
 * In the first case, the server closes the data connection (if it is open) and responds with a 226
 * reply, indicating that the abort command was successfully processed.
 * <p>
 * In the second case, the server aborts the FTP service in progress and closes the data connection,
 * returning a 426 reply to indicate that the service request terminated abnormally. The server then
 * sends a 226 reply, indicating that the abort command was successfully processed.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdAbor extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        msgOut(MSG226);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Aborts the FTP session";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return false;
    }

}
