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
import java.util.List;

/**
 * Represents the configuration for a particular user; Constructor.
 */
public class UserData {

    private String       uid;

    private String       fullName;

    private String       password;

    private List<String> groupNames;

    private String       dir;

    private boolean      adminRole;

    /**
     * Getter method for the java bean <code>uid</code>.
     * 
     * @return Returns the value of the java bean <code>uid</code>.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Setter method for the java bean <code>uid</code>.
     * 
     * @param uid The value of uid to set.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Getter method for the java bean <code>password</code>.
     * 
     * @return Returns the value of the java bean <code>password</code>.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter method for the java bean <code>password</code>.
     * 
     * @param password The value of password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter method for the java bean <code>fullName</code>.
     * 
     * @return Returns the value of the java bean <code>fullName</code>.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Setter method for the java bean <code>fullName</code>.
     * 
     * @param fullName The value of fullName to set.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Getter method for the java bean <code>groupNames</code>.
     * 
     * @return Returns the value of the java bean <code>groupNames</code>.
     */
    public List<String> getGroupNames() {
        if (groupNames == null) {
            groupNames = new ArrayList<String>();
        }
        return groupNames;
    }

    /**
     * Adds the name of a group the user belongs to.
     * 
     * @param name The group name.
     */
    public void addGroupName(String name) {
        getGroupNames().add(name);
    }

    /**
     * Setter method for the java bean <code>groupNames</code>.
     * 
     * @param groupNames The value of groupNames to set.
     */
    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    /**
     * Getter method for the java bean <code>dir</code>.
     * 
     * @return Returns the value of the java bean <code>dir</code>.
     */
    public String getDir() {
        return dir;
    }

    /**
     * Setter method for the java bean <code>dir</code>.
     * 
     * @param dir The value of dir to set.
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * Getter method for the java bean <code>adminRole</code>.
     * 
     * @return Returns the value of the java bean <code>adminRole</code>.
     */
    public boolean isAdminRole() {
        return adminRole;
    }

    /**
     * Setter method for the java bean <code>adminRole</code>.
     * 
     * @param adminRole The value of adminRole to set.
     */
    public void setAdminRole(boolean adminRole) {
        this.adminRole = adminRole;
    }

}
