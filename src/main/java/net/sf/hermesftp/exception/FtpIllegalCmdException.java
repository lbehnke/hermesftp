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
 * Exception that is thrown by the command reader if an unrecognized command token is received from
 * the client.
 *
 * @author Lars Behnke
 *
 */
public class FtpIllegalCmdException
    extends FtpException {


    private static final long serialVersionUID = -1798752526640971366L;

    private String cmdLine;
    /**
     * Constructor.
     */
    public FtpIllegalCmdException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param cmdLine The invalid command line received from the client.
     */
    public FtpIllegalCmdException(String cmdLine) {
        super("Invalid command: " + cmdLine);
        this.cmdLine = cmdLine;
    }


    /**
     * Getter method for the java bean <code>cmdLine</code>.
     * @return Returns the value of the java bean <code>cmdLine</code>.
     */
    public String getCmdLine() {
        return cmdLine;
    }

    /**
     * {@inheritDoc}
     */
    protected String getCategory() {
        return "FTP command interpreter";
    }
}
