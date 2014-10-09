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

package de.apporiented.hermesftp.usermanager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.apporiented.hermesftp.common.FtpConstants;
import de.apporiented.hermesftp.exception.FtpConfigException;

/**
 * Model data of a user group including the configured limits and path permissions. Note that the
 * order of the configured permission entries is important, since the first path match provides the
 * permission value.
 * 
 * @author Lars Behnke
 */
public class GroupData {

    /**
     * Control code for unlimited.
     */
    public static final long     UNLIMITED = -1;

    private String               name;

    private Map<String, Long>    limits;

    private List<PermissionData> permissions;

    /**
     * Getter method for the java bean <code>limits</code>.
     * 
     * @return Returns the value of the java bean <code>limits</code>.
     */
    public Map<String, Long> getLimits() {
        if (limits == null) {
            limits = new HashMap<String, Long>();
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
    public List<PermissionData> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<PermissionData>();
        }
        return permissions;
    }

    /**
     * Returns the group permission on the passed path.
     * 
     * @param path The path to check.
     * @param ftproot The FTP root folder.
     * @param user The user's name.
     * @return The permission.
     * @throws FtpConfigException Error on reading or processing a configuration file.
     */
    public int getPermission(String path, String ftproot, String user) throws FtpConfigException {
        for (PermissionData permission : getPermissions()) {
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
