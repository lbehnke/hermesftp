package net.sf.hermesftp.cmd;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.exception.FtpConfigException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract super class of commands setting up the data channel in passive mode (PASV, EPSV).
 * 
 * @author Behnke
 */
public abstract class AbstractFtpCmdPort extends AbstractFtpCmdSsl {

    private static Log log = LogFactory.getLog(AbstractFtpCmdPort.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        try {
            String args = getArguments();
            if (args.length() == 0) {
                msgOut(MSG501);
                return;
            }

            int protocolIdx = doReadProtocolIdx(args);
            String addr = doReadIPAddr(args);
            int port = doReadPort(args);
            log.debug("Data Channel Protocol: " + protocolIdx + ", IPAddr: " + addr + ", port: " + port);

            setupDataChannel(protocolIdx, addr, port);

            msgOut(MSG200);
        } catch (IOException e) {
            log.error(e.toString());
            msgOut(MSG500);
        } catch (FtpConfigException e) {
            log.error(e.toString());
            msgOut(MSG500);
        } catch (IllegalArgumentException e) {
            log.error(e.toString());
            msgOut(MSG501);
        }
    }

    /**
     * Sets up the data channel in active transfer mode. IPv4 and IPv6 are supported.
     * 
     * @param ipAddr IPv4 or IPv6 compliant address.
     * @param port The port.
     * @throws FtpConfigException
     * @throws IOException
     * @throws UnknownHostException
     */
    protected void setupDataChannel(int protocolIdx, String ipAddr, int port) throws FtpConfigException,
            IOException {
        Socket dataSocket;
        Boolean dataProtection = (Boolean) getCtx().getAttribute(ATTR_DATA_PROT);
        if (dataProtection != null && dataProtection.booleanValue()) {
            SSLSocketFactory factory = getCtx().getOptions().getSslContext().getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) factory.createSocket(ipAddr, port);
            sslSocket.setUseClientMode(false);
            enableCipherSuites(sslSocket);
            dataSocket = sslSocket;
        } else {
            dataSocket = SocketFactory.getDefault().createSocket(ipAddr, port);
        }
        getCtx().setDataSocket(dataSocket);
        if (isPassive()) {
            getCtx().getPassiveModeServerSocket().close();
            getCtx().setPassiveModeServerSocket(null);
        }
    }

    public boolean isAuthenticationRequired() {
        return true;
    }

    /**
     * Reads port from passed arguments.
     * 
     * @param args The arguments.
     * @return The port.
     */
    protected abstract int doReadPort(String args);

    /**
     * Reads the IPv4 or IPv6 compliant address from the passed arguments.
     * 
     * @param args The arguments.
     * @return The IP address.
     */
    protected abstract String doReadIPAddr(String args);

    /**
     * Reads the protocol index (1=IPv4, 2=IPv6) from the passed arguments.
     * 
     * @param args The arguments.
     * @return The protocol index.
     */
    protected abstract int doReadProtocolIdx(String args);

}
