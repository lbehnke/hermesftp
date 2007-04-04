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

/**
 * <b>RESTART (REST)</b>
 * <p>
 * The argument field represents the server marker at which file transfer is to be restarted. This
 * command does not cause file transfer but skips over the file to the specified data checkpoint.
 * This command shall be immediately followed by the appropriate FTP service command which shall
 * cause file transfer to resume.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdRest
    extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        try {
            Long pointer = new Long(getArguments());
            getCtx().setAttribute(ATTR_FILE_OFFSET, pointer);
            msgOut(MSG350_REST, pointer.toString());
        } catch (NumberFormatException e) {
            msgOut(MSG501);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the file offset to be used in the following transfer command.";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
