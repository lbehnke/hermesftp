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

package net.sf.hermesftp.usermanager;

import java.util.List;
import java.util.Map;

import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.exception.FtpQuotaException;
import net.sf.hermesftp.usermanager.model.GroupDataList;
import net.sf.hermesftp.usermanager.model.UserData;

/**
 * Generic description of user management classes.
 * 
 * @author Lars Behnke
 */
public interface UserManager {

    /**
     * Registers the current user's resource consumption. That is, downloaded or uploaded bytes or
     * files.
     * 
     * @param user The user name.
     * @param limitName The name of the consumption type (resource limit).
     * @param value The consumed resources.
     * @throws FtpQuotaException Thrown if resource limit has been reached.
     */
    void updateIncrementalStatistics(String user, String limitName, long value) throws FtpQuotaException;

    /**
     * Registers the current user's transfer rate. A mean value is calculated.
     * 
     * @param user The user name.
     * @param avgKeyName The key of the transfer rate (resource limit).
     * @param value The consumed resources.
     */
    void updateAverageStatistics(String user, String avgKeyName, long value);

    /**
     * Checks the resource consumption of the passed users. Only the passed limits are condidered.
     * 
     * @param user The user name.
     * @param limitNames The resource limits to condider.
     * @throws FtpQuotaException Thrown if at least one limit has been reached.
     */
    void checkResourceConsumption(String user, String[] limitNames) throws FtpQuotaException;

    /**
     * Returns the resource consumption statistics for a given user.
     * 
     * @param user The user.
     * @return The statistics.
     */
    Map getUserStatistics(String user);

    /**
     * Returns the logged statistics for aller user and all available dates (since the server was
     * started).
     * 
     * @return The statistics.
     */
    Map getAllStatistics();

    /**
     * Returns object representations of all registered users.
     * 
     * @param username The user's name.
     * @return The user data.
     * @throws FtpConfigException Error in configuration.
     */
    UserData getUserData(String username) throws FtpConfigException;

    /**
     * Returns object representations of all registered users.
     * 
     * @return The users.
     * @throws FtpConfigException Error in configuration.
     */
    List getUserDataList() throws FtpConfigException;

    /**
     * Returns object representations of all groups the passed user belongs to.
     * 
     * @param username The user's name.
     * @return The group data.
     * @throws FtpConfigException Error in configuration.
     */
    GroupDataList getGroupDataList(String username) throws FtpConfigException;

    /**
     * Validates the passed user credentials.
     * 
     * @param user The username.
     * @param password The password
     * @param ctx The context of the current session.
     * @return True, if credentials are valid.
     * @throws FtpConfigException Error on reading or processing a configuration file.
     */
    boolean authenticate(String user, String password, FtpSessionContext ctx) throws FtpConfigException;

    /**
     * (Re)loads the configuration.
     * 
     * @throws FtpConfigException Error on reading or processing a configuration file.
     */
    void load() throws FtpConfigException;

    /**
     * Checks if the configuration is loaded.
     * 
     * @return True, if configuration has already been loaded.
     * @throws FtpConfigException Error on reading or processing a configuration file.
     */
    boolean isLoaded() throws FtpConfigException;

}
