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

import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
import de.apporiented.hermesftp.exception.FtpCmdException;

/**
 * <b>PROTECTION BUFFER SIZE (PBSZ)</b>
 * <p>
 * The argument is a decimal integer representing the maximum size, in bytes, of the encoded data
 * blocks to be sent or received during file transfer. This number shall be no greater than can be
 * represented in a 32-bit unsigned integer.
 * <p>
 * This command allows the FTP client and server to negotiate a maximum protected buffer size for
 * the connection. There is no default size; the client must issue a PBSZ command before it can
 * issue the first PROT command.
 * <p>
 * The PBSZ command must be preceded by a successful security data exchange.
 * <p>
 * If the server cannot parse the argument, or if it will not fit in 32 bits, it should respond with
 * a 501 reply code.
 * <p>
 * If the server has not completed a security data exchange with the client, it should respond with
 * a 503 reply code.
 * <p>
 * Otherwise, the server must reply with a 200 reply code. If the size provided by the client is too
 * large for the server, it must use a string of the form "PBSZ=number" in the text part of the
 * reply to indicate a smaller buffer size. The client and the server must use the smaller of the
 * two buffer sizes if both buffer sizes are specified.
 * <p>
 * <i>[Excerpt from RFC-2228, Horowitz and Lunt]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdPbsz extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        Boolean ssl = (Boolean) getCtx().getAttribute(ATTR_SSL);
        if (ssl == null || !ssl) {
            msgOut(MSG503);
        } else {
            msgOut(MSG200_PBSZ, new Object[] {0});
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the maximum size of the encode data blocks during file transfer.";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
