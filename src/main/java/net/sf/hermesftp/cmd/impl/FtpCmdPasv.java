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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import net.sf.hermesftp.cmd.AbstractFtpCmdSsl;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.utils.NetUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PASSIVE (PASV)
 * <p>
 * This command requests the server-DTP to "listen" on a data port (which is not its default data
 * port) and to wait for a connection rather than initiate one upon receipt of a transfer command.
 * The response to this command includes the host and port address this server is listening on.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdPasv
    extends AbstractFtpCmdSsl {

    private static final int DATA_CHANNEL_TIMEOUT = 10000;
    
    private static Log log = LogFactory.getLog(FtpCmdPasv.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        try {
            if (getCtx().getPassiveModeServerSocket() != null) {
                getCtx().getPassiveModeServerSocket().close();
            }
            InetAddress localIp = NetUtils.getMachineAddress();
            
            ServerSocket sock;
            Boolean dataProtection = (Boolean) getCtx().getAttribute(ATTR_DATA_PROT);
            if (dataProtection != null && dataProtection.booleanValue()) {
                SSLServerSocketFactory factory = getCtx().getOptions()
                                                         .getSslContext()
                                                         .getServerSocketFactory();
                SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(0,
                                                                                               1,
                                                                                               localIp);
                sslServerSocket.setUseClientMode(false);
                enableCipherSuites(sslServerSocket);
                sock = sslServerSocket;
            } else {
                sock = ServerSocketFactory.getDefault().createServerSocket(0, 1, localIp);
            }
            getCtx().setPassiveModeServerSocket(sock);
            String ip = sock.getInetAddress().getHostAddress();
            int port = sock.getLocalPort();
            String addrPort = createPasvString(ip, port);
            msgOut(MSG227, new Object[] {addrPort.toString()});
            sock.setSoTimeout(DATA_CHANNEL_TIMEOUT);
            Socket clientModePassiveSocket = sock.accept();
            getCtx().setDataSocket(clientModePassiveSocket);
        } catch (IOException e) {
            log.error(e.toString());
            msgOut(MSG425);
        } catch (FtpConfigException e) {
            log.error(e.toString());
            msgOut(MSG425);
        }
    }

    private String createPasvString(String ip, int port) {
        StringBuffer addrPort = new StringBuffer();
        String[] ipParts = ip.split("\\.");
        int idx = 0;
        addrPort.append(ipParts[idx++].trim() + SEPARATOR);
        addrPort.append(ipParts[idx++].trim() + SEPARATOR);
        addrPort.append(ipParts[idx++].trim() + SEPARATOR);
        addrPort.append(ipParts[idx++].trim() + SEPARATOR);
        int p1 = (port >> BYTE_LENGTH) & BYTE_MASK;
        int p2 = port & BYTE_MASK;
        addrPort.append(p1 + SEPARATOR);
        addrPort.append(p2 + "");
        return addrPort.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Activates the passive transfer mode";
    }

}
