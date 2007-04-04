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

import net.sf.hermesftp.cmd.AbstractFtpCmdStorFile;
import net.sf.hermesftp.exception.FtpCmdException;

/**
 * <b>APPEND (with create) (APPE)</b>
 * <p>
 * This command causes the server-DTP to accept the data transferred via the data connection and to
 * store the data in a file at the server site. If the file specified in the pathname exists at the
 * server site, then the data shall be appended to that file; otherwise the file specified in the
 * pathname shall be created at the server site.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 */
public class FtpCmdAppe
    extends AbstractFtpCmdStorFile {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        getCtx().setAttribute(ATTR_FILE_OFFSET, new Long(-1));
        super.execute(false);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Append data to file on server";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
