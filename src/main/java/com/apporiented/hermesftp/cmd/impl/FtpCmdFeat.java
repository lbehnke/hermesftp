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

package com.apporiented.hermesftp.cmd.impl;

import com.apporiented.hermesftp.cmd.AbstractFtpCmd;
import com.apporiented.hermesftp.exception.FtpCmdException;

/**
 * <b>FEAT Command</b>
 * <p>
 * It is not to be expected that all servers will necessarily support all of the new commands
 * defined in all future amendments to the FTP protocol. In order to permit clients to determine
 * which new commands are supported by a particular server, without trying each possible command,
 * one new command is added to the FTP command repertoire. This command requests the server to list
 * all extension commands, or extended mechanisms, that it supports. That is, all defined and
 * specified commands and features not defined in [1], or this document, must be included in the
 * FEAT command output in the form specified in the document that defines the extension.
 * <p>
 * User-FTP PIs must expect to see, in FEAT command responses, unknown features listed. This is not
 * an error, and simply indicates that the server-FTP implementor has seen, and implemented, the
 * specification of a new feature that is unknown to the user-FTP.
 * <p>
 * The FEAT command consists solely of the word "FEAT". It has no parameters or arguments.
 * <p>
 * Where a server-FTP process does not support the FEAT command, it will respond to the FEAT command
 * with a 500 or 502 reply. This is simply the normal "unrecognized command" reply that any unknown
 * command would elicit. Errors in the command syntax, such as giving parameters, will result in a
 * 501 reply. Server-FTP processes that recognize the FEAT command, but implement no extended
 * features, and therefore have nothing to report, SHOULD respond with the "no-features" 211 reply.
 * However, as this case is practically indistinguishable from a server-FTP that does not recognize
 * the FEAT command, a 500 or 502 reply MAY also be used. The "no-features" reply MUST NOT use the
 * multi-line response format, exactly one response line is required and permitted. Replies to the
 * FEAT command MUST comply with the following syntax. Text on the first line of the reply is free
 * form, and not interpreted, and has no practical use, as this text is not expected to be revealed
 * to end users. The syntax of other reply lines is precisely defined, and if present, MUST be
 * exactly as specified.
 * 
 * <pre>
 *            feat-response   = error-response / no-features / feature-listing
 *            no-features     = &quot;211&quot; SP *TCHAR CRLF
 *            feature-listing = &quot;211-&quot; *TCHAR CRLF
 *            1*( SP feature CRLF )
 *            &quot;211 End&quot; CRLF
 *            feature         = feature-label [ SP feature-parms ]
 *            feature-label   = 1*VCHAR
 *            feature-parms   = 1*TCHAR
 * </pre>
 * 
 * Note that each feature line in the feature-listing begins with a single space. That space is not
 * optional, nor does it indicate general white space. This space guarantees that the feature line
 * can never be misinterpreted as the end of the feature-listing, but is required even where there
 * is no possibility of ambiguity.
 * <p>
 * Each extension supported must be listed on a separate line to facilitate the possible inclusion
 * of parameters supported by each extension command. The feature-label to be used in the response
 * to the FEAT command will be specified as each new feature is added to the FTP command set. Often
 * it will be the name of a new command added, however this is not required. In fact it is not
 * required that a new feature actually add a new command. Any parameters included are to be
 * specified with the definition of the command concerned. That specification shall also specify how
 * any parameters present are to be interpreted.
 * <p>
 * The feature-label and feature-parms are nominally case sensitive, however the definitions of
 * specific labels and parameters specify the precise interpretation, and it is to be expected that
 * those definitions will usually specify the label and parameters in a case independent manner.
 * Where this is done, implementations are recommended to use upper case letters when transmitting
 * the feature response.
 * <p>
 * The FEAT command itself is not included in the list of features supported, support for the FEAT
 * command is indicated by return of a reply other than a 500 or 502 reply.
 * <p>
 * A typical example reply to the FEAT command might be a multiline reply of the form:
 * 
 * <pre>
 *            C&gt; feat
 *            S&gt; 211-Extensions supported:
 *            S&gt;  MLST size*;create;modify*;perm;media-type
 *            S&gt;  SIZE
 *            S&gt;  COMPRESSION
 *            S&gt;  MDTM
 *            S&gt; 211 END
 * </pre>
 * 
 * The particular extensions shown here are simply examples of what may be defined in other places,
 * no particular meaning should be attributed to them. Recall also, that the feature names returned
 * are not command names, as such, but simply indications that the server possesses some attribute
 * or other.
 * <p>
 * The order in which the features are returned is of no importance, server-FTP processes are not
 * required to implement any particular order, or even to consistently return the same order when
 * the command is repeated. FTP implementations which support FEAT MUST include in the response to
 * the FEAT command all properly documented FTP extensions beyond those commands and mechanisms
 * described in RFC959 [1], including any which existed before the existence of FEAT. That is, when
 * a client receives a FEAT response from an FTP server, it can assume that the only extensions the
 * server supports are those that are listed in the FEAT response.
 * <p>
 * User-FTP processes should, however, be aware that there have been several FTP extensions
 * developed, and in widespread use, prior to the adoption of this document and the FEAT command.
 * The effect of this is that an error response to the FEAT command does not necessarily imply that
 * those extensions are not supported by the server-FTP process. User-PIs should test for such
 * extensions individually if an error response has been received to the FEAT command.
 * <p>
 * While not absolutely necessary, a standard mechanism for the server- PI to inform the user-PI of
 * any features and extensions supported will help reduce unnecessary traffic between the user-PI
 * and server- PI as more extensions may be introduced in the future. If no mechanism existed for
 * this, a user-FTP process would have to try each extension in turn resulting in a series of
 * exchanges between the user-PI and server-PI. Apart from being possibly wasteful, this procedure
 * may not always be possible, as issuing of a command just to determine if it is supported or not
 * may have some effect that is not desired. *
 * <p>
 * <i>[Excerpt from RFC-2389, Hethmon and Elz]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdFeat extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {

        msgOut(MSG211_FEAT_HEADER);
        msgOut(MSG211_FEAT_ENTRY, "MDTM");
        msgOut(MSG211_FEAT_ENTRY, "REST STREAM");
        msgOut(MSG211_FEAT_ENTRY, "SIZE");
        msgOut(MSG211_FEAT_ENTRY, "UTF8");
        msgOut(MSG211_FEAT_ENTRY, "CLNT");
        msgOut(MSG211_FEAT_ENTRY, "PASV");
        if (getCtx().getOptions().getBoolean(OPT_SSL_ALLOW_EXPLICIT, true)) {
            msgOut(MSG211_FEAT_ENTRY, "AUTH SSL");
            msgOut(MSG211_FEAT_ENTRY, "AUTH TLS");
        }
        msgOut(MSG211_FEAT_ENTRY, "MODE Z");
        msgOut(MSG211_FEAT_FOOTER);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Prints the supported features of the FTP server";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return false;
    }

}
