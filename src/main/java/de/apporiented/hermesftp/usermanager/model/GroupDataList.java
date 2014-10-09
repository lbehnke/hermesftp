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
import java.util.Collections;
import java.util.List;

import de.apporiented.hermesftp.common.FtpConstants;
import de.apporiented.hermesftp.exception.FtpConfigException;

/**
 * Data of a user group.
 * 
 * @author Behnke
 */
public class GroupDataList {

    private List<GroupData> list = Collections.synchronizedList(new ArrayList<GroupData>());

    /**
     * Adds group data.
     * 
     * @param data group data.
     */
    public void addGroup(GroupData data) {
        list.add(data);
    }

    /**
     * Clears all group data from the list.
     */
    public void clear() {
        list.clear();
    }

    /**
     * Returns the group of a given name.
     * 
     * @param name The group name.
     * @return The group or null.
     */
    public GroupData getGroup(String name) {
        if (name == null) {
            return null;
        }
        for (GroupData data : list) {
            if (data.getName().equalsIgnoreCase(name)) {
                return data;
            }
        }
        return null;
    }

    /**
     * The upper limit of the constraints named by the passed key.
     * 
     * @param key The name of the constraint.
     * @return The value.
     */
    public long getUpperLimit(String key) {
        long limit = -1;
        for (GroupData data : list) {
            long l = data.getLimit(key);
            if (l < 0) {
                return l;
            }
            limit = Math.max(l, limit);

        }
        return limit;
    }

    /**
     * The permission on a given path.
     * 
     * @param path The path to check.
     * @param username The user that wants to access the path.
     * @param ftproot The absolute ftp root directory.
     * @return The permission constant.
     * @throws FtpConfigException Error in configuration.
     */
    public int getPermission(String path, String username, String ftproot) throws FtpConfigException {
        int result = FtpConstants.PRIV_NONE;
        for (GroupData groupData : list) {
            int permission = groupData.getPermission(path, ftproot, username);
            result = Math.max(result, permission);
        }
        return result;

    }

}
