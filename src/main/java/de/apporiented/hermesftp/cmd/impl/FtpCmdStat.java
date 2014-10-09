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

import java.io.File;
import java.util.Map;
import java.util.Set;

import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
import de.apporiented.hermesftp.exception.FtpCmdException;
import de.apporiented.hermesftp.utils.IOUtils;

/**
 * <b>STATUS (STAT)</b>
 * <p>
 * This command shall cause a status response to be sent over the control connection in the form of
 * a reply. The command may be sent during a file transfer (along with the Telnet IP and Synch
 * signals--see the Section on FTP Commands) in which case the server will respond with the status
 * of the operation in progress, or it may be sent between file transfers. In the latter case, the
 * command may have an argument field. If the argument is a pathname, the command is analogous to
 * the "list" command except that data shall be transferred over the control connection. If a
 * partial pathname is given, the server may respond with a list of file names or attributes
 * associated with that specification. If no argument is given, the server should return general
 * status information about the server FTP process. This should include current values of all
 * transfer parameters and the status of connections.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdStat extends AbstractFtpCmd {

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String clientHost = getCtx().getClientSocket().getInetAddress().getHostAddress();
        String msg = getCtx().getUser() + "(" + clientHost + ")";
        msgOut(MSG211_STAT, new Object[] {msg});
        Map<String, Long> map = getCtx().getUserManager().getUserStatistics(getCtx().getUser());
        String arg = getArguments();
        if (arg.length() == 0) {
            printUserStatistics(map);
        } else {
            File dir = new File(getPathArg());
            if (!dir.exists()) {
                msgOut(MSG550);
                return;
            }
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    doPrintFileInfo(files[i]);
                }
            } else {
                doPrintFileInfo(dir);
            }
        }
        out("211 ");
    }

    private void doPrintFileInfo(File file) {
        int permission = getCtx().getPermission(file.getAbsolutePath());
        boolean read = (permission & PRIV_READ) > 0;
        boolean write = (permission & PRIV_WRITE) > 0;
        out("211-" + IOUtils.formatUnixFtpFileInfo(file, read, write));

    }

    private void printUserStatistics(Map<String, Long> map) {
        Set<Map.Entry<String, Long>> entrySet = map.entrySet();
        for (Map.Entry<String, Long> entry : entrySet) {
            String statName = (String) entry.getKey();
            Long value = (Long) entry.getValue();
            printOutStats(statName, value);

        }

    }

    private void printOutStats(String statName, Long value) {
        long statValue = value == null ? 0 : value.longValue();
        out("211-" + statName + ": " + statValue);
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Returns short client session based statistic";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
