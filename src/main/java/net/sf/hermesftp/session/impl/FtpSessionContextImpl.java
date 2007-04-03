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

package net.sf.hermesftp.session.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpEventListener;
import net.sf.hermesftp.common.FtpServerOptions;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.exception.FtpQuotaException;
import net.sf.hermesftp.usermanager.UserManager;
import net.sf.hermesftp.utils.IOUtils;
import net.sf.hermesftp.utils.LoggingReader;
import net.sf.hermesftp.utils.LoggingWriter;

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
public class FtpSessionContextImpl
    implements FtpConstants, FtpSessionContext {

    private static Log log = LogFactory.getLog(FtpSessionContextImpl.class);

    static int portIdx = 0;

    private String user;

    private String password;

    private boolean authenticated;

    private int dataType = DT_BINARY;

    private int transmissionMode = MODE_STREAM;

    private int storageStructure = STRUCT_FILE;

    private String remoteDir;

    private Socket clientSocket;

    private BufferedReader clientCmdReader;

    private PrintWriter clientResponseWriter;

    private FtpServerOptions options;

    private FtpEventListener eventListener;

    private ResourceBundle resourceBundle;

    private ServerSocket passiveModeServerSocket;

    private Socket dataSocket;

    private UserManager userManager;
    
    private Date creationTime;

    private Map attributes;
    
    private Map sessionStatistics = Collections.synchronizedMap(new HashMap());

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
        this.attributes = Collections.synchronizedMap(new HashMap());
        this.eventListener = listener;
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
            }
            if (!relDir.startsWith(File.separator)) {
                relDir = File.separator + relDir;
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
    public ServerSocket getPassiveModeServerSocket() {
        return passiveModeServerSocket;
    }

    /**
     * {@inheritDoc}
     */
    public void setPassiveModeServerSocket(ServerSocket passiveSocket) {
        this.passiveModeServerSocket = passiveSocket;
    }

    /**
     * {@inheritDoc}
     */
    public Socket getDataSocket() {
        return dataSocket;
    }

    /**
     * {@inheritDoc}
     */
    public void setDataSocket(Socket socket) {
        this.dataSocket = socket;
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
        this.clientResponseWriter = new LoggingWriter(clientSocket.getOutputStream(), true);
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
            result = userManager.getPermission(path, getUser(), options.getRootDir());
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
                dirName = userManager.getStartDir(getUser(), options.getRootDir());
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
        IOUtils.closeGracefully(getDataSocket());
        setDataSocket(null);
        IOUtils.closeGracefully(getPassiveModeServerSocket());
        setPassiveModeServerSocket(null);
        IOUtils.closeGracefully(getClientSocket());
    }

    /**
     * {@inheritDoc}
     */
    public String getCharset() {
        String charset;
        Boolean forceUtf8 = (Boolean) getAttribute(ATTR_FORCE_UTF8);
        if (forceUtf8 != null && forceUtf8.booleanValue()) {
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
            port = new Integer(0);
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
    public void registerResourceConsumption(String limitName, long value)
            throws FtpQuotaException {
        Map sessionStats = getSessionStatistics();
        Long consumptionObj = (Long) sessionStats.get(limitName);
        long consumption = consumptionObj == null ? 0 : consumptionObj.longValue();
        consumption += value;
        sessionStats.put(limitName, new Long(consumption));
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Map getSessionStatistics() {
        return sessionStatistics;
    }

}
