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

import org.apache.commons.io.FileUtils;

/**
 * <b>SIZE</b> Returns the size of the passed path in bytes.
 * 
 * @author Lars Behnke
 */
public class FtpCmdSize extends AbstractFtpCmd {

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

            /* This is the binary length. In ASCII mode the size may differ, see RFC 3659, chap. 4 */
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
