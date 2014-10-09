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
import java.io.IOException;
import java.io.PrintWriter;

import de.apporiented.hermesftp.cmd.AbstractFtpCmdList;
import de.apporiented.hermesftp.common.FtpSessionContext;

/**
 * <b>NAME LIST (NLST)</b>
 * <p>
 * This command causes a directory listing to be sent from server to user site. The pathname should
 * specify a directory or other system-specific file group descriptor; a null argument implies the
 * current directory. The server will return a stream of names of files and no other information.
 * The data will be transferred in ASCII or EBCDIC type over the data connection as valid pathname
 * strings separated by CRLF or NL. (Again the user must ensure that the TYPE is correct.) This
 * command is intended to return information that can be used by a program to further process the
 * files automatically. For example, in the implementation of a "multiple get" function.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdNlst extends AbstractFtpCmdList {

    // private static final Log log = LogFactory.getLog(FtpCmdNlst.class);

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Show names of files and directories";
    }

    /**
     * Prints out the name a single file or directory.
     * 
     * @param out The output stream.
     * @param file The file
     * @param ctx The FTP context.
     * @throws IOException Error on data transfer.
     */
    protected void doPrintFileInfo(PrintWriter out, File file, FtpSessionContext ctx) throws IOException {
        out.println(file.getName());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
