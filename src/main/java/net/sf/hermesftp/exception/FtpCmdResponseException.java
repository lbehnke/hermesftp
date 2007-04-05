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

package net.sf.hermesftp.exception;

/**
 * Exception that is generated by FTP commands. The response has not been sent to the client when
 * the exception was thrown. The response string is encoded in the message string and should be sent
 * to the client as soon as the message is catched.
 * 
 * @author Lars Behnke
 */
public class FtpCmdResponseException extends FtpCmdException {

    private static final long serialVersionUID = -3445315223145170575L;

    /**
     * Constructor.
     */
    public FtpCmdResponseException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message The message to display.
     */
    public FtpCmdResponseException(String message) {
        super(message);
    }

}
