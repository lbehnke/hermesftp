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

package net.sf.hermesftp.cmd;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

/**
 * Ancestor command class for subclasses that support SSL.
 *
 * @author Lars Behnke
 */
public abstract class AbstractFtpCmdSsl
    extends AbstractFtpCmd {

    /**
     * Enables the configured cipher suites in the passed socket.
     *
     * @param sslSocket The socket.
     */
    protected void enableCipherSuites(SSLSocket sslSocket) {
        String[] cipherSuites = getCtx().getOptions().getStringArray(OPT_SSL_CIPHER_SUITES, null);
        if (cipherSuites != null) {
            if (cipherSuites.length == 1 && WILDCARD.equals(cipherSuites[0])) {
                sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            } else {
                sslSocket.setEnabledCipherSuites(cipherSuites);
            }
        }
    }

    /**
     * Enables the configured cipher suites in the passed server socket.
     *
     * @param sslServerSocket The server socket.
     */
    protected void enableCipherSuites(SSLServerSocket sslServerSocket) {
        String[] cipherSuites = getCtx().getOptions().getStringArray(OPT_SSL_CIPHER_SUITES, null);
        if (cipherSuites != null) {
            if (cipherSuites.length == 1 && WILDCARD.equals(cipherSuites[0])) {
                sslServerSocket.setEnabledCipherSuites(sslServerSocket.getSupportedCipherSuites());
            } else {
                sslServerSocket.setEnabledCipherSuites(cipherSuites);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
