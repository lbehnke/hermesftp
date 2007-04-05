
package net.sf.hermesftp.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility methods that facilitate networking.
 * 
 * @author Lars Behnke
 * 
 */
public final class NetUtils {

    private static Log log = LogFactory.getLog(NetUtils.class);

    /**
     * Hidden constructor.
     */
    private NetUtils() {
        super();
    }

    /**
     * Returns the machine's network address.
     * 
     * @return The ip address.
     */
    public static InetAddress getMachineAddress() {
        InetAddress result = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                InetAddress ia = getMachineAddress(ni);
                if (ia != null) {
                    result = ia;
                }
            }
        } catch (SocketException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * Returns the network address of a particular network interface.
     * 
     * @param ni The network interface.
     * @return The machine address of a particular network interface.
     */
    public static InetAddress getMachineAddress(NetworkInterface ni) {
        Enumeration addrs = ni.getInetAddresses();
        InetAddress mAddr = null;
        while (addrs.hasMoreElements()) {
            InetAddress addr = (InetAddress) addrs.nextElement();
            if (addr.isSiteLocalAddress()) {
                mAddr = addr;
            }
        }
        return mAddr;
    }

    /**
     * Checks if SSL is available.
     * 
     * @return True, if SSL is available.
     */
    public static boolean isSSLAvailable() {
        try {
            Class.forName("com.sun.net.ssl.internal.ssl.Provider");
            log.info("JSSE installed.");
            return true;
        } catch (ClassNotFoundException e) {
            log.error("JSSE is NOT installed correctly!");
            return false;
        }

    }
    

}
