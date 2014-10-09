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

package de.apporiented.hermesftp.cmd;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import de.apporiented.hermesftp.common.FtpSessionContext;
import de.apporiented.hermesftp.exception.FtpCmdException;
import de.apporiented.hermesftp.utils.IOUtils;

/**
 * Abstract precursor for commands that handle LIST or NLST.
 * 
 * @author Lars Behnke
 */
public abstract class AbstractFtpCmdList extends AbstractFtpCmd {

    // private static Log log = LogFactory.getLog(AbstractFtpCmdList.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        msgOut(MSG150);
        String charset = getCtx().getCharset();
        PrintWriter dataOut = null;
        Socket dataSocket = null;
        try {
            dataSocket = getCtx().getDataSocketProvider().provideSocket();
            dataOut = new PrintWriter(new OutputStreamWriter(dataSocket.getOutputStream(), charset));

            String args = getArguments();
            String[] argParts = args.split(" ");

            /* Ignore server specific extension to RFC 959 such as LIST -la */
            File dir;
            if (argParts[0].trim().startsWith("-")) {
                dir = new File(getAbsPath(""));
            } else {
                dir = new File(getPathArg());
            }

            // TODO Allow filtering with wildcards *, ?

            if (!dir.exists()) {

                // lab
                dataSocket.shutdownOutput();
                msgOut(MSG550);
                return;
            }

            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                dataOut.println("total " + files.length);

                for (int i = 0; i < files.length; i++) {
                    doPrintFileInfo(dataOut, files[i], getCtx());
                }
            } else {
                doPrintFileInfo(dataOut, dir, getCtx());
            }

            // lab
            dataOut.flush();
            dataSocket.shutdownOutput();

            msgOut(MSG226);
        } catch (IOException e) {
            msgOut(MSG550);
        } catch (Exception e) {
            msgOut(MSG550);
        } finally {
            IOUtils.closeGracefully(dataOut);
            getCtx().closeSockets();
        }
    }

    /**
     * Prints information about a single file or directory.
     * 
     * @param out The output stream.
     * @param file The file.
     * @param ctx The FTP context.
     * @throws IOException Error on data transfer.
     */
    protected abstract void doPrintFileInfo(PrintWriter out, File file, FtpSessionContext ctx)
            throws IOException;
}
