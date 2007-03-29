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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RENAME TO (RNTO)
 * <p>
 * This command specifies the new pathname of the file specified in the immediately preceding
 * "rename from" command. Together the two commands cause a file to be renamed.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdRnto
    extends AbstractFtpCmd {

    private static Log log = LogFactory.getLog(FtpCmdRnto.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        File originalPath = (File) getCtx().getAttribute(ATTR_RENAME_FILE);
        if (originalPath == null) {
            msgOut(MSG503);
            return;
        }
        getCtx().setAttribute(ATTR_RENAME_FILE, null);
        if (getArguments().length() == 0) {
            msgOut(MSG501);
            return;
        }
        String newPathName = getPathArg();
        File newPath = new File(newPathName);
        if (newPath.exists()) {
            log.debug(newPath + " already exists");
            msgOut(MSG550_EXISTS);
            return;
        }
        boolean renamed = originalPath.renameTo(newPath);
        if (!renamed) {
            msgOut(MSG451);
            return;
        }
        msgOut(MSG200);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
