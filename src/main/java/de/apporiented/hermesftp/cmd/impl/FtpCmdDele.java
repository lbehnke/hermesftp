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

import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
import de.apporiented.hermesftp.exception.FtpCmdException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>DELETE (DELE)</b>
 * <p>
 * This command causes the file specified in the pathname to be deleted at the server site. If an
 * extra level of protection is desired (such as the query, "Do you really wish to delete?"), it
 * should be provided by the user-FTP process.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdDele extends AbstractFtpCmd {

    private static Log log = LogFactory.getLog(FtpCmdDele.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String response;
        if ((getPermission() & PRIV_WRITE) == 0) {
            response = msg(MSG550_PERM);
        } else {
            File file = new File(getPathArg());
            if (!file.exists() || file.isDirectory()) {
                log.debug(file + " not found");
                response = msg(MSG550);
            } else {
                boolean deleted = file.delete();
                response = deleted ? msg(MSG250) : msg(MSG450);
            }
        }
        out(response);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Delete file";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
