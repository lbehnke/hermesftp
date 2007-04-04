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

package net.sf.hermesftp.server.impl;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// CHECKSTYLE:OFF

/**
 * Test case that covers RFC 959 functionality.
 *
 * @author Lars Behnke
 *
 */
public class ServerRFC2428Test
    extends AbstractClientServerTestCase {
	
	private static Log log = LogFactory.getLog(ServerRFC2428Test.class);

    /**
     * Test case: Rename and size.
     */
    public void testExtendedPort()  {
        String str;
        try {
            //str = getClient().sendAndReceive("EPRT");
            str = getClient().openExtendedPassiveMode();
            assertTrue(str.startsWith("229"));
        } catch (IOException e) {
            log.error(e);
            fail(e.toString());
        }
    }

    /**
     * Test case: Rename and size.
     */
    public void testExtendedPassivePort()  {
        String str;
        try {
//            str = getClient().sendAndReceive("EPSV");
//            assertTrue(str.startsWith("227"));
//            str = getClient().sendAndReceive("EPSV 1");
//            assertTrue(str.startsWith("227"));
//            str = getClient().sendAndReceive("EPSV ALL");
//            assertTrue(str.startsWith("227"));
        } catch (Exception e) {
            log.error(e);
            fail(e.toString());
        }
    }
}
// CHECKSTYLE:ON