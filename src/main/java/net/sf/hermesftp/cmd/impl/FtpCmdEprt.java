/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
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

package net.sf.hermesftp.cmd.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.hermesftp.cmd.AbstractFtpCmdPort;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>The EPRT Command (EPRT)</b>
 * <p>
 * The EPRT command allows for the specification of an extended address for the
 * data connection. The extended address MUST consist of the network protocol as
 * well as the network and transport addresses. The format of EPRT is:
 * </p>
 * 
 * <pre>
 *       EPRT&lt;space&gt;&lt;d&gt;&lt;net-prt&gt;&lt;d&gt;&lt;net-addr&gt;&lt;d&gt;&lt;tcp-port&gt;&lt;d&gt;
 * </pre>
 * 
 * <p>
 * The EPRT command keyword MUST be followed by a single space (ASCII 32).
 * Following the space, a delimiter character (&lt;d&gt;) MUST be specified. The
 * delimiter character MUST be one of the ASCII characters in range 33-126
 * inclusive. The character "|" (ASCII 124) is recommended unless it coincides
 * with a character needed to encode the network address.
 * </p>
 * <p>
 * The &lt;net-prt&gt; argument MUST be an address family number defined by IANA
 * in the latest Assigned Numbers RFC (RFC 1700 [RP94] as of the writing of this
 * document). This number indicates the protocol to be used (and, implicitly,
 * the address length). This document will use two of address family numbers
 * from [RP94] as examples, according to the following table:
 * </p>
 * 
 * <pre>
 *       AF Number   Protocol
 *       ---------   --------
 *       1           Internet Protocol, Version 4 [Pos81a]
 *       2           Internet Protocol, Version 6 [DH96]
 * </pre>
 * 
 * <p>
 * The &lt;net-addr&gt; is a protocol specific string representation of the
 * network address. For the two address families specified above (AF Number 1
 * and 2), addresses MUST be in the following format:
 * </p>
 * 
 * <pre>
 *       AF Number   Address Format      Example
 *       ---------   --------------      -------
 *       1           dotted decimal      132.235.1.2
 *       2           IPv6 string         1080::8:800:200C:417A
 *       representations
 *       defined in [HD96]
 * </pre>
 * 
 * <p>
 * The &lt;tcp-port&gt; argument must be the string representation of the number
 * of the TCP port on which the host is listening for the data connection. The
 * following are sample EPRT commands:
 * 
 * <pre>
 *       EPRT |1|132.235.1.2|6275|
 *      
 *       EPRT |2|1080::8:800:200C:417A|5282|
 * </pre>
 * 
 * <p>
 * The first command specifies that the server should use IPv4 to open a data
 * connection to the host "132.235.1.2" on TCP port 6275. The second command
 * specifies that the server should use the IPv6 network protocol and the
 * network address "1080::8:800:200C:417A" to open a TCP data connection on port
 * 5282.
 * </p>
 * <p>
 * Upon receipt of a valid EPRT command, the server MUST return a code of 200
 * (Command OK). The standard negative error code 500 and 501 [PR85] are
 * sufficient to handle most errors (e.g., syntax errors) involving the EPRT
 * command. However, an additional error code is needed. The response code 522
 * indicates that the server does not support the requested network protocol.
 * The interpretation of this new error code is:
 * </p>
 * 
 * <pre>
 *       5yz Negative Completion
 *       x2z Connections
 *       xy2 Extended Port Failure - unknown network protocol
 * </pre>
 * 
 * <p>
 * The text portion of the response MUST indicate which network protocols the
 * server does support. If the network protocol is unsupported, the format of
 * the response string MUST be:
 * </p>
 * 
 * <pre>
 *       &lt;text stating that the network protocol is unsupported&gt; \
 *       (prot1,prot2,...,protn)
 * </pre>
 * 
 * <p>
 * Both the numeric code specified above and the protocol information between
 * the characters '(' and ')' are intended for the software automata receiving
 * the response; the textual message between the numeric code and the '(' is
 * intended for the human user and can be any arbitrary text, but MUST NOT
 * include the characters '(' and ')'. In the above case, the text SHOULD
 * indicate that the network protocol in the EPRT command is not supported by
 * the server. The list of protocols inside the parenthesis MUST be a comma
 * separated list of address family numbers. Two example response strings
 * follow: Network protocol not supported, use (1) Network protocol not
 * supported, use (1,2)
 * </p>
 * <p>
 * <i>[Excerpt from RFC-2428, Allman]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdEprt extends AbstractFtpCmdPort {

    private static final String EPRT_ARG_PATTERN = "^(.)([12])\\1([A-Fa-f0-9\\.\\:]+)\\1([0-9]*)\\1$";

    private static Log          log              = LogFactory
                                                         .getLog(FtpCmdEprt.class);

    private static Pattern      pattern;

    private int                 port;

    private String              addr;

    private int                 protocolIdx;

    private String              lastArgs;

    static {
        pattern = Pattern.compile(EPRT_ARG_PATTERN);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets port for active transfer.";
    }

    /**
     * {@inheritDoc}
     */
    protected String doReadIPAddr(String args) {
        if (!paramsParsed(args)) {
            parseParams(args);
        }
        return addr;
    }

    /**
     * {@inheritDoc}
     */
    protected int doReadPort(String args) {
        if (!paramsParsed(args)) {
            parseParams(args);
        }
        return port;
    }

    /**
     * {@inheritDoc}
     */
    protected int doReadProtocolIdx(String args) {
        if (!paramsParsed(args)) {
            parseParams(args);
        }
        return protocolIdx;
    }

    private boolean paramsParsed(String args) {
        return lastArgs != null && !lastArgs.equals(args);
    }

    private void parseParams(String args) {
        lastArgs = args;
        Matcher matcher = pattern.matcher(args);
        if (matcher.matches()) {
            /* CHECKSTYLE:OFF */
            int protocolIdx = Integer.parseInt(matcher.group(2));
            String ipAddr = matcher.group(3);
            int port = Integer.parseInt(matcher.group(4));
            log.debug("Protocol: " + protocolIdx + ", IPAddr: " + ipAddr
                    + ", Port: " + port);
            this.addr = ipAddr;
            this.port = port;
            this.protocolIdx = protocolIdx;
            /* CHECKSTYLE:ON */
        } else {
            throw new IllegalArgumentException("Invalid arguments: " + args);
        }
    }

}
