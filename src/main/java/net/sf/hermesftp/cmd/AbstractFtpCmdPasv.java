package net.sf.hermesftp.cmd;

import java.io.IOException;

import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.exception.FtpIllegalProtocolVersion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Behnke
 */
public abstract class AbstractFtpCmdPasv extends AbstractFtpCmd {

    private static Log log = LogFactory.getLog(AbstractFtpCmdPasv.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        try {

            /* Set up socket provider */
            getCtx().closeSockets();
            DataChannelInfo info = null;
            SocketProvider socketProvider = new PassiveModeSocketProvider(getCtx(), getPreferredProtocol());
            info = socketProvider.init();
            getCtx().setDataSocketProvider(socketProvider);

            /* Send connection parameters */
            out(createResponseMessage(info.getProtocolIdx(), info.getAddress(), info.getPort()));

            /*
             * Connecting the client (ServerSocket.accetp()) is deferred until data channel is
             * needed.
             */

        } catch (FtpIllegalProtocolVersion e) {
            log.error(e.toString());
            msgOut(MSG522);
        } catch (IOException e) {
            log.error(e.toString());
            msgOut(MSG425);
        } catch (RuntimeException e) {
            log.error(e.toString());
            msgOut(MSG501);
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }
    
    /**
     * Returns the preferred protocol version (1=IPv4, 2=IPv6, 0=undefined).
     * 
     * @return The protocol version.
     */
    protected abstract int getPreferredProtocol();


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
