package net.sf.hermesftp.cmd;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpCmdResponseException;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.utils.IOUtils;
import net.sf.hermesftp.utils.NetUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides the data transfer socket for transfer passive mode.
 * 
 * @author Behnke
 */
public class PassiveModeSocketProvider implements SocketProvider {

    private static final int  DATA_CHANNEL_TIMEOUT = 10000;

    private static Log        log                  = LogFactory.getLog(PassiveModeSocketProvider.class);

    private FtpSessionContext ctx;

    private ServerSocket      serverSocket;

    private Socket            dataSocket;

    private int               preferredProtocol;

    public PassiveModeSocketProvider(FtpSessionContext ctx, int preferredProtocol) {
        this.ctx = ctx;
        this.preferredProtocol = preferredProtocol;
    }

    /**
     * {@inheritDoc}
     */
    public DataChannelInfo init() throws IOException {

        /* Get local machine address and check protocol version. */
        InetAddress localIp = NetUtils.getMachineAddress();
        int currentProtocol = getProtocolIdxByAddr(localIp);
        boolean ok = ((preferredProtocol == currentProtocol) || (preferredProtocol == 0));
        if (!ok) {
            throw new IOException("Invalid IP version");
        }

        /* Get the next available port */
        int retries = 3;
        while (retries > 0) {
            Integer port = ctx.getNextPassivePort();
            port = port == null ? new Integer(0) : port;
            try {
                log.debug("Trying to bind server socket to port " + port + ".");
                serverSocket = createServerSocket(localIp, port.intValue());
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
     * @throws FtpConfigException
     * @throws IOException
     */
    private ServerSocket createServerSocket(InetAddress localIp, int port) throws FtpConfigException,
            IOException, FtpCmdResponseException {
        ServerSocket sock;
        Boolean dataProtection = (Boolean) ctx.getAttribute(FtpConstants.ATTR_DATA_PROT);
        boolean ssl = dataProtection != null && dataProtection.booleanValue();
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
