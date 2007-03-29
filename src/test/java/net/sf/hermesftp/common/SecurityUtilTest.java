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

package net.sf.hermesftp.common;

import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import net.sf.hermesftp.utils.SecurityUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tests password encoding / decoding.
 *
 * @author Lars Behnke
 *
 */
public class SecurityUtilTest
    extends TestCase {

    private static final String TEST_PASSWORD = "xxxxxxxx";

    private static Log log = LogFactory.getLog(SecurityUtilTest.class);

    /**
     * Checks the encoding of some password using different algorithms.
     */
    public void testPasswordEncodeDecode() {
        try {
            checkPassword(TEST_PASSWORD, "MD5");
            checkPassword(TEST_PASSWORD, "SHA");
            checkPassword(TEST_PASSWORD, null);
        } catch (NoSuchAlgorithmException e) {
            fail("Algorithm not found");
        }

    }

    private void checkPassword(String pw, String alg) throws NoSuchAlgorithmException {
        String encodedPw;
        encodedPw = SecurityUtil.encodePassword(pw, alg);
        log.info("Encoding: " + pw + " -> " + encodedPw);
        assertTrue(SecurityUtil.checkPassword(encodedPw, pw));
    }
}
