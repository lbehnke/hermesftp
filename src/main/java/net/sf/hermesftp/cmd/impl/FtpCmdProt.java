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
 * <b>DATA CHANNEL PROTECTION LEVEL (PROT)</b>
 * <p>
 * The argument is a single Telnet character code specifying the data channel protection level.
 * <p>
 * This command indicates to the server what type of data channel protection the client and server
 * will be using. The following codes are assigned:
 * <ul>
 * <li> C - Clear
 * <li> S - Safe
 * <li> E - Confidential
 * <li> P - Private
 * </ul>
 * The default protection level if no other level is specified is Clear. The Clear protection level
 * indicates that the data channel will carry the raw data of the file transfer, with no security
 * applied. The Safe protection level indicates that the data will be integrity protected. The
 * Confidential protection level indicates that the data will be confidentiality protected. The
 * Private protection level indicates that the data will be integrity and confidentiality protected.
 * <p>
 * It is reasonable for a security mechanism not to provide all data channel protection levels. It
 * is also reasonable for a mechanism to provide more protection at a level than is required (for
 * instance, a mechanism might provide Confidential protection, but include integrity-protection in
 * that encoding, due to API or other considerations).
 * <p>
 * The PROT command must be preceded by a successful protection buffer size negotiation.
 * <p>
 * If the server does not understand the specified protection level, it should respond with reply
 * code 504.
 * <p>
 * If the current security mechanism does not support the specified protection level, the server
 * should respond with reply code 536.
 * <p>
 * If the server has not completed a protection buffer size negotiation with the client, it should
 * respond with a 503 reply code.
 * <p>
 * The PROT command will be rejected and the server should reply 503 if no previous PBSZ command was
 * issued.
 * <p>
 * If the server is not willing to accept the specified protection level, it should respond with
 * reply code 534.
 * <p>
 * If the server is not able to accept the specified protection level, such as if a required
 * resource is unavailable, it should respond with reply code 431.
 *
 * Otherwise, the server must reply with a 200 reply code to indicate that the specified protection
 * level is accepted. *
 * <p>
 * <i>[Excerpt from RFC-2228, Horowitz and Lunt]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdProt
    extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String level = getArguments().trim().toUpperCase();
        Boolean ssl = (Boolean) getCtx().getAttribute(ATTR_SSL);
        if (ssl == null || !ssl.booleanValue()) {
            msgOut(MSG536);
        } else if ("C".equals(level)) {
            getCtx().setAttribute(ATTR_DATA_PROT, Boolean.FALSE);
            msgOut(MSG200);
        } else if ("P".equals(level)) {
            getCtx().setAttribute(ATTR_DATA_PROT, Boolean.TRUE);
            msgOut(MSG200);
        } else if ("E".equals(level) || "S".equals(level)) {
            msgOut(MSG504);
        } else {
            msgOut(MSG501);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the protection level. See RFC 2228.";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
