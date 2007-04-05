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

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.utils.IOUtils;

/**
 * Abstract precursor for commands that handle LIST or NLST.
 *
 * @author Lars Behnke
 *
 */
public abstract class AbstractFtpCmdList
    extends AbstractFtpCmd {

    // private static Log log = LogFactory.getLog(AbstractFtpCmdList.class);

    /**
     * {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        msgOut(MSG150);
        String charset = getCtx().getCharset();
        PrintWriter dataOut = null;
        try {
            Socket dataSocket = getCtx().getDataSocketProvider().provideSocket();
            dataOut = new PrintWriter(new OutputStreamWriter(dataSocket.getOutputStream(),
                                                                charset));
            
            String args = getArguments();
            String[] argParts = args.split(" ");
            
            /* Ignore server specific extension to RFC 959 such as LIST -la */
            File dir;
            if (argParts[0].trim().startsWith("-")) {
                dir = new File(getAbsPath(""));
            } else {
                dir = new File(getPathArg());
            }
            
            if (!dir.exists()) {
                msgOut(MSG550);
                return;
            }
            boolean empty = true;
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    doPrintFileInfo(dataOut, files[i], getCtx());
                    empty = false;
                }
            } else {
                doPrintFileInfo(dataOut, dir, getCtx());
                empty = false;
            }
            if (empty) {
               dataOut.println();
            }

            msgOut(MSG226);
        } catch (IOException e) {
            msgOut(MSG550);
        } catch (Exception e) {
            msgOut(MSG550);
        } finally {
            IOUtils.closeGracefully(dataOut);
            getCtx().getDataSocketProvider().closeSocket();
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
