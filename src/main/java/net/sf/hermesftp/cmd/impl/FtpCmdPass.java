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

/**
 * PASSWORD (PASS):
 * <p>
 * The argument field is a Telnet string specifying the user's password. This command must be
 * immediately preceded by the user name command, and, for some sites, completes the user's
 * identification for access control. Since password information is quite sensitive, it is desirable
 * in general to "mask" it or suppress typeout. It appears that the server has no foolproof way to
 * achieve this. It is therefore the responsibility of the user-FTP process to hide the sensitive
 * password information.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 */
public class FtpCmdPass
    extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String response;
        if (getCtx().getUser() == null || getCtx().getUser().length() == 0) {
            response = msg(MSG503_USR);
        } else {
            getCtx().setPassword(getArguments());
            if (getCtx().authenticate()) {
                response = msg(MSG230);
            } else {
                response = msg(MSG530);
            }
        }
        out(response);
        String clientHost = getCtx().getClientSocket().getInetAddress().getHostAddress();
        getCtx().getEventListener().loginPerformed(clientHost, getCtx().isAuthenticated());
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the user's password";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return false;
    }

}
