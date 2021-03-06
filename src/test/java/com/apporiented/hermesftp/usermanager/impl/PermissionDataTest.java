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

package com.apporiented.hermesftp.usermanager.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import com.apporiented.hermesftp.exception.FtpConfigException;
import com.apporiented.hermesftp.usermanager.model.PermissionData;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Behnke
 */
public class PermissionDataTest {

    private String ftproot;

    private String user;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        ftproot = new File(System.getProperty("user.home"), "hermesftp")
                .getAbsolutePath();
        user = "tester";
    }

    private String getDir(String relDir) {
        File dir = new File(ftproot, relDir);
        return FilenameUtils.normalizeNoEndSeparator(dir.getAbsolutePath());
    }

    /**
     * Tests the ant style path matching.
     */
    @Test
    public void testPathMatch() {
        // CHECKSTYLE:OFF
        PermissionData perm = new PermissionData();
        try {
            perm.setTemplate("${ftproot}/**");
            assertTrue(perm.matches(getDir("/test/test"), ftproot, user));
            assertTrue(perm.matches(getDir("/test"), ftproot, user));
            assertTrue(perm.matches(getDir("/"), ftproot, user));
            assertFalse(perm.matches("", ftproot, user));
            assertFalse(perm.matches(null, ftproot, user));

            perm.setTemplate("${ftproot}/**");
            assertTrue(perm.matches(getDir("/test/test"), ftproot, user));
            assertTrue(perm.matches(getDir("/test"), ftproot, user));
            assertTrue(perm.matches(getDir("/"), ftproot, user));

            perm.setTemplate("${ftproot}");
            assertFalse(perm.matches(getDir("/test/test"), ftproot, user));
            assertFalse(perm.matches(getDir("/test"), ftproot, user));
            assertTrue(perm.matches(getDir("/"), ftproot, user));

            perm.setTemplate("${ftproot}/users/${user}*");
            assertTrue(perm.matches(getDir("/users/tester"), ftproot, user));
            assertTrue(perm.matches(getDir("/users/tester2"), ftproot, user));

        } catch (FtpConfigException e) {
            fail(e.toString());
        }
        // CHECKSTYLE:ON
    }

}
