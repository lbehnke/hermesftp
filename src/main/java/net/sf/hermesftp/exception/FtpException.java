/*
 * ------------------------------
 * XML Flashcards
 * Copyright (c) 2006 Lars Behnke
 * ------------------------------
 *
 * This file is part of Hermes FTP Server.
 *
 * Hermes FTP Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sf.hermesftp.exception;

/**
 * Parent exception.
 * 
 * @author Lars Behnke
 * 
 */
public abstract class FtpException extends Exception {

    private static final long serialVersionUID = -7292242642192720520L;

    /**
     * Constructor.
     */
    public FtpException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message The error message.
     */
    public FtpException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param cause The cause.
     */
    public FtpException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * 
     * @param message The error message.
     * @param cause The cause.
     */
    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        String msg = getMessage() == null || getMessage().trim().length() == 0 ? "System error" : getMessage();
        return getCategory() + ": " + msg;
    }

    /**
     * Returns the error specific category. Must be overwritten by subclasses.
     * 
     * @return The error category.
     */
    protected abstract String getCategory();
}
