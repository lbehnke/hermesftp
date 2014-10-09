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

package de.apporiented.hermesftp.parser;

import java.net.SocketTimeoutException;

import de.apporiented.hermesftp.cmd.FtpCmd;
import de.apporiented.hermesftp.common.Abortable;
import de.apporiented.hermesftp.common.FtpSessionContext;
import de.apporiented.hermesftp.exception.FtpIllegalCmdException;

/**
 * @author Lars Behnke
 */
public interface FtpCmdReader extends Runnable, Abortable {

    /**
     * Sets the FTP Context of the session.
     * 
     * @param ctx The context
     */
    void setCtx(FtpSessionContext ctx);

    /**
     * Waits until another command is available.
     * 
     * @param timeout Timeout in milliseconds.
     * @return The next FtpCmd to handle.
     * @throws FtpIllegalCmdException Thrown if an unknown command token is encountered.
     * @throws SocketTimeoutException Timeout error.
     */
    FtpCmd waitForNextCommand(int timeout) throws FtpIllegalCmdException, SocketTimeoutException;

    /**
     * Getter method for the java bean <code>parser</code>.
     * 
     * @return Returns the value of the java bean <code>parser</code>.
     */
    FtpCmdParser getParser();

    /**
     * Setter method for the java bean <code>parser</code>.
     * 
     * @param parser The value of parser to set.
     */
    void setParser(FtpCmdParser parser);

    /**
     * Starts reading command string from the previously passed input stream.
     */
    void start();
}
