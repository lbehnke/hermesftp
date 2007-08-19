/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
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

package net.sf.hermesftp.cmd.impl;

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;

/**
 * <b>REPRESENTATION TYPE (TYPE)</b>
 * <p>
 * The argument specifies the representation type as described in the Section on Data Representation
 * and Storage. Several types take a second parameter. The first parameter is denoted by a single
 * Telnet character, as is the second Format parameter for ASCII and EBCDIC; the second parameter
 * for local byte is a decimal integer to indicate Bytesize. The parameters are separated by a
 * (Space, ASCII code 32). The following codes are assigned for type:
 * <ul>
 * <li>A - ASCII (N - Non-print, T - Telnet format effectors, C - Carriage Control (ASA))
 * <li> E - EBCDIC (N - Non-print, T - Telnet format effectors, C - Carriage Control (ASA))
 * <li>I - Image
 * <li>L &lt;byte size&gt; - Local byte Byte size
 * </ul>
 * The default representation type is ASCII Non-print. If the Format parameter is changed, and later
 * just the first argument is changed, Format then returns to the Non-print default.
 * <p>
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]. See RFC 959, 3.1.1 for Type descriptions..</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdType extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String arg = getArguments().trim().toUpperCase();
        if (arg == null || arg.length() == 0) {
            msgOut(MSG501);
        } else if (arg.startsWith("A")) {
            writeResponse(DT_ASCII);
        } else if (arg.startsWith("E")) {
            writeResponse(DT_EBCDIC);
        } else if (arg.startsWith("I")) {
            writeResponse(DT_BINARY);
        } else {
            msgOut(MSG501);
        }
    }

    private void writeResponse(int dataType) {
        getCtx().setDataType(dataType);
        String typeName = TYPE_NAMES[dataType];
        msgOut(MSG200_TYPE, new Object[] {typeName});
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the data type (ASCII, EBCDIC, BINARY)";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
