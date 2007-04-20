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

package net.sf.hermesftp.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utitlities for handling strings.
 * 
 * @author Behnke
 */
public final class StringUtils {

    /**
     * Hidden Constructor.
     */
    private StringUtils() {
    }

    /**
     * Converts the passed list to a comma separated string.
     * 
     * @param list The list to be converted.
     * @return The string.
     */
    public static String convertLstToString(List list) {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String s = (String) iter.next();
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Validates the syntax of the passed email.
     * 
     * @param email The email.
     * @return True, if syntax ok.
     */
    public static boolean validateEmail(String email) {
        String patternStr = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@"
                + "[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        return Pattern.matches(patternStr, email);
    }

    /**
     * Quotes the special characters of a given regular expression string. Example: <blockquote>
     * 
     * <pre>
     *     This.is.an.example  --&gt;  This\.is\.an\.example
     * </pre>
     * 
     * </blockquote>
     * 
     * @param s The unencoded string.
     * @return The encoded string.
     */
    public static String encodeRegex(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (".-\\[]{}()".indexOf(c) > -1) {
                sb.append("\\");
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Parses a string encoded list of integer values. The list may include ranges. Example:
     * 
     * <pre>
     *    1000-1011,1022,1023,2000-3000
     * </pre>
     * 
     * @param portListStr Encoded list of integer values.
     * @return List of alle Integers defined by the passed list.
     */
    public static Integer[] parseIntegerList(String portListStr) {

        if (portListStr == null) {
            return null;
        }
        List portList = new ArrayList();
        String[] allowedPorts = portListStr.split(",");
        if (allowedPorts != null && allowedPorts.length > 0 && allowedPorts[0] != null
                && allowedPorts[0].trim().length() > 0) {
            for (int i = 0; i < allowedPorts.length; i++) {
                String[] portRange = allowedPorts[i].split("\\-");
                if (portRange.length > 1) {
                    int portMin = Integer.parseInt(portRange[0].trim());
                    int portMax = Integer.parseInt(portRange[1].trim());
                    for (int port = portMin; port <= portMax; port++) {
                        portList.add(new Integer(port));
                    }
                } else {
                    int port = Integer.parseInt(portRange[0].trim());
                    portList.add(new Integer(port));
                }
            }
        }
        return (Integer[]) portList.toArray(new Integer[portList.size()]);

    }
}
