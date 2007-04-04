package net.sf.hermesftp.utils;

import java.net.InetAddress;

import junit.framework.TestCase;

public class NetUtilsTest extends TestCase {

    public void testGetMachineAddr() {
        InetAddress addrObj = NetUtils.getMachineAddress();
        String addr = addrObj.getHostAddress();

        /* Check syntax */
        assertTrue(addr.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$"));

        /* We don't want the loopback address */
        assertNotSame("127.0.0.1", addr);

    }
}
