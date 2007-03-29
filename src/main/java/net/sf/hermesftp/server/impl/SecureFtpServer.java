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

package net.sf.hermesftp.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ResourceBundle;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.server.AbstractFtpServer;
import net.sf.hermesftp.session.impl.FtpSessionContextImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/**
 * Implementation of a FTP server that supports communication via implicit SSL. Note that implicit
 * SSL is not covered by any RFC.
 *
 * @author Lars Behnke
 *
 */
public class SecureFtpServer
    extends AbstractFtpServer {

    private static Log log = LogFactory.getLog(SecureFtpServer.class);

    /**
     * {@inheritDoc}
     */
    protected FtpSessionContext createFtpContext() {
        FtpSessionContext ctx = new FtpSessionContextImpl(getOptions(),
                                                          getUserManager(),
                                                          ResourceBundle.getBundle(getResources()),
                                                          this);
        ctx.setAttribute(ATTR_SSL, Boolean.TRUE);
        return ctx;
    }

    /**
     * {@inheritDoc}
     */
    protected ServerSocket createServerSocket() throws IOException, FtpConfigException {
        SSLContext sslContext = getOptions().getSslContext();
        int sslPort = getOptions().getImplicitSslPort();
        SSLServerSocketFactory factory = (SSLServerSocketFactory) sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(sslPort);
        enableCipherSuites(sslServerSocket);
        log.info("Enabled cipher suites (implicit SSL): "
            + StringUtils.arrayToCommaDelimitedString(sslServerSocket.getEnabledCipherSuites()));
        return sslServerSocket;
    }

    private void enableCipherSuites(SSLServerSocket sslServerSocket) {
        String[] cipherSuites = getOptions().getStringArray(OPT_SSL_CIPHER_SUITES, null);
        if (cipherSuites != null) {
            if (cipherSuites.length == 1 && "*".equals(cipherSuites[0])) {
                sslServerSocket.setEnabledCipherSuites(sslServerSocket.getSupportedCipherSuites());
            } else {
                sslServerSocket.setEnabledCipherSuites(cipherSuites);
            }
        }
    }

}
