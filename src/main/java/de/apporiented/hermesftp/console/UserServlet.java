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

package de.apporiented.hermesftp.console;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.apporiented.hermesftp.common.FtpConstants;
import de.apporiented.hermesftp.usermanager.UserManager;
import de.apporiented.hermesftp.usermanager.model.UserData;

/**
 * Provides an overview of system and application properties.
 * 
 * @author Administrator
 */
public class UserServlet extends AbstractConsoleServlet {

    private static final String NOT_APPLICABLE   = "n/a";

    private static final long   serialVersionUID = -594524060863329206L;

    private UserManager         userManager;

    /**
     * {@inheritDoc}
     */
    protected Properties getContentProperties() {
        Properties result = new Properties();
        String s = userManager != null ? createUserTable() : NOT_APPLICABLE;
        result.put("user.table", s);
        return result;
    }

    private String createUserTable() {

        List<UserData> list = null;
        try {
            list = getUserManager().getUserDataList();
        } catch (Exception e) {
            return "not available";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ConsoleConstants.TABLE_START_TAG);
        createColumnHeaders(sb);

        int rowCount = 0;
        for (UserData userData : list) {
            rowCount++;
            fillRow(sb, rowCount, userData);
        }

        sb.append(ConsoleConstants.TABLE_END_TAG);
        return sb.toString();
    }

    private void fillRow(StringBuffer sb, int rowCount, UserData user) {
        String band = rowCount % 2 == 0 ? ConsoleConstants.TR_START_TAG_CLASS_A
                : ConsoleConstants.TR_START_TAG_CLASS_B;
        sb.append(band);

        fillField(sb, user.getUid());
        fillField(sb, user.getFullName());
        fillNumField(sb, getUserStat(user.getUid(), FtpConstants.STAT_BYTES_DOWNLOADED));
        fillNumField(sb, getUserStat(user.getUid(), FtpConstants.STAT_BYTES_UPLOADED));
        fillNumField(sb, getUserStat(user.getUid(), FtpConstants.STAT_FILES_DOWNLOADED));
        fillNumField(sb, getUserStat(user.getUid(), FtpConstants.STAT_FILES_UPLOADED));
        fillNumField(sb, getUserStat(user.getUid(), FtpConstants.STAT_DOWNLOAD_RATE));
        fillNumField(sb, getUserStat(user.getUid(), FtpConstants.STAT_UPLOAD_RATE));

        sb.append(ConsoleConstants.TR_END_TAG);
    }

    private void createColumnHeaders(StringBuffer sb) {
        sb.append(ConsoleConstants.TR_START_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "User ID" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "Name" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "DL Bytes" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "UL Bytes" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "DL Files" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "UL Files" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "DL KB/s" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "UL KB/s" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TR_END_TAG);
    }

    private String getUserStat(String uid, String key) {
        String result;
        Map<String, Long> userStats = getUserManager().getUserStatistics(uid);
        if (userStats != null) {
            Long val = userStats.get(key);
            result = val == null ? "0" : formatNum(val);
        } else {
            result = NOT_APPLICABLE;
        }
        return result;
    }

    /**
     * Getter methode for property <code>userManager</code>.
     * 
     * @return Property <code>userManager</code>.
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Setter methode for property <code>userManager</code>.
     * 
     * @param userManager Value for <code>userManager</code>.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
