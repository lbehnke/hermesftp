package net.sf.hermesftp.utils;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {

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
     * Quotes the special characters of a given regular expression string.
     * Example:
     * <blockquote>
     * <pre>
     * This.is.an.example  -->  This\.is\.an\.example
     * </pre>
     * </blockquote>
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
}
