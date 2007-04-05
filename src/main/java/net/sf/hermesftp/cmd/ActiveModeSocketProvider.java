package net.sf.hermesftp.cmd;

import java.io.IOException;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.utils.IOUtils;

/**
 * Provider for the client socket (active transfer). The creation of the socket is deferred until it
 * is needed.
 * 
 * @author Behnke
 */
public class ActiveModeSocketProvider implements SocketProvider {

    private FtpSessionContext ctx;

    private DataChannelInfo   dataChannelInfo;

    Socket                    socket;

    public ActiveModeSocketProvider(FtpSessionContext ctx, DataChannelInfo info) {
        this.ctx = ctx;
        this.dataChannelInfo = info;
    }

    public DataChannelInfo init() throws IOException {
        closeSocket();
        return dataChannelInfo;
    }
    
    public Socket provideSocket() throws IOException {
        if (socket == null) {
            socket = createClientSocket();
        }
        return socket;
    }

    public void closeSocket() {
        if (socket != null) {
            IOUtils.closeGracefully(socket);
            socket = null;
        }

    }

    private Socket createClientSocket() throws IOException {
        Socket dataSocket;
        Boolean dataProtection = (Boolean) ctx.getAttribute(FtpConstants.ATTR_DATA_PROT);
        boolean ssl = dataProtection != null && dataProtection.booleanValue();
        if (ssl) {
            SSLSocketFactory factory;
            try {
                factory = ctx.getOptions().getSslContext().getSocketFactory();
            } catch (FtpConfigException e) {
                throw new IOException("Setting up SSL failed.");
            }
            SSLSocket sslSocket = (SSLSocket) factory.createSocket(dataChannelInfo.getAddress(), dataChannelInfo.getPort());
            sslSocket.setUseClientMode(false);
            enableCipherSuites(sslSocket);
            dataSocket = sslSocket;
        } else {
            dataSocket = SocketFactory.getDefault().createSocket(dataChannelInfo.getAddress(), dataChannelInfo.getPort());
        }
        return dataSocket;
    }

    /**
     * Enables the configured cipher suites in the passed socket.
     * 
     * @param sslSocket The socket.
     */
    private void enableCipherSuites(SSLSocket sslSocket) {
        String[] cipherSuites = ctx.getOptions().getStringArray(FtpConstants.OPT_SSL_CIPHER_SUITES, null);
        if (cipherSuites != null) {
            if (cipherSuites.length == 1 && FtpConstants.WILDCARD.equals(cipherSuites[0])) {
                sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            } else {
                sslSocket.setEnabledCipherSuites(cipherSuites);
            }
        }
    }



}
