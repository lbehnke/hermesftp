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

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.exception.FtpQuitException;

/**
 * LOGOUT (QUIT):
 *
 * This command terminates a USER and if file transfer is not in progress, the server closes the
 * control connection. If file transfer is in progress, the connection will remain open for result
 * response and the server will then close it. If the user-process is transferring files for several
 * USERs but does not wish to close and then reopen connections for each, then the REIN command
 * should be used instead of QUIT. An unexpected close on the control connection will cause the
 * server to take the effective action of an abort (ABOR) and a logout (QUIT).
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 */
public class FtpCmdQuit
    extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        getCtx().closeSockets();
        String goodbye = getCtx().getOptions().getProperty(OPT_MSG_GOODBYE);
        if (goodbye == null || goodbye.length() == 0) {
            goodbye = getCtx().getRes(MSG_GOODBYE);
        }
        out("221 " + goodbye);
        throw new FtpQuitException();
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Close FTP session";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return false;
    }

}
