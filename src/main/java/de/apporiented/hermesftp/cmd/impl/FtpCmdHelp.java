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

package de.apporiented.hermesftp.cmd.impl;

import java.util.ArrayList;
import java.util.List;

import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
import de.apporiented.hermesftp.cmd.CmdListAware;
import de.apporiented.hermesftp.cmd.FtpCmd;
import de.apporiented.hermesftp.exception.FtpCmdException;

/**
 * <b>HELP (HELP)</b>
 * <p>
 * This command shall cause the server to send helpful information regarding its implementation
 * status over the control connection to the user. The command may take an argument (e.g., any
 * command name) and return more specific information as a response. The reply is type 211 or 214.
 * It is suggested that HELP be allowed before entering a USER command. The server may use this
 * reply to specify site-dependent parameters, e.g., in response to HELP SITE.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdHelp extends AbstractFtpCmd implements CmdListAware {

    private List<FtpCmd> cmdList;

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {

        StringBuffer sb = new StringBuffer();
        sb.append(msg(MSG214)).append("\n");
        sb.append("214-");
        for (FtpCmd cmd : getCmdList()) {
            sb.append(cmd.getToken());
            if (cmd instanceof FtpCmdNotImpl) {
                sb.append("*");
            }
            sb.append(" ");
        }
        sb.append("\n214 ");
        out(sb.toString());
    }

    private List<FtpCmd> getCmdList() {
        if (cmdList == null) {
            cmdList = new ArrayList<FtpCmd>();
        }
        return cmdList;
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Prints help information";
    }

    /**
     * {@inheritDoc}
     */
    public void setList(List<FtpCmd> cmdList) {
        this.cmdList = cmdList;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return false;
    }

}
