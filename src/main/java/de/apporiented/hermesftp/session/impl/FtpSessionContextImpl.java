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

package de.apporiented.hermesftp.session.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import de.apporiented.hermesftp.cmd.SocketProvider;
import de.apporiented.hermesftp.common.FtpConstants;
import de.apporiented.hermesftp.common.FtpEventListener;
import de.apporiented.hermesftp.common.FtpServerOptions;
import de.apporiented.hermesftp.common.FtpSessionContext;
import de.apporiented.hermesftp.exception.FtpConfigException;
import de.apporiented.hermesftp.exception.FtpQuotaException;
import de.apporiented.hermesftp.usermanager.UserManager;
import de.apporiented.hermesftp.usermanager.model.GroupDataList;
import de.apporiented.hermesftp.usermanager.model.UserData;
import de.apporiented.hermesftp.utils.LoggingReader;
import de.apporiented.hermesftp.utils.LoggingWriter;
import de.apporiented.hermesftp.utils.VarMerger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class servers as a means of transportation for data shared by a single FTP session.
 * Instances of the <code>FtpSessionContextImpl</code> class are passed to each of the commands
 * while executing a FTP command sequence. The command objects read connection settings and other
 * options from the context. In turn data that may concern the general state of the FTP session can
 * be stored in the context.
 * 
 * @author Lars Behnke
 */
public class FtpSessionContextImpl implements FtpConstants, FtpSessionContext {

    private static Log          log               = LogFactory.getLog(FtpSessionContextImpl.class);

    private static int          portIdx;

    private String              user;

    private String              password;

    private boolean             authenticated;

    private int                 dataType          = DT_BINARY;

    private int                 transmissionMode  = MODE_STREAM;

    private int                 storageStructure  = STRUCT_FILE;

    private String              remoteDir;

    private Socket              clientSocket;

    private BufferedReader      clientCmdReader;

    private PrintWriter         clientResponseWriter;

    private FtpServerOptions    options;

    private FtpEventListener    eventListener;

    private ResourceBundle      resourceBundle;

    private SocketProvider      dataSocketProvider;

    private UserManager         userManager;

    private Date                creationTime;

    private Map<String, Object> attributes;

    private Map<String, Long>   sessionStatistics = Collections.synchronizedMap(new HashMap<String, Long>());

    /**
     * Constructor.
     * 
     * @param options The server options.
     * @param userManager The user manager.
     * @param resourceBundle The resource bundle that containts messages and texts.
     * @param listener The listener that is informed on session events.
     */
    public FtpSessionContextImpl(FtpServerOptions options, UserManager userManager,
            ResourceBundle resourceBundle, FtpEventListener listener) {
        super();
        this.userManager = userManager;
        this.resourceBundle = resourceBundle;
        this.options = options;
        this.attributes = Collections.synchronizedMap(new HashMap<String, Object>());
        this.eventListener = listener;
    }

    @Override
    public void check() throws FtpConfigException {
        try {
            String dirName = getOptions().getRootDir();
            File dir = new File(dirName);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new FtpConfigException("Remote directory not found.");
            }
        }
        catch (RuntimeException e) {
            throw new FtpConfigException("Invalid configuration: " + e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setAttribute(String name, Object value) {
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public FtpServerOptions getOptions() {
        return options;
    }

    /**
     * {@inheritDoc}
     */
    public String getOption(String key) {
        return getOptions().getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteDir() {
        if (remoteDir == null) {
            remoteDir = getOptions().getRootDir();
        }
        return remoteDir;
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteRelDir() {
        String relDir = null;
        try {
            String canDir = new File(getRemoteDir()).getCanonicalPath();
            canDir = FilenameUtils.normalizeNoEndSeparator(canDir);

            String canRoot = new File(getOptions().getRootDir()).getCanonicalPath();
            canRoot = FilenameUtils.normalizeNoEndSeparator(canRoot);

            if (canDir.toUpperCase().startsWith(canRoot.toUpperCase())) {
                relDir = canDir.substring(canRoot.length());
                if (!relDir.startsWith(File.separator)) {
                    relDir = File.separator + relDir;
                }
            }
            else {
                relDir = canDir;
            }

        } catch (IOException e) {
            log.error(e);
        }
        return relDir;
    }

    /**
     * {@inheritDoc}
     */
    public void setRemoteDir(String remoteDir) {
        this.remoteDir = FilenameUtils.normalizeNoEndSeparator(remoteDir);
    }

    /**
     * {@inheritDoc}
     */
    public String getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * {@inheritDoc}
     */
    public FtpEventListener getEventListener() {
        return eventListener;
    }

    /**
     * {@inheritDoc}
     */
    public String getRes(String id) {
        return resourceBundle.getString(id);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * {@inheritDoc}
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * {@inheritDoc}
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * {@inheritDoc}
     */
    public int getStorageStructure() {
        return storageStructure;
    }

    /**
     * {@inheritDoc}
     */
    public void setStorageStructure(int storageStructure) {
        this.storageStructure = storageStructure;
    }

    /**
     * {@inheritDoc}
     */
    public int getTransmissionMode() {
        return transmissionMode;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransmissionMode(int transmissionMode) {
        this.transmissionMode = transmissionMode;
    }

    /**
     * {@inheritDoc}
     */
    public SocketProvider getDataSocketProvider() {
        return dataSocketProvider;
    }

    /**
     * {@inheritDoc}
     */
    public void setDataSocketProvider(SocketProvider provider) {
        this.dataSocketProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * {@inheritDoc}
     */
    public void setClientSocket(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientResponseWriter = new LoggingWriter(new OutputStreamWriter(clientSocket.getOutputStream()),
            true);
        this.clientCmdReader = new LoggingReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /**
     * {@inheritDoc}
     */
    public PrintWriter getClientResponseWriter() {
        return clientResponseWriter;
    }

    /**
     * {@inheritDoc}
     */
    public BufferedReader getClientCmdReader() {
        return clientCmdReader;
    }

    /**
     * {@inheritDoc}
     */
    public int getPermission(String path) {
        int result = PRIV_NONE;
        try {
            GroupDataList list = (GroupDataList) getAttribute(ATTR_GROUP_DATA);
            result = list.getPermission(path, getUser(), options.getRootDir());
        } catch (FtpConfigException e) {
            log.error(e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean authenticate() {
        authenticated = false;
        String dirName = null;
        try {
            authenticated = userManager.authenticate(getUser(), getPassword(), this);
            if (authenticated) {
                setAttribute(ATTR_LOGIN_TIME, new Date());
                UserData userData = userManager.getUserData(getUser());
                setAttribute(ATTR_USER_DATA, userData);
                GroupDataList groupList = userManager.getGroupDataList(getUser());
                setAttribute(ATTR_GROUP_DATA, groupList);
                dirName = getStartDir();
                File dir = new File(dirName);
                if (!dir.exists()) {
                    FileUtils.forceMkdir(dir);
                }
                setRemoteDir(dirName);
            }
        } catch (FtpConfigException e) {
            log.error(e);
        } catch (IOException e) {
            log.error("Could not create directory: " + dirName);
        }
        return authenticated;
    }

    private synchronized String getStartDir() throws FtpConfigException {
        UserData userData = (UserData) getAttribute(ATTR_USER_DATA);
        if (userData == null) {
            throw new FtpConfigException("User data not available");
        }
        VarMerger varMerger = new VarMerger(userData.getDir());
        Properties props = new Properties();
        props.setProperty("ftproot", FilenameUtils.separatorsToUnix(options.getRootDir()));
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
    public UserData getUserData() {
        return (UserData) getAttribute(ATTR_USER_DATA);
    }

    /**
     * {@inheritDoc}
     */
    public void resetCredentials() {
        authenticated = false;
        setUser(null);
        setPassword(null);
    }

    /**
     * {@inheritDoc}
     */
    public void closeSockets() {
        if (getDataSocketProvider() != null) {
            getDataSocketProvider().closeSocket();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getCharset() {
        String charset;
        Boolean forceUtf8 = (Boolean) getAttribute(ATTR_FORCE_UTF8);
        if (forceUtf8 != null && forceUtf8) {
            charset = "UTF-8";
        } else {
            String key = getDataType() == DT_EBCDIC ? OPT_CHARSET_EBCDIC : OPT_CHARSET_ASCII;
            charset = getOptions().getProperty(key);
        }
        return charset;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getNextPassivePort() {
        Integer port;
        Integer[] allowedPorts = getOptions().getAllowedPorts();
        if (allowedPorts == null || allowedPorts.length == 0) {

            /* Let the system decide which port to use. */
            port = 0;
        } else {

            /* Get the port from the user defined list. */
            port = allowedPorts[portIdx++];
            if (portIdx >= allowedPorts.length) {
                portIdx = 0;
            }
        }
        return port;

    }

    /**
     * {@inheritDoc}
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Long> getSessionStatistics() {
        return sessionStatistics;
    }

    private int getUpperLimit(String globalOptionKey, String groupLimitKey) {
        long result;
        long globalLimit = getOptions().getInt(globalOptionKey, -1);

        GroupDataList list = (GroupDataList) getAttribute(ATTR_GROUP_DATA);
        long groupLimit = list.getUpperLimit(groupLimitKey);

        if (globalLimit < 0) {
            result = groupLimit;
        } else if (groupLimit < 0) {
            result = globalLimit;
        } else {
            result = Math.max(groupLimit, globalLimit);
        }

        return (int) result;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxDownloadRate() {
        return getUpperLimit(OPT_MAX_DOWNLOAD_RATE, STAT_DOWNLOAD_RATE);
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxUploadRate() {
        return getUpperLimit(OPT_MAX_UPLOAD_RATE, STAT_UPLOAD_RATE);
    }

    /**
     * Increases a particular resource consumption by the passed value.
     * 
     * @param countKey The name of the statistic.
     * @param value The value
     * @throws FtpQuotaException Thrown if a resource limit has been reached.
     */
    public void updateIncrementalStat(String countKey, long value) throws FtpQuotaException {

        /* All sessions of user */
        getUserManager().updateIncrementalStatistics(getUser(), countKey, value);

        /* Current session */
        Map<String, Long> sessionStats = getSessionStatistics();
        Long consumptionObj = sessionStats.get(countKey);
        long consumption = consumptionObj == null ? 0 : consumptionObj;
        sessionStats.put(countKey, consumption + value);
    }

    /**
     * Updates the upload or download transfer rate taking the passed value into account.
     * 
     * @param avgKey The name of the statistic.
     * @param value The value
     */
    public void updateAverageStat(String avgKey, int value) {

        /* All sessions of user */
        getUserManager().updateAverageStatistics(getUser(), avgKey, value);

        /* Current session */
        String countKey = "Sample count (" + avgKey + ")";
        Map<String, Long> sessionStats = getSessionStatistics();
        Long prevAvgObj = sessionStats.get(avgKey);
        long prevAvg = prevAvgObj == null ? 0 : prevAvgObj;
        Long prevCountObj = sessionStats.get(countKey);
        long prevCount = prevCountObj == null ? 0 : prevCountObj;
        long currentAvg = (prevAvg * prevCount + value) / (prevCount + 1);
        sessionStats.put(avgKey, currentAvg);
        sessionStats.put(countKey, prevCount + 1);
    }

}
