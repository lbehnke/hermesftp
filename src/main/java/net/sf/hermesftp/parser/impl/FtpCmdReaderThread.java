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

package net.sf.hermesftp.parser.impl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.hermesftp.cmd.ClientSocketModifier;
import net.sf.hermesftp.cmd.CmdListAware;
import net.sf.hermesftp.cmd.FtpCmd;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.exception.FtpIllegalCmdException;
import net.sf.hermesftp.parser.FtpCmdParser;
import net.sf.hermesftp.parser.FtpCmdReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This Command reader thread listens for client input and mantains an FIFO list of incoming command
 * lines. The thread is active during the execution of transfer commands (STOR, RETR) and informs
 * these commands asynchronously when a status or abort is requested by the client.
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdReaderThread
    extends Thread
    implements FtpCmdReader {

    private static Log log = LogFactory.getLog(FtpCmdReaderThread.class);

    private FtpSessionContext ctx;

    private List cmdQueue = Collections.synchronizedList(new ArrayList());

    private List errorQueue = Collections.synchronizedList(new ArrayList());

    private boolean terminated;

    private FtpCmdParser parser;

    private Object cmdLock = new Object();

    private FtpCmd lastCmd;

    /**
     * Constructor.
     */
    public FtpCmdReaderThread() {
        super();
        this.terminated = false;
    }

    /**
     * Reads commands issued by the client form the buffer. There two commands - STAT and ABOR -
     * that can be send during a file transfer operation. If the currently executed command (RETR,
     * STOR,...) implements the AsyncAbortListener interface, the responsibility to handle these
     * requests is delegated to this command.
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            while (!isTerminated()) {
                String cmdLine = getCtx().getClientCmdReader().readLine();
                if (cmdLine == null) {
                    log.debug("Client disconnected. Command reader is terminated.");
                    break;
                }
                if (lastCmd == null || !lastCmd.handleAsyncCmd(cmdLine)) {
                    handleSyncCmd(cmdLine);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            terminated = true;
        }
    }

    private void handleSyncCmd(String cmdLine) {
        FtpCmd cmd = null;
        String token = getParser().findCommandToken(cmdLine);
        synchronized (cmdLock) {
//            while (cmdAvailable() || errAvailable()) {
//                try {
//                    cmdLock.wait(DEFAULT_TIMEOUT);
//                } catch (InterruptedException e) {
//                    log.debug("Thread " + this.getName() + " has been interrupted.");
//                }
//                if (isTerminated()) {
//                    break;
//                }
//            }
            if (token == null) {
                enqueueErr(cmdLine);
            } else {
                cmd = enqueueCmd(cmdLine, token);
            }
            cmdLock.notifyAll();
        }
        if (cmd instanceof ClientSocketModifier) {
            waitUntilSocketModified(cmd);
        }

    }

    private void waitUntilSocketModified(FtpCmd cmd) {
        ClientSocketModifier sockCmd = (ClientSocketModifier) cmd;
        while (!sockCmd.socketModified()) {
            try {
                synchronized (sockCmd) {
                    sockCmd.wait();
                }
            } catch (InterruptedException e1) {
                log.error(e1);
            }
        }
    }

    private FtpCmd enqueueCmd(String cmdLine, String token) {
        FtpCmd cmd = getParser().createCommandByToken(token);
        if (cmd == null) {
            enqueueErr(cmdLine);
        } else {

            if (cmd instanceof CmdListAware) {
                CmdListAware fla = (CmdListAware) cmd;
                fla.setList(createCommandList());
            }

            String args = cmdLine.substring(token.length()).trim();
            cmd.setArguments(args);
            cmdQueue.add(cmd);
        }
        return cmd;
    }

    private List createCommandList() {
        String[] tokens = getParser().getCommandTokens();
        Arrays.sort(tokens);
        List cmdList = new ArrayList();
        for (int i = 0; i < tokens.length; i++) {
            cmdList.add(getParser().createCommandByToken(tokens[i]));
        }
        return cmdList;
    }

    private void enqueueErr(String cmdLine) {
        errorQueue.add(cmdLine);
    }

    private boolean cmdAvailable() {
        return cmdQueue.size() > 0;
    }

    private boolean errAvailable() {
        return errorQueue.size() > 0;
    }

    private FtpCmd consumeNextCommand() {
        return (FtpCmd) consumeNext(cmdQueue);
    }

    private String consumeNextError() {
        return (String) consumeNext(errorQueue);
    }

    /**
     * Gets next Object from the queue. Has to be called within a synchronized method.
     *
     * @param queue The list that stores the objects.
     * @return The next Object.
     */
    private Object consumeNext(List queue) {
        Object result;
        if (queue.size() > 0) {
            result = queue.size() > 0 ? queue.get(0) : null;
            queue.remove(0);
        } else {
            result = null;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public FtpCmd waitForNextCommand(int timeout) throws FtpIllegalCmdException,
            SocketTimeoutException {
        FtpCmd cmd = null;
        synchronized (cmdLock) {
            long startStamp = System.currentTimeMillis();
            while (!cmdAvailable() && !errAvailable()) {
                try {
                    cmdLock.wait(timeout);
                } catch (InterruptedException e) {
                    log.debug("Thread " + this.getName() + " has been interrupted.");
                }
                if (startStamp + timeout < System.currentTimeMillis()) {
                    throw new SocketTimeoutException("Command reader timeout.");
                }
                if (isTerminated()) {
                    break;
                }
            }
            String err = consumeNextError();
            if (err != null) {
                throw new FtpIllegalCmdException(err);
            }

            cmd = consumeNextCommand();
            lastCmd = cmd;
            cmdLock.notifyAll();
        }
        return cmd;
    }

    /**
     * {@inheritDoc}
     */
    public FtpCmdParser getParser() {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    public void setParser(FtpCmdParser parser) {
        this.parser = parser;
    }

    /**
     * Getter method for the java bean <code>ctx</code>.
     *
     * @return Returns the value of the java bean <code>ctx</code>.
     */
    public FtpSessionContext getCtx() {
        return ctx;
    }

    /**
     * Setter method for the java bean <code>ctx</code>.
     *
     * @param ctx The value of ctx to set.
     */
    public void setCtx(FtpSessionContext ctx) {
        this.ctx = ctx;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTerminated() {
        return terminated;
    }

    /**
     * {@inheritDoc}
     */
    public void abort() {
        terminated = true;
    }

}
