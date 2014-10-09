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

package de.apporiented.hermesftp.exception;

/**
 * Exception that indicates the attempted violation of access rights.
 * 
 * @author Lars Behnke
 */
public class FtpPermissionException extends FtpException {

    private static final long serialVersionUID = -8321481119135048910L;

    /**
     * Constructor.
     */
    public FtpPermissionException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message The error message.
     */
    public FtpPermissionException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    protected String getCategory() {
        return "Security";
    }
}
