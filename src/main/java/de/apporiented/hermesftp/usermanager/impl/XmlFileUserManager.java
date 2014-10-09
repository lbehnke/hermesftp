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

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.apporiented.hermesftp.common.FtpSessionContext;
import de.apporiented.hermesftp.exception.FtpConfigException;
import de.apporiented.hermesftp.exception.FtpQuotaException;
import de.apporiented.hermesftp.usermanager.UserManager;
import de.apporiented.hermesftp.usermanager.model.GroupData;
import de.apporiented.hermesftp.usermanager.model.GroupDataList;
import de.apporiented.hermesftp.usermanager.model.UserData;
import de.apporiented.hermesftp.usermanager.model.UserManagerData;
import de.apporiented.hermesftp.utils.SecurityUtil;
import de.apporiented.hermesftp.utils.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lars Behnke
 */
public class XmlFileUserManager implements UserManager {

    private static Log                     log                 = LogFactory.getLog(XmlFileUserManager.class);

    private XmlFileReader                  fileReader;

    private UserManagerData                userManagerData;

    private Map<String, Map<String, Long>> resourceConsumption = Collections
                                                                   .synchronizedMap(new HashMap<String, Map<String, Long>>());

    private DateFormat                     dateFormat          = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * {@inheritDoc}
     */
    public synchronized boolean authenticate(String user, String password, FtpSessionContext ctx)
            throws FtpConfigException {
        boolean result = false;
        UserData userData = userManagerData.getUserData(user);
        if (userData == null) {
            return result;
        }
        if (userData.getPassword() == null) {
            result = password != null && StringUtils.validateEmail(password);
        } else {
            try {
                result = SecurityUtil.checkPassword(userData.getPassword(), password);
            } catch (NoSuchAlgorithmException e) {
                throw new FtpConfigException("Algorithm not supported: " + userData.getPassword());
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized GroupDataList getGroupDataList(String username) throws FtpConfigException {
        UserData userData = userManagerData.getUserData(username);
        if (userData == null) {
            throw new FtpConfigException("User " + username + " not configured.");
        }
        GroupDataList groupList = new GroupDataList();
        for (String groupName : userData.getGroupNames()) {
            GroupData groupData = userManagerData.getGroupData(groupName);
            groupList.addGroup(groupData);
        }
        return groupList;

    }

    /**
     * {@inheritDoc}
     */
    public synchronized List<UserData> getUserDataList() throws FtpConfigException {
        return userManagerData.getUserData();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void load() throws FtpConfigException {
        userManagerData = fileReader.read();
    }

    /**
     * Getter method for the java bean <code>fileReader</code>.
     * 
     * @return Returns the value of the java bean <code>fileReader</code>.
     */
    public XmlFileReader getFileReader() {
        return fileReader;
    }

    /**
     * Setter method for the java bean <code>fileReader</code>.
     * 
     * @param fileReader The value of fileReader to set.
     */
    public void setFileReader(XmlFileReader fileReader) {
        this.fileReader = fileReader;
    }

    /**
     * {@inheritDoc}
     */
    public void checkResourceConsumption(String user, String[] limitNames) throws FtpQuotaException {
        Map<String, Long> userConsumptions = getUserStatistics(user);
        for (String limitName : userConsumptions.keySet()) {
            Long consumptionObj = userConsumptions.get(limitName);
            long consumption = consumptionObj == null ? 0 : consumptionObj.longValue();
            long limit;
            try {
                GroupDataList list = getGroupDataList(user);
                limit = list.getUpperLimit(limitName);
            } catch (FtpConfigException e) {
                log.error(e);
                limit = 0;
            }
            if (consumption >= limit) {
                throw new FtpQuotaException(createQuotaMessage(limitName, consumption, limit));
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void updateIncrementalStatistics(String user, String limitName, long value)
            throws FtpQuotaException {
        Map<String, Long> userConsumptions = getUserStatistics(user);
        Long consumptionObj = (Long) userConsumptions.get(limitName);
        long consumption = consumptionObj == null ? 0 : consumptionObj.longValue();
        consumption += value;
        long limit;
        try {
            GroupDataList list = getGroupDataList(user);
            limit = list.getUpperLimit(limitName);
        } catch (FtpConfigException e) {
            log.error(e);
            limit = 0;
        }
        if (consumption > limit) {
            throw new FtpQuotaException(createQuotaMessage(limitName, consumption, limit));
        }
        userConsumptions.put(limitName, new Long(consumption));

    }

    /**
     * {@inheritDoc}
     */
    public void updateAverageStatistics(String user, String avgKey, long value) {
        String countKey = "Sample count (" + avgKey + ")";
        Map<String, Long> userConsumptions = getUserStatistics(user);
        Long prevAvgObj = (Long) userConsumptions.get(avgKey);
        long prevAvg = prevAvgObj == null ? 0 : prevAvgObj.longValue();
        Long prevCountObj = (Long) userConsumptions.get(countKey);
        long prevCount = prevCountObj == null ? 0 : prevCountObj.longValue();
        long currentAvg = (prevAvg * prevCount + value) / (prevCount + 1);
        userConsumptions.put(avgKey, new Long(currentAvg));
        userConsumptions.put(countKey, new Long(prevCount + 1));
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Long> getUserStatistics(String user) {
        String userAndDate = getUserAndDateKey(user);
        return getUserResourceConsumptions(userAndDate);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Map<String, Long>> getAllStatistics() {
        return resourceConsumption;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLoaded() throws FtpConfigException {
        return userManagerData != null;

    }

    /**
     * {@inheritDoc}
     */
    public synchronized UserData getUserData(String username) throws FtpConfigException {
        return userManagerData.getUserData(username);
    }

    private String getUserAndDateKey(String user) {
        String userAndDate = dateFormat.format(new Date()) + " " + user;
        return userAndDate;
    }

    private Map<String, Long> getUserResourceConsumptions(String userAndDate) {
        Map<String, Long> userConsumption = resourceConsumption.get(userAndDate);
        if (userConsumption == null) {
            userConsumption = Collections.synchronizedMap(new HashMap<String, Long>());
            resourceConsumption.put(userAndDate, userConsumption);
        }
        return userConsumption;
    }

    private String createQuotaMessage(String limitName, long consumption, long limit) {
        return limitName + " exceed limit of " + limit + " (current consumption is " + consumption + ")";
    }

}
