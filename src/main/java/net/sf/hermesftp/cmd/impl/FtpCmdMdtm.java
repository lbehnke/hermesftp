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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;

/**
 * <b>MDTM</b>
 * <p>
 * Modifies the timestamp of a file or path.
 *
 * @author Lars Behnke
 */
public class FtpCmdMdtm
    extends AbstractFtpCmd {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String[] argParts = getArguments().split(" ");
        Date date = null;
        String pathName = null;

        /* Check arguments */
        if (argParts.length == 2) {
            try {
                date = dateFormat.parse(argParts[0]);
            } catch (ParseException e) {
                date = null;
            }
            pathName = argParts[1].trim();
        } else if (argParts.length == 1) {
            date = new Date();
            pathName = argParts[0];
        }
        if (date == null || pathName == null) {
            msgOut(MSG501);
            return;
        }

        /* Proceed with valid arguments */
        setModificationDate(date, pathName);
    }

    private void setModificationDate(Date date, String pathName) {
        pathName = getAbsPath(pathName);
        File path = new File(pathName);
        if (path.exists()) {
            if ((getCtx().getPermission(pathName) & PRIV_WRITE) == 0) {
                msgOut(MSG550_PERM);
            } else {
                path.setLastModified(date.getTime());
                msgOut(MSG200);
            }
        } else {
            msgOut(MSG501_PATH);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the modification date of a file/path";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
