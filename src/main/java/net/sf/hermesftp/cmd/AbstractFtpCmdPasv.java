package net.sf.hermesftp.cmd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.exception.FtpCmdResponseException;
import net.sf.hermesftp.utils.NetUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Behnke
 */
public abstract class AbstractFtpCmdPasv extends AbstractFtpCmdSsl {

    private static final int DATA_CHANNEL_TIMEOUT = 10000;

    private static Log       log                  = LogFactory.getLog(AbstractFtpCmdPasv.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        try {
            if (getCtx().getPassiveModeServerSocket() != null) {
                getCtx().getPassiveModeServerSocket().close();
            }

            /* Get local machine address */
            InetAddress localIp = NetUtils.getMachineAddress();

            /* Get the next available port */
            ServerSocket sock = null;
            int retries = 3;
            while (retries > 0) {
                Integer port = getCtx().getNextPassivePort();
                port = port == null ? new Integer(0) : port;
                try {
                    log.debug("Trying to bind server socket to port " + port + ".");
                    sock = createServerSocket(localIp, port.intValue());
                    break;
                } catch (Exception e) {
                    retries--;
                    log.debug("Binding server socket to port " + port + " failed.");
                }
            }
            if (sock == null) {
                msgOut(MSG425);
                return;
            }

            getCtx().setPassiveModeServerSocket(sock);
            String ip = sock.getInetAddress().getHostAddress();
            int port = sock.getLocalPort();

            /* Send connection parameters */
            out(createResponseMessage(1, ip, port));

            /* Wait from the client to connect. */
            Socket clientModePassiveSocket = sock.accept();

            getCtx().setDataSocket(clientModePassiveSocket);
        } catch (IOException e) {
            log.error(e.toString());
            msgOut(MSG425);
        } catch (RuntimeException e) {
            log.error(e.toString());
            msgOut(MSG501);
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
    protected ServerSocket createServerSocket(InetAddress localIp, int port) throws FtpConfigException,
            IOException, FtpCmdResponseException {
        ServerSocket sock;
        Boolean dataProtection = (Boolean) getCtx().getAttribute(ATTR_DATA_PROT);
        if (dataProtection != null && dataProtection.booleanValue()) {
            SSLServerSocketFactory factory = getCtx().getOptions().getSslContext().getServerSocketFactory();
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
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

    /**
     * Returns the reponse string encoding ip address, port and protocol type. Example: 229 Entering
     * Extended Passive Mode (|||6000|).
     * 
     * @param protocolIdx The protocol index (1=IPv4, 2=IPv6).
     * @param ip The address.
     * @param port The port.
     * @return The string encoding the connection data in an appropriate format.
     */
    protected abstract String createResponseMessage(int protocol, String addr, int port);

}
