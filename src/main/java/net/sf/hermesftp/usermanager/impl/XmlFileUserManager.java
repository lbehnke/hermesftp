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

package net.sf.hermesftp.usermanager.impl;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.exception.FtpQuotaException;
import net.sf.hermesftp.usermanager.UserManager;
import net.sf.hermesftp.usermanager.model.GroupData;
import net.sf.hermesftp.usermanager.model.UserData;
import net.sf.hermesftp.usermanager.model.UserManagerData;
import net.sf.hermesftp.utils.SecurityUtil;
import net.sf.hermesftp.utils.StringUtils;
import net.sf.hermesftp.utils.VarMerger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lars Behnke
 */
public class XmlFileUserManager implements UserManager {

    private static Log      log                 = LogFactory.getLog(XmlFileUserManager.class);

    private XmlFileReader   fileReader;

    private UserManagerData userManagerData;

    private Map             resourceConsumption = Collections.synchronizedMap(new HashMap());

    private DateFormat      dateFormat          = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * {@inheritDoc}
     */
    public int getPermission(String path, String username, String ftproot) throws FtpConfigException {
        int result = FtpConstants.PRIV_NONE;
        List userGroups = getGroupDataList(username);
        for (Iterator iter = userGroups.iterator(); iter.hasNext();) {
            GroupData groupData = (GroupData) iter.next();
            int permission = groupData.getPermission(path, ftproot, username);
            result = Math.max(result, permission);
        }
        return result;
    }

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

    public synchronized List getGroupDataList(String username) throws FtpConfigException {
        UserData userData = userManagerData.getUserData(username);
        if (userData == null) {
            throw new FtpConfigException("User " + username + " not configured.");
        }
        List groupList = new ArrayList();
        for (Iterator iter = userData.getGroupNames().iterator(); iter.hasNext();) {
            String groupName = (String) iter.next();
            GroupData groupData = userManagerData.getGroupData(groupName);
            groupList.add(groupData);
        }
        return groupList;

    }

    public synchronized List getUserDataList() throws FtpConfigException {
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
    public  synchronized String getStartDir(String user, String ftproot) throws FtpConfigException {
        UserData userData = userManagerData.getUserData(user);
        VarMerger varMerger = new VarMerger(userData.getDir());
        Properties props = new Properties();
        props.setProperty("ftproot", FilenameUtils.separatorsToUnix(ftproot));
        props.setProperty("user", user);
        varMerger.merge(props);
        if (!varMerger.isReplacementComplete()) {
            throw new FtpConfigException("Unresolved placeholders in user configuration file found.");
        }
        return varMerger.getText();
    }

    /**
     * {@inheritDoc}
     */
    public void checkResourceConsumption(String user, String[] limitNames) throws FtpQuotaException {
        Map userConsumptions = getUserStatistics(user);
        for (int i = 0; i < limitNames.length; i++) {
            Long consumptionObj = (Long) userConsumptions.get(limitNames[i]);
            long consumption = consumptionObj == null ? 0 : consumptionObj.longValue();
            long limit;
            try {
                limit = getLimit(user, limitNames[i]);
            } catch (FtpConfigException e) {
                log.error(e);
                limit = 0;
            }
            if (consumption >= limit) {
                throw new FtpQuotaException(createQuotaMessage(limitNames[i], consumption, limit));
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void registerResourceConsumption(String user, String limitName, long value)
            throws FtpQuotaException {
        Map userConsumptions = getUserStatistics(user);
        Long consumptionObj = (Long) userConsumptions.get(limitName);
        long consumption = consumptionObj == null ? 0 : consumptionObj.longValue();
        consumption += value;
        long limit;
        try {
            limit = getLimit(user, limitName);
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
    public Map getUserStatistics(String user) {
        String userAndDate = getUserAndDateKey(user);
        Map userConsumptions = getUserResourceConsumptions(userAndDate);
        return userConsumptions;

    }

    /**
     * {@inheritDoc}
     */
    public Map getAllStatistics() {
        return resourceConsumption;
    }

    private String getUserAndDateKey(String user) {
        String userAndDate = dateFormat.format(new Date()) + " " + user;
        return userAndDate;
    }

    private Map getUserResourceConsumptions(String userAndDate) {
        Map userConsumption = (Map) resourceConsumption.get(userAndDate);
        if (userConsumption == null) {
            userConsumption = Collections.synchronizedMap(new HashMap());
            resourceConsumption.put(userAndDate, userConsumption);
        }
        return userConsumption;
    }

    private String createQuotaMessage(String limitName, long consumption, long limit) {
        return limitName + " exceed limit of " + limit + " (current consumption is " + consumption + ")";
    }

    private long getLimit(String username, String limitName) throws FtpConfigException {
        long result = 0;
        List userGroups = getGroupDataList(username);
        for (Iterator iter = userGroups.iterator(); iter.hasNext();) {
            GroupData groupData = (GroupData) iter.next();
            long limit = groupData.getLimit(limitName);
            if (limit == -1) {
                limit = Long.MAX_VALUE;
            }
            result = Math.max(result, groupData.getLimit(limitName));
        }
        return result;
    }

    public boolean isLoaded() throws FtpConfigException {
        return userManagerData != null;

    }

}
