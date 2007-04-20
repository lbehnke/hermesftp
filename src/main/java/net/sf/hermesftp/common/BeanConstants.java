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

package net.sf.hermesftp.common;

/**
 * Holds the spring bean names as constants.
 *
 * @author Lars Behnke
 *
 */
public interface BeanConstants {

    /** Bean that represents a FTP session. */
    public static final String BEAN_SESSION = "session";

    /** Bean that represents the FTP server. */
    public static final String BEAN_SERVER = "server";

    /** Bean that represents the FTP server (SSL). */
    public static final String BEAN_SSL_SERVER = "sslserver";

    /** Bean that represents the FTP console. */
    public static final String BEAN_CONSOLE    = "console";

}
