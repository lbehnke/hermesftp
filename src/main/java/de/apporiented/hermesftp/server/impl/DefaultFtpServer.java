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

package de.apporiented.hermesftp.server.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ResourceBundle;

import javax.net.ServerSocketFactory;

import de.apporiented.hermesftp.common.FtpSessionContext;
import de.apporiented.hermesftp.server.AbstractFtpServer;
import de.apporiented.hermesftp.session.impl.FtpSessionContextImpl;

/**
 * FTP server implementation that handles unencrypted communication over the FTP port. The server
 * can switch to the SSL protocol after excplicit SSL is requested (AUTH command).
 * 
 * @author Lars Behnke
 */
public class DefaultFtpServer extends AbstractFtpServer {

    /**
     * {@inheritDoc}
     */
    protected ServerSocket createServerSocket() throws IOException {
        int port = getOptions().getFtpPort();
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
        return serverSocket;
    }

    /**
     * {@inheritDoc}
     */
    protected FtpSessionContext createFtpContext() {
        FtpSessionContext ctx = new FtpSessionContextImpl(getOptions(), getUserManager(), ResourceBundle
            .getBundle(getResources()), this);
        ctx.setAttribute(ATTR_SSL, Boolean.FALSE);
        return ctx;
    }
}
