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

package net.sf.hermesftp.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpEventListener;
import net.sf.hermesftp.common.FtpServerOptions;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.session.FtpSession;
import net.sf.hermesftp.usermanager.UserManager;
import net.sf.hermesftp.utils.AbstractAppAwareBean;
import net.sf.hermesftp.utils.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ancestor class for FTP server implementations.
 *
 * @author Lars Behnke
 *
 */
public abstract class AbstractFtpServer
    extends AbstractAppAwareBean
    implements FtpServer, FtpConstants, FtpEventListener {

    private static final int DEFAULT_TIMEOUT = 3000;

    private static Log log = LogFactory.getLog(FtpServer.class);

    private List sessions = new ArrayList();

    private boolean terminated;

    private FtpServerOptions options;

    private String resources;

    private int status = SERVER_STATUS_UNDEF;

    private UserManager userManager;

    private List ftpEventListeners = new ArrayList();
    
    private int connectionCountHWMark;

    private Date connectionCountHWMarkDate = new Date();

    /**
     * Creates a server socket. Depending on the server implementation this can be a SSL or a
     * regular server socket.
     *
     * @return The server socket.
     * @throws IOException Error on creating server socket.
     * @throws FtpConfigException Error on processing configuration or key store.
     */
    protected abstract ServerSocket createServerSocket() throws IOException, FtpConfigException;

    /**
     * Creates the context object passed to the user session.
     *
     * @return The session context.
     */
    protected abstract FtpSessionContext createFtpContext();

    /**
     * Halts the server.
     */
    public void abort() {
        this.terminated = true;
    }

    /**
     * Getter method for the java bean <code>options</code>.
     *
     * @return Returns the value of the java bean <code>options</code>.
     */
    public FtpServerOptions getOptions() {
        return options;
    }

    /**
     * Setter method for the java bean <code>options</code>.
     *
     * @param options The value of options to set.
     */
    public void setOptions(FtpServerOptions options) {
        this.options = options;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        setStatus(SERVER_STATUS_INIT);
        ServerSocket serverSocket = null;
        try {
            getUserManager().load();
            serverSocket = createServerSocket();
            serverSocket.setSoTimeout(DEFAULT_TIMEOUT);
            setStatus(SERVER_STATUS_READY);
            while (!isTerminated()) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                FtpSessionContext ctx = createFtpContext();
                ctx.setCreationTime(new Date());
                ctx.setClientSocket(clientSocket);
                FtpSession session = (FtpSession) getApplicationContext().getBean(BEAN_SESSION);
                session.setFtpContext(ctx);

                log.debug("Accepting connection to "
                    + ctx.getClientSocket().getInetAddress().getHostAddress());

                session.start();
                registerSession(session);

            }
            setStatus(SERVER_STATUS_HALTED);
        } catch (IOException e) {
            setStatus(SERVER_STATUS_UNDEF);
            log.error(e, e);
        } catch (FtpConfigException e) {
            setStatus(SERVER_STATUS_UNDEF);
            log.error(e.getMessage());
        } finally {
            terminateAllClientSessions();
            IOUtils.closeGracefully(serverSocket);
        }

    }

    private void registerSession(FtpSession session) {
        synchronized (this) {
            sessions.add(session);
            int sessionCount = getConnectionCount();
            if (sessionCount >= connectionCountHWMark) {
                connectionCountHWMark = sessionCount;
                connectionCountHWMarkDate = new Date();
            }
        }
    }

    /**
     * Getter method for the java bean <code>connectionCount</code>.
     *
     * @return Returns the value of the java bean <code>connectionCount</code>.
     */
    public int getConnectionCount() {
        synchronized (this) {
            cleanUpSessions();
            return sessions.size();
        }
    }

    public void cleanUpSessions() {
        List newList = new ArrayList();
        for (Iterator iter = sessions.iterator(); iter.hasNext();) {
            FtpSession session = (FtpSession) iter.next();
            if (!session.isTerminated()) {
                newList.add(session);
            }
        }
        sessions = newList;
    }

    private void terminateAllClientSessions() {
        for (Iterator iter = sessions.iterator(); iter.hasNext();) {
            FtpSession session = (FtpSession) iter.next();
            session.abort();
        }
    }

    /**
     * Convenience method for accessing the application properties.
     *
     * @param name The name of the requested property.
     * @return The property.
     */
    public String getOption(String name) {
        return getOptions().getProperty(name);
    }

    /**
     * Getter method for the java bean <code>resources</code>.
     *
     * @return Returns the value of the java bean <code>resources</code>.
     */
    public String getResources() {
        return resources;
    }

    /**
     * Setter method for the java bean <code>resources</code>.
     *
     * @param resources The value of resources to set.
     */
    public void setResources(String resources) {
        this.resources = resources;
    }

    /**
     * Getter method for the java bean <code>terminated</code>.
     *
     * @return Returns the value of the java bean <code>terminated</code>.
     */
    public boolean isTerminated() {
        return terminated;
    }

    /**
     * Getter method for the java bean <code>status</code>.
     *
     * @return Returns the value of the java bean <code>status</code>.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Setter method for the java bean <code>status</code>.
     *
     * @param status The value of status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Getter method for the java bean <code>userManager</code>.
     *
     * @return Returns the value of the java bean <code>userManager</code>.
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Setter method for the java bean <code>userManager</code>.
     *
     * @param userManager The value of userManager to set.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * {@inheritDoc}
     */
    public void addFtpEventListener(FtpEventListener lstnr) {
        this.ftpEventListeners.add(lstnr);
    }

    /**
     * {@inheritDoc}
     */
    public void downloadPerformed(String clientId, File file) {
        for (Iterator iter = ftpEventListeners.iterator(); iter.hasNext();) {
            FtpEventListener listener = (FtpEventListener) iter.next();
            listener.downloadPerformed(clientId, file);
        }
        log.debug("Download event delegated to listeners.");
    }

    /**
     * {@inheritDoc}
     */
    public void loginPerformed(String clientId, boolean successful) {
        for (Iterator iter = ftpEventListeners.iterator(); iter.hasNext();) {
            FtpEventListener listener = (FtpEventListener) iter.next();
            listener.loginPerformed(clientId, successful);
        }
        log.debug("Login event delegated to listeners.");
    }

    /**
     * {@inheritDoc}
     */
    public void uploadPerformed(String clientId, File file) {
        for (Iterator iter = ftpEventListeners.iterator(); iter.hasNext();) {
            FtpEventListener listener = (FtpEventListener) iter.next();
            listener.uploadPerformed(clientId, file);
        }
        log.debug("Download event delegated to listeners.");
    }

    /**
     * {@inheritDoc}
     */
    public void sessionOpened(Object sessionObj) {
        for (Iterator iter = ftpEventListeners.iterator(); iter.hasNext();) {
            FtpEventListener listener = (FtpEventListener) iter.next();
            listener.sessionOpened(sessionObj);
        }
        log.debug("Session opened event delegated to listeners.");
    }

    /**
     * {@inheritDoc}
     */
    public void sessionClosed(Object sessionObj) {
        synchronized (this) {
            sessions.remove(sessionObj);
        }
        for (Iterator iter = ftpEventListeners.iterator(); iter.hasNext();) {
            FtpEventListener listener = (FtpEventListener) iter.next();
            listener.sessionOpened(sessionObj);
        }
        log.debug("Session closed event delegated to listeners.");
    }

    public List getSessions() {
        return sessions;
    }

    public int getConnectionCountHWMark() {
        return connectionCountHWMark;
    }

    public void setConnectionCountHWMark(int connectionCountHWMark) {
        this.connectionCountHWMark = connectionCountHWMark;
    }

    public Date getConnectionCountHWMarkDate() {
        return connectionCountHWMarkDate;
    }

    public void setConnectionCountHWMarkDate(Date connectionCountHWMarkDate) {
        this.connectionCountHWMarkDate = connectionCountHWMarkDate;
    }

}
