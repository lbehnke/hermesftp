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
     * @param protocol The protocol index (1=IPv4, 2=IPv6).
     * @param addr The address.
     * @param port The port.
     * @return The string encoding the connection data in an appropriate format.
     */
    protected abstract String createResponseMessage(int protocol, String addr, int port);

}
