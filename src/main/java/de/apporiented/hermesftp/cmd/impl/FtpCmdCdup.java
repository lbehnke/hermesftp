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

/**
 * <b>CHANGE TO PARENT DIRECTORY (CDUP)</b>
 * <p>
 * This command is a special case of CWD, and is included to simplify the implementation of programs
 * for transferring directory trees between operating systems having different syntaxes for naming
 * the parent directory. The reply codes shall be identical to the reply codes of CWD. See Appendix
 * II for further details.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdCdup extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        File dir = new File(getCtx().getRemoteDir());
        String parent = dir.getParent();
        if (parent == null) {
            msgOut(MSG550);
            return;
        }
        if ((getCtx().getPermission(parent) & PRIV_READ) == 0) {
            msgOut(MSG550_PERM);
            return;
        }
        getCtx().setRemoteDir(parent);
        msgOut(MSG250);
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
