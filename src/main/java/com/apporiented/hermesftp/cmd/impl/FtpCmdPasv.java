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

package com.apporiented.hermesftp.cmd.impl;

import com.apporiented.hermesftp.cmd.AbstractFtpCmdPasv;

/**
 * <b>PASSIVE (PASV)</b>
 * <p>
 * This command requests the server-DTP to "listen" on a data port (which is not its default data
 * port) and to wait for a connection rather than initiate one upon receipt of a transfer command.
 * The response to this command includes the host and port address this server is listening on.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdPasv extends AbstractFtpCmdPasv {

    /**
     * {@inheritDoc}
     */
    protected int getPreferredProtocol() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    protected String createResponseMessage(int protocolIdx, String ip, int port) {
        StringBuffer addrPort = new StringBuffer();
        String[] ipParts = ip.split("\\.");
        int idx = 0;
        addrPort.append(ipParts[idx++].trim()).append(SEPARATOR);
        addrPort.append(ipParts[idx++].trim()).append(SEPARATOR);
        addrPort.append(ipParts[idx++].trim()).append(SEPARATOR);
        addrPort.append(ipParts[idx++].trim()).append(SEPARATOR);
        int p1 = (port >> BYTE_LENGTH) & BYTE_MASK;
        int p2 = port & BYTE_MASK;
        addrPort.append(p1).append(SEPARATOR).append(p2);
        return msg(MSG227, new String[] {addrPort.toString()});
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Activates the passive transfer mode";
    }

}
