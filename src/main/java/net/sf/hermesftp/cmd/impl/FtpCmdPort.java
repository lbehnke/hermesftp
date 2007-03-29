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

import java.io.IOException;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import net.sf.hermesftp.cmd.AbstractFtpCmdSsl;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.exception.FtpCmdException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DATA PORT (PORT):
 * <p>
 * The argument is a HOST-PORT specification for the data port to be used in data connection. There
 * are defaults for both the user and server data ports, and under normal circumstances this command
 * and its reply are not needed. If this command is used, the argument is the concatenation of a
 * 32-bit internet host address and a 16-bit TCP port address. This address information is broken
 * into 8-bit fields and the value of each field is transmitted as a decimal number (in character
 * string representation). The fields are separated by commas. A port command would be: PORT
 * h1,h2,h3,h4,p1,p2 where h1 is the high order 8 bits of the internet host address.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 */
public class FtpCmdPort
    extends AbstractFtpCmdSsl {

    private static final String DOT = ".";

    private static Log log = LogFactory.getLog(FtpCmdPort.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        try {
            String[] args = getArguments().split(",");
            int idx = 0;
            String ip = args[idx++].trim() + DOT + args[idx++].trim() + DOT + args[idx++].trim()
                + DOT + args[idx++].trim();
            log.debug("IP: " + ip);
            int p1 = Integer.parseInt(args[idx++].trim()) & BYTE_MASK;
            int p2 = Integer.parseInt(args[idx++].trim()) & BYTE_MASK;
            int port = (p1 << BYTE_LENGTH) + p2;
            log.debug("Port: " + port);
            Socket dataSocket;
            Boolean dataProtection = (Boolean) getCtx().getAttribute(ATTR_DATA_PROT);
            if (dataProtection != null && dataProtection.booleanValue()) {
                SSLSocketFactory factory = getCtx().getOptions().getSslContext().getSocketFactory();
                SSLSocket sslSocket = (SSLSocket) factory.createSocket(ip, port);
                sslSocket.setUseClientMode(false);
                enableCipherSuites(sslSocket);
                dataSocket = sslSocket;
            } else {
                dataSocket = SocketFactory.getDefault().createSocket(ip, port);
            }
            getCtx().setDataSocket(dataSocket);
            if (isPassive()) {
                getCtx().getPassiveModeServerSocket().close();
                getCtx().setPassiveModeServerSocket(null);
            }
            msgOut(MSG200);
        } catch (IOException e) {
            log.error(e.toString());
            msgOut(MSG500);
        } catch (FtpConfigException e) {
            log.error(e.toString());
            msgOut(MSG500);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets port for active transfer.";
    }

}
