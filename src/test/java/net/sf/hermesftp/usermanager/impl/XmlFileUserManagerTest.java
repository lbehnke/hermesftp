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

package net.sf.hermesftp.usermanager.impl;

import junit.framework.TestCase;
import net.sf.hermesftp.SpringUtil;
import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.exception.FtpConfigException;

/**
 * Reads a user configuration file and tests some permission requests.
 * 
 * @author Lars Behnke
 */
public class XmlFileUserManagerTest extends TestCase implements FtpConstants {

    private XmlFileUserManager userManager;


    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        userManager = (XmlFileUserManager) SpringUtil.getBean("userManager");
        userManager.load();
    }

    /**
     * Checks permissions of the default users.
     */
    public void testAuthenticate() {
        
        try {
            assertTrue (userManager.authenticate("anonymous", "mail@online.com", null));
            assertFalse (userManager.authenticate("anonymous", "wrong", null));
            assertFalse (userManager.authenticate("anonymous", null, null));
               
            assertTrue (userManager.authenticate("user", "user", null));
            assertFalse (userManager.authenticate("user", "wrong", null));
            assertFalse (userManager.authenticate("user", null, null));

            assertFalse (userManager.authenticate("unknownuser", null, null));

            /* Check MD5 password */
            assertTrue (userManager.authenticate("admin", "admin", null));
            assertFalse (userManager.authenticate("admin", "user", null));
            assertFalse (userManager.authenticate("admin", "{MD5}ISMvKXpXpadDiUoOSoAfww==", null));

        } catch (FtpConfigException e) {
            fail();
        }

        // CHECKSTYLE:ON
    }

}
