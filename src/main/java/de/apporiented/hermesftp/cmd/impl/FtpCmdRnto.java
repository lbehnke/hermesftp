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
 * <b>RENAME TO (RNTO)</b>
 * <p>
 * This command specifies the new pathname of the file specified in the immediately preceding
 * "rename from" command. Together the two commands cause a file to be renamed.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdRnto extends AbstractFtpCmd {

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
