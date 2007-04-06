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

package net.sf.hermesftp.cmd;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpSessionContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract ancestor of FTP command classes that provides some functionallity shared by different
 * command classes.
 * 
 * @author Lars Behnke
 */
public abstract class AbstractFtpCmd implements FtpCmd, FtpConstants {

    private static Log        log = LogFactory.getLog(AbstractFtpCmd.class);

    private String            token;

    private String            arguments;

    private FtpSessionContext ctx;

    private boolean           responded;

    /**
     * Returns a message resource string.
     * 
     * @param msgKey The message key.
     * @param args The arguments.
     * @return The message.
     */
    protected String msg(String msgKey, Object[] args) {
        String msg = msg(msgKey);
        if (args != null) {
            msg = MessageFormat.format(msg, args);
        }
        return msg;
    }

    /**
     * Returns a message resource string.
     * 
     * @param msgKey The message key.
     * @return The message.
     */
    protected String msg(String msgKey) {
        return getCtx().getRes(msgKey);
    }

    /**
     * Returns a message resource string.
     * 
     * @param msgKey The message key.
     * @param arg An single message argument.
     * @return The message.
     */
    protected String msg(String msgKey, String arg) {
        return msg(msgKey, new Object[] {arg});
    }

    /**
     * Writes out the response to a client command.
     * 
     * @param text The response.
     */
    protected void out(String text) {
        responded = !text.startsWith("150");
        getCtx().getClientResponseWriter().println(text);
        getCtx().getClientResponseWriter().flush();
    }

    /**
     * Writes the message identified by the passed key to the control stream. If additional
     * arguments are passed they are integrated into the message string.
     * 
     * @param msgKey The message key as defined in the resource file.
     * @param args The optional arguments.
     */
    protected void msgOut(String msgKey, Object[] args) {
        String msg = msg(msgKey, args);
        out(msg);
    }

    /**
     * Convenience method that prints out a message to the control channel..
     * 
     * @param msgKey The key of the message.
     */
    protected void msgOut(String msgKey) {
        msgOut(msgKey, (Object[]) null);
    }

    /**
     * Convenience method that prints out a message to the control channel.
     * 
     * @param msgKey The key of the message.
     * @param argument Text argument.
     */
    protected void msgOut(String msgKey, String argument) {
        msgOut(msgKey, new Object[] {argument});
    }

    /**
     * Returns a path argument.
     * 
     * @return The path
     */
    protected String getPathArg() {
        return getAbsPath(getArguments());
    }

    /**
     * Returns the absolute path of the passed rel. path.
     * 
     * @param path The relative path;
     * @return The absolute path
     */
    protected String getAbsPath(String path) {
        String result;
        try {
            path = FilenameUtils.normalizeNoEndSeparator(path);
            if (path.startsWith(File.separator)) {
                result = new File(getCtx().getOptions().getRootDir(), path.substring(1)).getCanonicalPath();
            } else {
                result = new File(getCtx().getRemoteDir(), path).getCanonicalPath();
            }
        } catch (IOException e) {
            result = getCtx().getRemoteDir();
            log.error(e);
        }
        return result;
    }

    /**
     * @return Returns the file offset to be used, or null not offset defined.
     */
    protected long getAndResetFileOffset() {
        Long fileOffsetObj = (Long) getCtx().getAttribute(ATTR_FILE_OFFSET);
        getCtx().setAttribute(ATTR_FILE_OFFSET, null);
        long fileOffset = fileOffsetObj == null ? 0 : fileOffsetObj.longValue();
        return fileOffset;
    }


    
    /**
     * {@inheritDoc}
     */
    public void setArguments(String args) {
        this.arguments = args;

    }

    /**
     * Returns the arguments previously passed to the instance.
     * 
     * @return the command line arguments.
     */
    public String getArguments() {
        return arguments;
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
     * Gets the permission on the current path.
     * 
     * @return The permission constant.
     */
    public int getPermission() {
        return getCtx().getPermission(getCtx().getRemoteDir());
    }

    /**
     * Returns the command token.
     * 
     * @return The command token.
     */
    public String getToken() {
        return token;
    }

    /**
     * {@inheritDoc}
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return True if response has been sent.
     */
    public boolean isResponded() {
        return responded;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handleAsyncCmd(String req) {
        return false;
    }
}
