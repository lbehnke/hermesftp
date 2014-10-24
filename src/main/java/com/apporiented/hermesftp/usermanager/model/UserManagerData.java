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

package com.apporiented.hermesftp.usermanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data Model for the user manager configuration.
 * 
 * @author Lars Behnke
 */
public class UserManagerData {

    private GroupDataList  groupDataList;

    private List<UserData> userData;

    /**
     * Getter method for the java bean <code>groupData</code>.
     * 
     * @return Returns the value of the java bean <code>groupData</code>.
     */
    public GroupDataList getGroupData() {
        if (groupDataList == null) {
            groupDataList = new GroupDataList();
        }
        return groupDataList;
    }

    /**
     * Getter method for the java bean <code>userData</code>.
     * 
     * @return Returns the value of the java bean <code>userData</code>.
     */
    public List<UserData> getUserData() {
        if (userData == null) {
            userData = Collections.synchronizedList(new ArrayList<UserData>());
        }
        return userData;
    }

    /**
     * Returns the user object by the username.
     * 
     * @param username The user's name.
     * @return The user object.
     */
    public UserData getUserData(String username) {
        username = username.trim();
        for (UserData user : getUserData()) {
            if (user.getUid().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Returns the group object by the group name.
     * 
     * @param groupname The group's name.
     * @return The group object.
     */
    public GroupData getGroupData(String groupname) {
        return groupDataList.getGroup(groupname.trim());
    }

}
