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
 * REMOVE DIRECTORY (RMD)
 * <p>
 * This command causes the directory specified in the pathname to be removed as a directory (if the
 * pathname is absolute) or as a subdirectory of the current working directory (if the pathname is
 * relative).
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdRmd
    extends AbstractFtpCmd {

    private static Log log = LogFactory.getLog(FtpCmdRmd.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String response;
        File dir = new File(getPathArg());
        if (!dir.exists() || !dir.isDirectory()) {
            log.debug(dir + " not found");
            response = msg(MSG550);
        } else if (!isEmpty(dir)) {
            response = msg(MSG550_NOTEMPTY);
        } else {
            boolean deleted = delete(dir);
            response = deleted ? msg(MSG250) : msg(MSG450);

        }
        out(response);
    }

    private boolean delete(File dir) {
        boolean deleted = true;
        if ((getCtx().getPermission(dir.getAbsolutePath()) & PRIV_WRITE) > 0) {
            File[] list = dir.listFiles();
            for (int i = 0; i < list.length; i++) {
                deleted &= delete(list[i]);
            }
            deleted &= dir.delete();
        } else {
            deleted = false;
        }
        return deleted;
    }

    /**
     * Checks if the directory is empty.
     *
     * @param dir The directory.
     * @return True, if the directory is empty.
     */
    private boolean isEmpty(File dir) {
        File[] list = dir.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile()) {
                return false;
            } else if (!isEmpty(list[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Removes a directory";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
