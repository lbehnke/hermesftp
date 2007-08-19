/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
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

package net.sf.hermesftp.session.impl;

import java.net.SocketTimeoutException;
import java.text.MessageFormat;

import net.sf.hermesftp.cmd.FtpCmd;
import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpCmdException;
import net.sf.hermesftp.exception.FtpCmdResponseException;
import net.sf.hermesftp.exception.FtpIllegalCmdException;
import net.sf.hermesftp.exception.FtpQuitException;
import net.sf.hermesftp.parser.FtpCmdReader;
import net.sf.hermesftp.session.FtpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default FTP session implementation.
 * 
 * @author Lars Behnke
 */
public class FtpSessionImpl extends Thread implements FtpSession, FtpConstants {

    private static final int  DEFAULT_IDLE_SECONDS = 60;

    private static final int  COMMAND_TIMEOUT      = 3000;

    private static Log        log                  = LogFactory.getLog(FtpSessionImpl.class);

    private FtpCmdReader      cmdReader;

    private FtpSessionContext ftpContext;

    private boolean           terminated;

    /**
     * Constructor.
     */
    public FtpSessionImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            printWelcome();
            getCmdReader().setCtx(getFtpContext());
            getCmdReader().start();
            long startWaiting = System.currentTimeMillis();
            while (!isTerminated()) {
                FtpCmd cmd = null;
                try {
                    cmd = getCmdReader().waitForNextCommand(COMMAND_TIMEOUT);
                    terminated = !executeCmd(cmd);
                    startWaiting = System.currentTimeMillis();
                } catch (FtpIllegalCmdException e) {
                    String msg = formatResString(MSG500_CMD, new Object[] {e.getCmdLine()});
                    out(msg);
                } catch (SocketTimeoutException e) {
                    long maxIdleSecs = getFtpContext().getOptions().getInt(OPT_MAX_IDLE_SECONDS,
                        DEFAULT_IDLE_SECONDS);
                    if (System.currentTimeMillis(/*
                                                     * ------------------------------ Hermes FTP
                                                     * Server Copyright (c) 2006 Lars Behnke
                                                     * ------------------------------ This file is
                                                     * part of Hermes FTP Server. Hermes FTP Server
                                                     * is free software; you can redistribute it
                                                     * and/or modify it under the terms of the GNU
                                                     * General Public License as published by the
                                                     * Free Software Foundation; either version 2 of
                                                     * the License, or (at your option) any later
                                                     * version. Foobar is distributed in the hope
                                                     * that it will be useful, but WITHOUT ANY
                                                     * WARRANTY; without even the implied warranty
                                                     * of MERCHANTABILITY or FITNESS FOR A
                                                     * PARTICULAR PURPOSE. See the GNU General
                                                     * Public License for more details. You should
                                                     * have received a copy of the GNU General
                                                     * Public License along with Foobar; if not,
                                                     * write to the Free Software Foundation, Inc.,
                                                     * 51 Franklin St, Fifth Floor, Boston, MA
                                                     * 02110-1301 USA
                                                     */) - startWaiting > (maxIdleSecs * MILLI)) {
                        out(formatResString(MSG421, new Object[0]));
                        log.debug("Session timeout after " + maxIdleSecs + " seconds");
                        terminated = true;
                    }
                }
            }

        } catch (FtpCmdException e) {
            log.error("Session closed because of error while executing command", e);
        } finally {
            terminated = true;
            getCmdReader().abort();
            getFtpContext().closeSockets();
            getFtpContext().getEventListener().sessionClosed(this);
        }
    }

    private boolean executeCmd(FtpCmd cmd) throws FtpCmdException {
        boolean proceed = true;
        if (cmd != null) {
            synchronized (cmd) {
                if (cmd.isAuthenticationRequired() && !getFtpContext().isAuthenticated()) {
                    String msg = getFtpContext().getRes(MSG530);
                    out(msg);
                } else {
                    try {
                        cmd.setCtx(getFtpContext());
                        cmd.execute();
                    } catch (FtpQuitException e) {
                        proceed = false;
                    } catch (FtpCmdResponseException e) {
                        out(e.getMessage());
                    } finally {
                        cmd.notifyAll();
                    }
                }
            }
        }
        return proceed;
    }

    private void printWelcome() {
        String title = getFtpContext().getOptions().getAppTitle();
        String version = getFtpContext().getOptions().getAppVersion();
        String welcome = formatResString(MSG220_WEL, new Object[] {title + " " + version});
        getFtpContext().getClientResponseWriter().println(welcome);
        welcome = getFtpContext().getOption(OPT_MSG_WELCOME);
        if (welcome != null && welcome.length() > 0) {
            welcome = formatResString(MSG220_WEL, new Object[] {welcome});
            out(welcome);
        }
        out(getFtpContext().getRes(MSG220));
    }

    private String formatResString(String resourceKey, Object[] args) {
        String msg = getFtpContext().getRes(resourceKey);
        if (args != null) {
            msg = MessageFormat.format(msg, args);
        }
        return msg;
    }

    private void out(String msg) {
        getFtpContext().getClientResponseWriter().println(msg);
        getFtpContext().getClientResponseWriter().flush();
    }

    /**
     * Getter method for the java bean <code>ftpContext</code>.
     * 
     * @return Returns the value of the java bean <code>ftpContext</code>.
     */
    public FtpSessionContext getFtpContext() {
        return ftpContext;
    }

    /**
     * Setter method for the java bean <code>ftpContext</code>.
     * 
     * @param ftpContext The value of ftpContext to set.
     */
    public void setFtpContext(FtpSessionContext ftpContext) {
        this.ftpContext = ftpContext;
    }

    /**
     * {@inheritDoc}
     */
    public void setCmdReader(FtpCmdReader reader) {
        this.cmdReader = reader;

    }

    private FtpCmdReader getCmdReader() {
        return cmdReader;
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
     * @see net.sf.hermesftp.session.FtpSession#abort()
     */
    public void abort() {
        terminated = true;
    }

}
