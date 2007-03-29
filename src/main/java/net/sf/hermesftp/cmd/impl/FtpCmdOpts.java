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

import net.sf.hermesftp.cmd.AbstractFtpCmd;
import net.sf.hermesftp.exception.FtpCmdException;

/**
 * OPTS Command:
 * <p>
 * The OPTS (options) command allows a user-PI to specify the desired behavior of a server-FTP
 * process when another FTP command (the target command) is later issued. The exact behavior, and
 * syntax, will vary with the target command indicated, and will be specified with the definition of
 * that command. Where no OPTS behavior is defined for a particular command there are no options
 * available for that command.
 * <p>
 * Request Syntax:
 *
 * <pre>
 *            opts             = opts-cmd SP command-name
 *            [ SP command-options ] CRLF
 *            opts-cmd         = &quot;opts&quot;
 *            command-name     = &lt;any FTP command which allows option setting&gt;
 *            command-options  = &lt;format specified by individual FTP command&gt;
 * </pre>
 *
 * Response Syntax:
 *
 * <pre>
 *            opts-response    = opts-good / opts-bad
 *            opts-good        = &quot;200&quot; SP response-message CRLF
 *            opts-bad         = &quot;451&quot; SP response-message CRLF /
 *            &quot;501&quot; SP response-message CRLF
 *            response-message = *TCHAR
 * </pre>
 *
 * An "opts-good" response (200 reply) MUST be sent when the command- name specified in the OPTS
 * command is recognized, and the command- options, if any, are recognized, and appropriate. An
 * "opts-bad" response is sent in other cases. A 501 reply is appropriate for any permanent error.
 * That is, for any case where simply repeating the command at some later time, without other
 * changes of state, will also be an error. A 451 reply should be sent where some temporary
 * condition at the server, not related to the state of communications between user and server,
 * prevents the command being accepted when issued, but where if repeated at some later time, a
 * changed environment for the server-FTP process may permit the command to succeed. If the OPTS
 * command itself is not recognized, a 500 or 502 reply will, of course, result.
 * <p>
 * The OPTS command MUST be implemented whenever the FEAT command is implemented. Because of that,
 * there is no indication in the list of features returned by FEAT to indicate that the OPTS command
 * itself is supported. Neither the FEAT command, nor the OPTS command, have any optional
 * functionality, thus there are no "OPTS FEAT" or "OPTS OPTS" commands.
 * <p>
 * Security Considerations: No significant new security issues, not already present in the FTP
 * protocol, are believed to have been created by this extension. However, this extension does
 * provide a mechanism by which users can determine the capabilities of an FTP server, and from
 * which additional information may be able to be deduced. While the same basic information could be
 * obtained by probing the server for the various commands, if the FEAT command were not provided,
 * that method may reveal an attacker by logging the attempts to access various extension commands.
 * This possibility is not considered a serious enough threat to be worthy of any remedial action.
 *
 * The security of any additional features that might be reported by the FEAT command, and
 * manipulated by the OPTS command, should be addressed where those features are defined.
 * <p>
 * <i>[Excerpt from RFC-2389, Hethmon and Elz]</i>
 * </p>
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdOpts
    extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String response;
        String[] argParts = getArguments().split(" ");
        if (argParts.length == 2 && argParts[0].equalsIgnoreCase("UTF8")) {
            getCtx().setAttribute(ATTR_FORCE_UTF8,
                                  Boolean.valueOf(argParts[1].equalsIgnoreCase("ON")));
            response = msg(MSG200);
        } else {
            response = msg(MSG451);
        }
        out(response);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Specifies the desired behavior of the FTP server.";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
