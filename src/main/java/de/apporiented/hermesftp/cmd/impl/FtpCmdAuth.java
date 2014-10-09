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

import java.io.IOException;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
import de.apporiented.hermesftp.cmd.ClientSocketModifier;
import de.apporiented.hermesftp.exception.FtpCmdException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/**
 * <b>AUTHENTICATION/SECURITY MECHANISM (AUTH)</b>
 * <p>
 * The argument field is a Telnet string identifying a supported mechanism. This string is
 * case-insensitive. Values must be registered with the IANA, except that values beginning with "X-"
 * are reserved for local use.
 * <p>
 * If the server does not recognize the AUTH command, it must respond with reply code 500. This is
 * intended to encompass the large deployed base of non-security-aware ftp servers, which will
 * respond with reply code 500 to any unrecognized command. If the server does recognize the AUTH
 * command but does not implement the security extensions, it should respond with reply code 502.
 * <p>
 * If the server does not understand the named security mechanism, it should respond with reply code
 * 504.
 * <p>
 * If the server is not willing to accept the named security mechanism, it should respond with reply
 * code 534.
 * <p>
 * If the server is not able to accept the named security mechanism, such as if a required resource
 * is unavailable, it should respond with reply code 431.
 * <p>
 * If the server is willing to accept the named security mechanism, but requires security data, it
 * must respond with reply code 334.
 * <p>
 * If the server is willing to accept the named security mechanism, and does not require any
 * security data, it must respond with reply code 234.
 * <p>
 * If the server is responding with a 334 reply code, it may include security data as described in
 * the next section.
 * <p>
 * Some servers will allow the AUTH command to be reissued in order to establish new authentication.
 * The AUTH command, if accepted, removes any state associated with prior FTP Security commands. The
 * server must also require that the user reauthorize (that is, reissue some or all of the USER,
 * PASS, and ACCT commands) in this case (see section 4 of RFC2228 for an explanation of "authorize"
 * in this context).
 * 
 * @author Lars Behnke
 */
public class FtpCmdAuth extends AbstractFtpCmd implements ClientSocketModifier, HandshakeCompletedListener {

    private static Log log = LogFactory.getLog(FtpCmdAuth.class);

    private boolean    executed;

    /**
     * Some notes about SSL support: Use keytool to generate a keystore/key: <code>
     * keytool -genkey -alias behnke -keyalg DSA -keystore keystore -validity 365 -storepass secret -keypass secret
     * </code>
     * The attributes keypass and storepass must be equal! {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String prot = getArguments().toUpperCase().trim();
        if (!"SSL".equals(prot) && !"TLS".equals(prot)) {
            msgOut(MSG504);
            executed = true;
        }

        if (!getCtx().getOptions().getBoolean(OPT_SSL_ALLOW_EXPLICIT, true)) {
            msgOut(MSG534);
            executed = true;
        }

        if (!executed) {
            try {
                /* Create secure socket */
                SSLSocket sslSocket = createSslSocket();

                /* Send last response message in plain text */
                msgOut(MSG234, new Object[] {prot});

                /* From now on all communication is encrypted */
                getCtx().setClientSocket(sslSocket);

            } catch (IOException e) {
                msgOut(MSG431);
            }

            /* New authentication is required */
            getCtx().resetCredentials();
            getCtx().setAttribute(ATTR_SSL, Boolean.TRUE);
            executed = true;
        }
        synchronized (this) {
            notifyAll();
        }
    }

    private SSLSocket createSslSocket() throws IOException {
        String clientHost = getCtx().getClientSocket().getInetAddress().getHostAddress();
        SSLContext sslContext = getCtx().getOptions().getSslContext();
        SSLSocketFactory factory = (SSLSocketFactory) sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) factory.createSocket(getCtx().getClientSocket(), clientHost,
            getCtx().getOptions().getFtpPort(), true);
        sslSocket.setUseClientMode(false);
        sslSocket.addHandshakeCompletedListener(this);
        enableCipherSuites(sslSocket);
        log.info("Enabled cipher suites (explicit SSL): "
                + StringUtils.arrayToCommaDelimitedString(sslSocket.getEnabledCipherSuites()));
        return sslSocket;
    }

    /**
     * Enables the configured cipher suites in the passed socket.
     * 
     * @param sslSocket The socket.
     */
    private void enableCipherSuites(SSLSocket sslSocket) {
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
     * {@inheritDoc}
     */
    public boolean socketModified() {
        return executed;
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Initiates an explicit SSL/TLS connection. See RFC 2228.";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void handshakeCompleted(HandshakeCompletedEvent e) {
        log.debug("Explicit SSL handshake completed. Cipher suite: " + e.getCipherSuite());
    }

}
