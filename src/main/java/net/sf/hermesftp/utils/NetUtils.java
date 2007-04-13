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

    /**
     * Checks if the passed IP complies to a given pattern.
     * 
     * @param ipTemplateList String list of patterns that may contain wild cards, such as:
     *            192.168.*.*, 127.0.0.1, !85.0.0.0
     * @param ip The IP address to check.
     * @return True, if the passed IP address matches at least one of the patterns.
     */
    public static boolean checkIPMatch(String ipTemplateList, String ip) {
        String[] chk = ip.split("\\.");
        if (chk.length != 4) {
            throw new IllegalArgumentException("Illegal IP address: " + ip);
        }

        boolean inverse = false;
        String[] ipTemplateArr = ipTemplateList.split(",");
        for (int i = 0; i < ipTemplateArr.length; i++) {
            String t;
            if (ipTemplateArr[i].trim().startsWith("!")) {
                t = ipTemplateArr[i].substring(1).trim();
                inverse = true;
            } else {
                t = ipTemplateArr[i].trim();
            }
            String[] tmpl = t.split("\\.");
            boolean match = true;
            for (int j = 0; j < tmpl.length; j++) {
                match &= ("*".equals(tmpl[j].trim())) || (tmpl[j].trim().equals(chk[j].trim())) ^ inverse;
            }
            if (match) {
                return true;
            }
        }

        return false;

    }

}
