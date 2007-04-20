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

package net.sf.hermesftp.cmd.impl;

import net.sf.hermesftp.cmd.AbstractFtpCmdPasv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>The EPSV Command (EPSV)</b>
 * <p>
 * The EPSV command requests that a server listen on a data port and wait for a connection. The EPSV
 * command takes an optional argument. The response to this command includes only the TCP port
 * number of the listening connection. The format of the response, however, is similar to the
 * argument of the EPRT command. This allows the same parsing routines to be used for both commands.
 * In addition, the format leaves a place holder for the network protocol and/or network address,
 * which may be needed in the EPSV response in the future. The response code for entering passive
 * mode using an extended address MUST be 229. The interpretation of this code, according to [PR85]
 * is:
 * </p>
 * 
 * <pre>
 *   2yz Positive Completion
 *   x2z Connections
 *   xy9 Extended Passive Mode Entered
 * </pre>
 * 
 * <p>
 * The text returned in response to the EPSV command MUST be:
 * </p>
 * 
 * <pre>
 *   &lt;text indicating server is entering extended passive mode&gt; \
 *   (&lt;d&gt;&lt;d&gt;&lt;d&gt;&lt;tcp-port&gt;&lt;d&gt;)
 * </pre>
 * 
 * <p>
 * The portion of the string enclosed in parentheses MUST be the exact string needed by the EPRT
 * command to open the data connection, as specified above.
 * </p>
 * <p>
 * The first two fields contained in the parenthesis MUST be blank. The third field MUST be the
 * string representation of the TCP port number on which the server is listening for a data
 * connection. The network protocol used by the data connection will be the same network protocol
 * used by the control connection. In addition, the network address used to establish the data
 * connection will be the same network address used for the control connection. An example response
 * string follows:
 * </p>
 * 
 * <pre>
 *   Entering Extended Passive Mode (|||6446|)
 * </pre>
 * 
 * <p>
 * The standard negative error codes 500 and 501 are sufficient to handle all errors involving the
 * EPSV command (e.g., syntax errors). When the EPSV command is issued with no argument, the server
 * will choose the network protocol for the data connection based on the protocol used for the
 * control connection. However, in the case of proxy FTP, this protocol might not be appropriate for
 * communication between the two servers. Therefore, the client needs to be able to request a
 * specific protocol. If the server returns a protocol that is not supported by the host that will
 * be connecting to the port, the client MUST issue an ABOR (abort) command to allow the server to
 * close down the listening connection. The client can then send an EPSV command requesting the use
 * of a specific network protocol, as follows:
 * </p>
 * 
 * <pre>
 *   EPSV&lt;space&gt;&lt;net-prt&gt;
 * </pre>
 * 
 * <p>
 * If the requested protocol is supported by the server, it SHOULD use the protocol. If not, the
 * server MUST return the 522 error messages as outlined in section 2.
 * </p>
 * <p>
 * Finally, the EPSV command can be used with the argument "ALL" to inform Network Address
 * Translators that the EPRT command (as well as other data commands) will no longer be used. An
 * example of this command follows:
 * </p>
 * <p>
 * EPSV&lt;space&gt;ALL
 * </p>
 * <p>
 * Upon receipt of an EPSV ALL command, the server MUST reject all data connection setup commands
 * other than EPSV (i.e., EPRT, PORT, PASV, et al.). This use of the EPSV command is further
 * explained in section 4.
 * </p>
 * <p>
 * <i>[Excerpt from RFC-2428, Allman]</i>
 * </p>
 */
public class FtpCmdEpsv extends AbstractFtpCmdPasv {

    private static Log log = LogFactory.getLog(FtpCmdEpsv.class);
    
    /**
     * {@inheritDoc}
     */
    protected String createResponseMessage(int protocolIdx, String ip, int port) {
        return msg(MSG229, new String[] {"" + port});
    }

    
    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Activates the extended passive transfer mode";
    }


    /**
     * {@inheritDoc}
     */
    protected int getPreferredProtocol() {
        int result = 0;
        String args = getArguments();
        if (args != null) {
            try {
                result = Integer.parseInt(args.trim());
            } catch (Exception e) {
                log.warn("Invalid argument for EPSV: " + getArguments());
            }
        }
        return result;
    }


}
