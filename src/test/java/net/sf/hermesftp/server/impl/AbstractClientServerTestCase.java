/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
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

package net.sf.hermesftp.server.impl;

import java.io.IOException;

import junit.framework.TestCase;

import net.sf.hermesftp.SpringUtil;
import net.sf.hermesftp.client.FtpTestClient;
import net.sf.hermesftp.common.BeanConstants;
import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.server.FtpServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// CHECKSTYLE:OFF

/**
 * Abstract test case that sets up the communication infrastructure between FTP client and server.
 * 
 * @author Lars Behnke
 */
public abstract class AbstractClientServerTestCase extends TestCase implements FtpConstants, BeanConstants {

    private static final int SERVER_DELAY = 800;

    private static Log       log          = LogFactory.getLog(ServerRFC959Test.class);

    private FtpTestClient    client;

    static {
        log.info("Starting FTP servers...");
        startServer((FtpServer) SpringUtil.getBean("testServer"));
    }

    private static void startServer(FtpServer svr) {
        Thread svrThread = new Thread(svr);
        svrThread.start();
        while (svr.getStatus() != SERVER_STATUS_READY) {
            try {
                Thread.sleep(SERVER_DELAY);
            } catch (InterruptedException e) {
                log.error(e);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        try {
            getClient().openConnection(null, "user", "user");
            getClient().openPassiveMode();
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        getClient().closeConnection();
    }

    /**
     * Getter method for the java bean <code>client</code>.
     * 
     * @return Returns the value of the java bean <code>client</code>.
     * @throws IOException Error on initializing the FTP client.
     */
    protected FtpTestClient getClient() throws IOException {
        if (client == null) {
            client = new FtpTestClient();
        }
        return client;
    }

}

// CHECKSTYLE:ON

