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

package net.sf.hermesftp.usermanager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.exception.FtpConfigException;

/**
 * Model data of a user group including the configured limits and path permissions. Note that the
 * order of the configured permission entries is important, since the first path match provides the
 * permission value.
 *
 * @author Lars Behnke
 *
 */
public class GroupData {

    /**
     * Control code for unlimited.
     */
    public static final long UNLIMITED = -1;

    private String name;

    private Map limits;

    private List permissions;

    /**
     * Getter method for the java bean <code>limits</code>.
     *
     * @return Returns the value of the java bean <code>limits</code>.
     */
    public Map getLimits() {
        if (limits == null) {
            limits = new HashMap();
        }
        return limits;
    }

    /**
     * Convenience method for returning the limit specified by the passed name.
     *
     * @param name The limit name.
     * @return The value.
     */
    public long getLimit(String name) {
        Long limit = (Long) getLimits().get(name);
        if (limit == null || limit.longValue() == UNLIMITED) {
            return Long.MAX_VALUE;
        } else {
            return limit.longValue();
        }
    }

    /**
     * Getter method for the java bean <code>permissions</code>.
     *
     * @return Returns the value of the java bean <code>permissions</code>.
     */
    public List getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList();
        }
        return permissions;
    }

    /**
     * Returns the group permission on the passed path.
     *
     * @param path The path to check.
     * @param ftproot The FTP root folder.
     * @param user The username.
     * @return The permission.
     * @throws FtpConfigException Error on reading or processing a configuration file.
     */
    public int getPermission(String path, String ftproot, String user) throws FtpConfigException {
        for (Iterator iter = getPermissions().iterator(); iter.hasNext();) {
            PermissionData permission = (PermissionData) iter.next();
            if (permission.matches(path, ftproot, user)) {
                return permission.getPermission();
            }

        }
        return FtpConstants.PRIV_NONE;
    }

    /**
     * Getter method for the java bean <code>name</code>.
     *
     * @return Returns the value of the java bean <code>name</code>.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for the java bean <code>name</code>.
     *
     * @param name The value of name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

}
