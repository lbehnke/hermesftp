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

package de.apporiented.hermesftp.common;

import java.io.File;

/**
 * <code>Eventlistener</code> that is implemented by classes that are to be informed by FTP
 * command objects on certain events.
 * 
 * @author Lars Behnke
 */
public interface FtpEventListener {

    /**
     * The event is called after a file has been uploaded by a client.
     * 
     * @param file The uploaded file.
     * @param clientId The unique ID of a client.
     */
    void uploadPerformed(String clientId, File file);

    /**
     * The event is called after a file has been downloaded by a client.
     * 
     * @param file The downloaded file.
     * @param clientId The unique ID of a client.
     */
    void downloadPerformed(String clientId, File file);

    /**
     * Number of failed long attempts.
     * 
     * @param clientId The unique ID of a client.
     * @param successful True, if the authentication was successful.
     */
    void loginPerformed(String clientId, boolean successful);

    /**
     * Method is called after a client session has been opened.
     * 
     * @param sessionObj The closed session object.
     */
    void sessionOpened(Object sessionObj);

    /**
     * Method is called after a client session has been closed.
     * 
     * @param sessionObj The closed session object.
     */
    void sessionClosed(Object sessionObj);

}
