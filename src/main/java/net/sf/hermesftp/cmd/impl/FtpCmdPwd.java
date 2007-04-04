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

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;

import org.apache.commons.io.FilenameUtils;

/**
 * <b>PRINT WORKING DIRECTORY (PWD)</b>
 *
 * This command causes the name of the current working directory to be returned in the reply..
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 */
public class FtpCmdPwd
    extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String dir = FilenameUtils.separatorsToUnix(getCtx().getRemoteRelDir());
        String comment = getCtx().getRes(PWD);
        msgOut(MSG257, new Object[] {dir, comment});
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Momentanes Verzeichnis zurückliefern";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
