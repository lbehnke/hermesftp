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

package com.apporiented.hermesftp.session;

import com.apporiented.hermesftp.common.FtpSessionContext;
import com.apporiented.hermesftp.parser.FtpCmdReader;

/**
 * Interface for FTP session implementations.
 * 
 * @author Lars Behnke
 */
public interface FtpSession extends Runnable {

    /**
     * Sets the FTP Context, that contains all the shared settings of a FTP session.
     * 
     * @param ctx The FTP context.
     */
    void setFtpContext(FtpSessionContext ctx);

    /**
     * Returns the FTP Context, that contains all the shared settings of a FTP session.
     * 
     * @return ctx The FTP context.
     */
    FtpSessionContext getFtpContext();

    /**
     * Sets the object providing the client's commands. The bean is set by the Spring Framework.
     * 
     * @param reader The FTP command reader thread.
     */
    void setCmdReader(FtpCmdReader reader);

    /**
     * Indicates whether the session is still alive or has been terminated.
     * 
     * @return True, if the session is alive.
     */
    boolean isTerminated();

    /**
     * Starts the client session.
     */
    void start();

    /**
     * Aborts the client session.
     */
    void abort();

}
