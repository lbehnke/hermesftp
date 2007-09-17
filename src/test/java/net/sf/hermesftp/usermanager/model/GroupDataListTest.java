/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
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

package net.sf.hermesftp.usermanager.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.sf.hermesftp.SpringUtil;
import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.usermanager.impl.XmlFileUserManager;

import org.junit.Before;
import org.junit.Test;


public class GroupDataListTest implements FtpConstants {

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
    public void testPermissions() {
        // CHECKSTYLE:OFF
        String root = "c:/test";
        GroupDataList groupList;
        try {
            String user = "user";
            groupList = userManager.getGroupDataList(user);
            assertEquals(PRIV_READ, groupList.getPermission("c:\\test", user,
                    root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission(
                    "c:\\test\\user", user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission(
                    "c:\\test\\user\\dir1\\dir2", user, root));
            assertEquals(PRIV_NONE, groupList.getPermission("c:\\test\\admin",
                    user, root));

            user = "admin";
            groupList = userManager.getGroupDataList(user);
            assertEquals(PRIV_READ_WRITE, groupList.getPermission("c:\\test",
                    user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission(
                    "c:\\test\\user", user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission(
                    "c:\\test\\user\\dir1\\dir2", user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission(
                    "c:\\test\\admin", user, root));

        } catch (FtpConfigException e) {
            fail(e.toString());
        }
        // CHECKSTYLE:ON
    }

}
