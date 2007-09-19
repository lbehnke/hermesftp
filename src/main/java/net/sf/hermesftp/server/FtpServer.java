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

package net.sf.hermesftp.server;

import java.util.Date;
import java.util.List;

import net.sf.hermesftp.common.FtpEventListener;
import net.sf.hermesftp.common.FtpServerOptions;
import net.sf.hermesftp.session.FtpSession;
import net.sf.hermesftp.usermanager.UserManager;

/**
 * Contract for FTP server implementations.
 * 
 * @author Lars Behnke
 */
public interface FtpServer extends Runnable {

    /**
     * Returns the name of the FTP server.
     * 
     * @return The name.
     */
    String getName();

    /**
     * Returns the number of FTP connections currently active.
     * 
     * @return The number of connections.
     */
    int getConnectionCount();

    /**
     * Maximum number of connections since server started.
     * 
     * @return The high water mark.
     */
    int getConnectionCountHWMark();

    /**
     * Date when number of connections reached its maximum.
     * 
     * @return The high water mark date.
     */
    Date getConnectionCountHWMarkDate();

    /**
     * Halts the server.
     */
    void abort();

    /**
     * Returns the server status.
     * 
     * @return The status code (0 = undefined, 1 = initializing, 2 = ready).
     */
    int getStatus();

    /**
     * Returns the list of active sessions.
     * 
     * @return The list.
     */
    List<FtpSession> getSessions();

    /**
     * Removes closed sessions from memory.
     */
    void cleanUpSessions();

    /**
     * Returns the application properties.
     * 
     * @return The properties.
     */
    FtpServerOptions getOptions();

    /**
     * Returns the user manager implementation.
     * 
     * @return The user manager.
     */
    UserManager getUserManager();

    /**
     * Adds an external listerner that wants to get informed about FTP events.
     * 
     * @param lstnr The listener.
     */
    void addFtpEventListener(FtpEventListener lstnr);

}
