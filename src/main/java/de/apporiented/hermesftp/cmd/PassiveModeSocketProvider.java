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


package de.apporiented.hermesftp.cmd;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import de.apporiented.hermesftp.common.FtpConstants;
import de.apporiented.hermesftp.common.FtpSessionContext;
import de.apporiented.hermesftp.utils.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides the data transfer socket for transfer passive mode.
 * 
 * @author Behnke
 */
public class PassiveModeSocketProvider implements SocketProvider {

    private static final int  MAX_BIND_RETRIES     = 3;

    private static final int  DATA_CHANNEL_TIMEOUT = 10000;

    private static Log        log                  = LogFactory.getLog(PassiveModeSocketProvider.class);

    private FtpSessionContext ctx;

    private ServerSocket      serverSocket;

    private Socket            dataSocket;

    private int               preferredProtocol;

    /**
     * Constructor.
     * 
     * @param ctx Session context.
     * @param preferredProtocol Preferred protocol (IPv4 or IPv6)
     */
    public PassiveModeSocketProvider(FtpSessionContext ctx, int preferredProtocol) {
        this.ctx = ctx;
        this.preferredProtocol = preferredProtocol;
    }

    /**
     * {@inheritDoc}
     */
    public DataChannelInfo init() throws IOException {

        /* Get local machine address and check protocol version. */
        InetAddress localIp = ctx.getClientSocket().getLocalAddress();
        int currentProtocol = getProtocolIdxByAddr(localIp);
        boolean ok = (preferredProtocol == currentProtocol) || (preferredProtocol == 0);
        if (!ok) {
            throw new IOException("Invalid IP version");
        }

        /* Get the next available port */
        int retries = MAX_BIND_RETRIES;
        while (retries > 0) {
            Integer port = ctx.getNextPassivePort();
            port = port == null ? new Integer(0) : port;
            try {
                log.debug("Trying to bind server socket to port " + port);
                serverSocket = createServerSocket(localIp, port);
                break;
            } catch (Exception e) {
                retries--;
                log.debug("Binding server socket to port " + port + " failed.");
            }
        }
        if (serverSocket == null) {
            throw new IOException("Initializing server socket failed.");
        }

        /* Wrap up connection parameter */
        log.debug("Server socket successfully bound to port " + serverSocket.getLocalPort() + ".");
        return new DataChannelInfo(localIp.getHostAddress(), serverSocket.getLocalPort());

    }

    /**
     * {@inheritDoc}
     */
    public void closeSocket() {
        IOUtils.closeGracefully(serverSocket);
        IOUtils.closeGracefully(dataSocket);
        serverSocket = null;
        dataSocket = null;

    }

    /**
     * {@inheritDoc}
     */
    public Socket provideSocket() throws IOException {
        if (dataSocket == null) {
            if (serverSocket == null) {
                throw new IOException("Server socket not initialized.");
            }
            dataSocket = serverSocket.accept();
        }
        return dataSocket;
    }

    private int getProtocolIdxByAddr(InetAddress addr) {
        if (addr instanceof Inet4Address) {
            return 1;
        } else if (addr instanceof Inet6Address) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Creates the server socket that accepts the data connection.
     * 
     * @param localIp The local IP address.
     * @param port The port.
     * @return The server socket.
     * @throws IOException Error on creating server socket.
     */
    private ServerSocket createServerSocket(InetAddress localIp, int port) throws IOException {
        ServerSocket sock;
        Boolean dataProtection = (Boolean) ctx.getAttribute(FtpConstants.ATTR_DATA_PROT);
        boolean ssl = dataProtection != null && dataProtection;
        if (ssl) {
            SSLServerSocketFactory factory = ctx.getOptions().getSslContext().getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(port, 1, localIp);
            sslServerSocket.setUseClientMode(false);
            enableCipherSuites(sslServerSocket);
            sock = sslServerSocket;
        } else {
            sock = ServerSocketFactory.getDefault().createServerSocket(port, 1, localIp);
        }
        sock.setSoTimeout(DATA_CHANNEL_TIMEOUT);
        return sock;
    }

    /**
     * Enables the configured cipher suites in the passed server socket.
     * 
     * @param sslServerSocket The server socket.
     */
    private void enableCipherSuites(SSLServerSocket sslServerSocket) {
        String[] cipherSuites = ctx.getOptions().getStringArray(FtpConstants.OPT_SSL_CIPHER_SUITES, null);
        if (cipherSuites != null) {
            if (cipherSuites.length == 1 && FtpConstants.WILDCARD.equals(cipherSuites[0])) {
                sslServerSocket.setEnabledCipherSuites(sslServerSocket.getSupportedCipherSuites());
            } else {
                sslServerSocket.setEnabledCipherSuites(cipherSuites);
            }
        }
    }
}
