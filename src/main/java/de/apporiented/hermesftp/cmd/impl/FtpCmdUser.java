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

import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
import de.apporiented.hermesftp.exception.FtpCmdException;

/**
 * <b>USER NAME (USER)</b>
 * <p>
 * The argument field is a Telnet string identifying the user. The user identification is that which
 * is required by the server for access to its file system. This command will normally be the first
 * command transmitted by the user after the control connections are made (some servers may require
 * this). Additional identification information in the form of a password and/or an account command
 * may also be required by some servers. Servers may allow a new USER command to be entered at any
 * point in order to change the access control and/or accounting information. This has the effect of
 * flushing any user, password, and account information already supplied and beginning the login
 * sequence again. All transfer parameters are unchanged and any file transfer in progress is
 * completed under the old access control parameters.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdUser extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        boolean forceSsl = getCtx().getOptions().getBoolean(OPT_SSL_FORCE, false);
        Boolean ssl = (Boolean) getCtx().getAttribute(ATTR_SSL);

        if ((ssl == null || !ssl.booleanValue()) && forceSsl) {
            msgOut(MSG530_AUTH);
        } else if (getArguments().length() == 0) {
            msgOut(MSG501);
        } else {
            getCtx().setUser(getArguments());
            msgOut(MSG331);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the user name";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return false;
    }

}
