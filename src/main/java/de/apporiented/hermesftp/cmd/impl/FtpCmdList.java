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
import de.apporiented.hermesftp.utils.IOUtils;

/**
 * <b>LIST (LIST)</b>
 * <p>
 * This command causes a list to be sent from the server to the passive DTP. If the pathname
 * specifies a directory or other group of files, the server should transfer a list of files in the
 * specified directory. If the pathname specifies a file then the server should send current
 * information on the file. A null argument implies the user's current working or default directory.
 * The data transfer is over the data connection in type ASCII or type EBCDIC. (The user must ensure
 * that the TYPE is appropriately ASCII or EBCDIC). Since the information on a file may vary widely
 * from system to system, this information may be hard to use automatically in a program, but may be
 * quite useful to a human user.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdList extends AbstractFtpCmdList {

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Show directory contents";
    }

    /**
     * Lists the contents of the current working directory if no arugment was passed. Or
     * information/contents about the passed file/path.
     * <p>
     * Example directory in unix mode:
     * 
     * <pre>
     *                  drw-rw-rw-   1 user     group           0 Jan 21 19:53 .
     *                  drw-rw-rw-   1 user     group           0 Jan 21 19:53 ..
     *                  -rw-rw-rw-   1 user     group       76806 Dec 20 15:17 FILELOAD.pdf
     *                  drw-rw-rw-   1 user     group           0 Jan 21 16:29 rr
     *                  -rw-rw-rw-   1 user     group     5037072 Jan 19 20:40 spybotsd14.exe
     *                  -rw-rw-rw-   1 user     group        4951 Dec 20 15:18 vvs01_486400002bcd.pdf
     * </pre>
     * 
     * Filezilla:
     * 
     * <pre>
     *                  drwxr-xr-x 1 ftp ftp              0 Apr 22 11:46 hermesftp-0.3
     *                  -rw-r--r-- 1 ftp ftp           2327 Mar 29 14:04 slapd.conf
     * </pre>
     * 
     * @param out The output stream.
     * @param file The file
     * @param ctx The FTP context.
     * @throws IOException Error on data transfer.
     */
    protected void doPrintFileInfo(PrintWriter out, File file, FtpSessionContext ctx) throws IOException {
        int permission = getCtx().getPermission(file.getAbsolutePath());
        boolean read = (permission & PRIV_READ) > 0;
        boolean write = (permission & PRIV_WRITE) > 0;
        String s = IOUtils.formatUnixFtpFileInfo(file, read, write);
        out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
