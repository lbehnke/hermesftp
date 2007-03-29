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
 * Generated on configuration errors.
 *
 * @author Lars Behnke
 *
 */
public class FtpConfigException 
    extends FtpException {

    private static final long serialVersionUID = -6212143412329656488L;

    /**
     * Constructor.
     */
    public FtpConfigException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message The error message.
     */
    public FtpConfigException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Root exception.
     */
    public FtpConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message.
     * @param cause The root exception.
     */
    public FtpConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    protected String getCategory() {
        return "Configuration";
    }
}
