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

package net.sf.hermesftp.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

/**
 * @author Behnke
 */
public class NetUtilsTest extends TestCase {

    public void testGetMachineAddr() {
        InetAddress addrObj = NetUtils.getMachineAddress(false);
        if (addrObj == null) {
            addrObj = NetUtils.getMachineAddress(true);
            assertEquals("127.0.0.1", addrObj.getHostAddress());
        } else {
            String addr = addrObj.getHostAddress();

            /* Check syntax */
            assertTrue(addr.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$"));

            /* We don't want the loopback address */
            assertNotSame("127.0.0.1", addr);
        }
    }

    public void testMatchIP() {
        Inet4Address ip;
        try {
            ip = (Inet4Address)Inet4Address.getLocalHost();
            /* Check no match */
            assertFalse(NetUtils.checkIPv4Match("85.*.*.*", ip));
            assertFalse(NetUtils.checkIPv4Match("127.0.0.2", ip));
            assertFalse(NetUtils.checkIPv4Match("*.1.*.*", ip));
            assertFalse(NetUtils.checkIPv4Match("127.1", ip));
            assertFalse(NetUtils.checkIPv4Match("85.*.*.*,!127.0.0.1", ip));
            assertFalse(NetUtils.checkIPv4Match("127.0.0.2,!127.0.0.*", ip));
            assertFalse(NetUtils.checkIPv4Match("*.1.*.*,!127.*.*.*", ip));

            /* Check match */
            assertTrue(NetUtils.checkIPv4Match("85.*.*.*,127.0.0.1", ip));
            assertTrue(NetUtils.checkIPv4Match("127.0.0.2,127.0.0.*", ip));
            assertTrue(NetUtils.checkIPv4Match("*.1.*.*,127.*.*.*", ip));
            assertTrue(NetUtils.checkIPv4Match("127.1,127,0", ip));
        } catch (UnknownHostException e) {
            fail("Cannot get local host address.");
        }



    }

}
