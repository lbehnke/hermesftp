/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2014 Lars Behnke
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

package de.apporiented.hermesftp.usermanager.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import de.apporiented.hermesftp.SpringUtil;
import de.apporiented.hermesftp.common.FtpConstants;
import de.apporiented.hermesftp.exception.FtpConfigException;

import org.junit.Before;
import org.junit.Test;

/**
 * Reads a user configuration file and tests some permission requests.
 * 
 * @author Lars Behnke
 */
public class XmlFileUserManagerTest implements FtpConstants {

    private XmlFileUserManager userManager;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        userManager = (XmlFileUserManager) SpringUtil.getBean("userManager");
        userManager.load();
    }

    /**
     * Checks permissions of the default users.
     */
    @Test
    public void testAuthenticate() {

        try {
            assertTrue(userManager.authenticate("anonymous", "mail@online.com",
                    null));
            assertFalse(userManager.authenticate("anonymous", "wrong", null));
            assertFalse(userManager.authenticate("anonymous", null, null));

            assertTrue(userManager.authenticate("user", "user", null));
            assertFalse(userManager.authenticate("user", "wrong", null));
            assertFalse(userManager.authenticate("user", null, null));

            assertFalse(userManager.authenticate("unknownuser", null, null));

            /* Check MD5 password */
            assertTrue(userManager.authenticate("admin", "admin", null));
            assertFalse(userManager.authenticate("admin", "user", null));
            assertFalse(userManager.authenticate("admin",
                    "{MD5}ISMvKXpXpadDiUoOSoAfww==", null));

        } catch (FtpConfigException e) {
            fail();
        }

        // CHECKSTYLE:ON
    }

}
