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
 * Exception generated when invalid command line arguments are passed.
 * 
 * @author Lars Behnke
 * 
 */
public class FtpCLIException
    extends FtpException {

    private static final long serialVersionUID = -3103326754939760429L;

    /**
     * Constructor.
     */
    public FtpCLIException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message The error message.
     */
    public FtpCLIException(String message) {
        super(message);
    }


    /**
     * {@inheritDoc}
     */
    protected String getCategory() {
        return "Command line interface";
    }

}
