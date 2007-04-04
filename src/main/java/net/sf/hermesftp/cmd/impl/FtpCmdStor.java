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
 * <b>STORE (STOR)</b>
 * <p>
 * This command causes the server-DTP to accept the data transferred via the data connection and to
 * store the data as a file at the server site. If the file specified in the pathname exists at the
 * server site, then its contents shall be replaced by the data being transferred. A new file is
 * created at the server site if the file specified in the pathname does not already exist.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 */
public class FtpCmdStor
    extends AbstractFtpCmdStorFile {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        super.execute(false);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Store file on server";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
