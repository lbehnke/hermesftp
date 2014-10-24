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

package com.apporiented.hermesftp.cmd.impl;

import java.io.File;

import com.apporiented.hermesftp.cmd.AbstractFtpCmd;
import com.apporiented.hermesftp.exception.FtpCmdException;

/**
 * <b>CHANGE WORKING DIRECTORY (CWD)</b>
 * <p>
 * This command allows the user to work with a different directory or dataset for file storage or
 * retrieval without altering his login or accounting information. Transfer parameters are similarly
 * unchanged. The argument is a pathname specifying a directory or other system dependent file group
 * designator.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdCwd extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String response;
        String dirName;
        dirName = getPathArg();
        File dir = new File(dirName);
        if ((getCtx().getPermission(dirName) & PRIV_READ) == 0) {
            response = msg(MSG550_PERM);
        } else if (dir.exists() && dir.isDirectory()) {
            getCtx().setRemoteDir(dirName);
            response = msg(MSG250);
        } else {
            response = msg(MSG501_PATH);
        }
        out(response);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Changes into the parent directory";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
