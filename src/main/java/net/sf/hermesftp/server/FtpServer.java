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

package net.sf.hermesftp.server;

import java.util.Date;
import java.util.List;

import net.sf.hermesftp.common.FtpEventListener;
import net.sf.hermesftp.common.FtpServerOptions;
import net.sf.hermesftp.usermanager.UserManager;

/**
 * @author Lars Behnke
 * 
 */
public interface FtpServer extends Runnable {

    /**
     * Returns the number of FTP connections currently active.
     * 
     * @return The number of connections.
     */
    int getConnectionCount();

    /**
     * Maximum number of connections since server has been started.
     * 
     * @return The highwater mark.
     */
    int getConnectionCountHWMark();

    /**
     * Date when number of connections reached its maximum since server has been started.
     * 
     * @return The highwater mark date.
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
    List getSessions();

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
