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
 * TRANSFER MODE (MODE)
 * <p>
 * The argument is a single Telnet character code specifying the data transfer modes described in
 * the Section on Transmission Modes.
 *
 * The following codes are assigned for transfer modes:
 * <ul>
 * <li> S - Stream
 * <li> B - Block
 * <li> C - Compressed
 * </ul>
 * The default transfer mode is Stream.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdMode
    extends AbstractFtpCmd {

    /**
     * Currently stream and block modes are supported.
     *
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String response;

        String stru = getArguments().toUpperCase().trim();

        if ("S".equals(stru)) {
            getCtx().setTransmissionMode(MODE_STREAM);
            response = msg(MSG200);
        } else if ("B".equals(stru)) {
            getCtx().setTransmissionMode(MODE_BLOCK);
            response = msg(MSG200);
        } else if ("C".equals(stru)) {
            response = msg(MSG504);
        } else if ("Z".equals(stru)) {
            // TODO see http://java.sun.com/j2se/1.4.2/docs/api/java/util/zip/Deflater.html
            getCtx().setAttribute(ATTR_MODE_Z, Boolean.TRUE);
            response = msg(MSG200);
        } else {
            response = msg(MSG501);
        }
        out(response);

    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the transfer mode";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
