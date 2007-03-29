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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * Security related utility methods.
 *
 * @author Lars Behnke
 *
 */
public final class SecurityUtil {

    private static final String ALG_END = "}";

    private static final String ALG_START = "{";

    /**
     * Constructor hidden.
     */
    private SecurityUtil() {
        super();
    }

    /**
     * Calculates based on the passed parameters an hash code and returns it as BASE64-String. The
     * used algorithm is prepended.
     *
     * @param password The password to encode.
     * @param algorithm The alogrithm to use (MD5 e.g.)
     * @return The encoded password as string.
     * @throws NoSuchAlgorithmException Passed alogrith is not supported.
     */
    public static String encodePassword(String password, String algorithm)
                                                                          throws NoSuchAlgorithmException {
        if (password == null) {
            throw new IllegalArgumentException("No password passed");
        }
        String result = password.trim();
        if (algorithm == null) {
            return result;
        }
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(password.getBytes());
        byte[] digestedPassword = digest.digest();
        String base64password = new String(Base64.encodeBase64(digestedPassword));
        return ALG_START + algorithm + ALG_END + base64password;
    }

    /**
     * Checks the validity of the password password based on a given hashcode. The hashcode to check
     * against contains the used algorithm has prefix. Example: {MD5}Cwz8B/yoHJVquRgdhXb0qA==.
     *
     * @param passwordHash The hash code to check against.
     * @param password The password to check.
     * @return True, if password is valid.
     * @throws NoSuchAlgorithmException Algorithm (from hash prefix) is not supported.
     */
    public static boolean checkPassword(String passwordHash, String password)
                                                                             throws NoSuchAlgorithmException {
        if (passwordHash == null || password == null) {
            return false;
        }
        String algorithm = null;
        int startIdx = passwordHash.indexOf(ALG_START);
        int endIdx = passwordHash.indexOf(ALG_END);
        if (startIdx == 0 && endIdx > startIdx) {
            algorithm = passwordHash.substring(startIdx + 1, endIdx);
            String hashStr = encodePassword(password.trim(), algorithm);
            return passwordHash.equals(hashStr);
        } else {
            return passwordHash.trim().equals(password.trim());
        }
    }

}
