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

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;

import org.apache.commons.io.FileUtils;

/**
 * SIZE:
 *
 * Returns the size of the passed path in bytes.
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdSize
    extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        File path = new File(getPathArg());
        if (!path.exists()) {
            msgOut(MSG550);
        } else if ((getCtx().getPermission(path.getAbsolutePath()) & PRIV_READ) == 0) {
            msgOut(MSG550_PERM);
        } else if (path.isDirectory()) {
            msgOut(MSG213_SIZE, new Object[] {new Long(FileUtils.sizeOfDirectory(path))});
        } else {
            msgOut(MSG213_SIZE, new Object[] {new Long(path.length())});
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Calculates the size of a path";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
