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
    
    public void testMatchIP() {
        String ip = "127.0.0.1";
          
        /* Check no match */
        assertFalse(NetUtils.checkIPMatch("85.*.*.*", ip));
        assertFalse(NetUtils.checkIPMatch("127.0.0.2", ip));
        assertFalse(NetUtils.checkIPMatch("*.1.*.*", ip));
        assertFalse(NetUtils.checkIPMatch("127.1", ip));
        assertFalse(NetUtils.checkIPMatch("85.*.*.*,!127.0.0.1", ip));
        assertFalse(NetUtils.checkIPMatch("127.0.0.2,!127.0.0.*", ip));
        assertFalse(NetUtils.checkIPMatch("*.1.*.*,!127.*.*.*", ip));
  
        /* Check match */
        assertTrue(NetUtils.checkIPMatch("85.*.*.*,127.0.0.1", ip));
        assertTrue(NetUtils.checkIPMatch("127.0.0.2,127.0.0.*", ip));
        assertTrue(NetUtils.checkIPMatch("*.1.*.*,127.*.*.*", ip));
        assertTrue(NetUtils.checkIPMatch("127.1,127,0", ip));


    }
    
}
