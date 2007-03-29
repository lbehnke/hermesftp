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
 * Exception thrown by the FTP server when a client has reached quota limits. These limits can be
 * configured in the hermes context file.
 *
 * @author Lars Behnke
 *
 */
public class FtpQuotaException
    extends FtpException {

    private static final long serialVersionUID = -5875206764152032864L;

    /**
     * Constructor.
     */
    public FtpQuotaException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message The message to be displayed.
     */
    public FtpQuotaException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    protected String getCategory() {
        return "FTP Quota";
    }
}
