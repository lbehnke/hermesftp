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

package net.sf.hermesftp.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import net.sf.hermesftp.exception.FtpQuotaException;
import net.sf.hermesftp.usermanager.UserManager;

/**
 * This class servers as a means of transportation for data shared by a single FTP session.
 * Instances of the <code>FtpSessionContextImpl</code> class are passed to each of the commands
 * while executing a FTP command sequence. The command objects read connection settings and other
 * options from the context. In turn data that may concern the general state of the FTP session can
 * be stored in the context.
 * 
 * @author Lars Behnke
 */
public interface FtpSessionContext {

    /**
     * Returns a sesson attribute.
     * 
     * @param name Name of the attribute.
     * @return Value of the attribute.
     */
    Object getAttribute(String name);

    /**
     * Sets a session attribute.
     * 
     * @param name Name of the attribute.
     * @param value Value of the attribute.
     */
    void setAttribute(String name, Object value);

    /**
     * @return The static server options as read from the configuration file.
     */
    FtpServerOptions getOptions();

    /**
     * Returns the value of an server option.
     * 
     * @param key The option name.
     * @return The value.
     */
    String getOption(String key);

    /**
     * Gets the user's password.
     * 
     * @return The user's password.
     */
    String getPassword();

    /**
     * Sets the user's password.
     * 
     * @param password The users password.
     */
    void setPassword(String password);

    /**
     * Gets the current remote directory.
     * 
     * @return The remote directory.
     */
    String getRemoteDir();

    /**
     * Gets the relative path, with respect to the FTP root path.
     * 
     * @return The path.
     */
    String getRemoteRelDir();

    /**
     * Sets the remote directory of the user's session.
     * 
     * @param remoteDir The directory to set.
     */
    void setRemoteDir(String remoteDir);

    /**
     * Returns the login name of the user.
     * 
     * @return The name.
     */
    String getUser();

    /**
     * Sets the login name of the user.
     * 
     * @param user The user name.
     */
    void setUser(String user);

    /**
     * Returns the instance that listens to session events.
     * 
     * @return The session listener.
     */
    FtpEventListener getEventListener();

    /**
     * Returns a named resource from a resource bundle.
     * 
     * @param id The name of the resource.
     * @return The resource.
     */
    String getRes(String id);

    /**
     * Checks whether user was successfully authenticated.
     * 
     * @return True, user is authenticated.
     */
    boolean isAuthenticated();

    /**
     * Returns the current data type (ASCII, BINARY, EPBCDIC).
     * 
     * @return The data type.
     */
    int getDataType();

    /**
     * Sets the current data type.
     * 
     * @param dataType The data type.
     */
    void setDataType(int dataType);

    /**
     * Returns the transmission mode (Stream, Block, Compressed).
     * 
     * @return The transmission mode.
     */
    int getTransmissionMode();

    /**
     * Sets the transmission mode (Stream, Block, Compressed).
     * 
     * @param mode The transmission mode.
     */
    void setTransmissionMode(int mode);

    /**
     * Returns the storage structure (File, Record).
     * 
     * @return The storage structure.
     */
    int getStorageStructure();

    /**
     * Sets the storage structure (File, Record).
     * 
     * @param struct The storage structure..
     */
    void setStorageStructure(int struct);

    /**
     * Returns the server socket used in passive transfer mode.
     * 
     * @return The socket.
     */
    ServerSocket getPassiveModeServerSocket();

    /**
     * Sets the server socket to be used in passive transfer mode.
     * 
     * @param passiveSocket The socket.
     */
    void setPassiveModeServerSocket(ServerSocket passiveSocket);

    /**
     * Returns the socket that is used for file transfer.
     * 
     * @return The socket.
     */
    Socket getDataSocket();

    /**
     * Sets the socket that is used in file transfer.
     * 
     * @param socket The socket.
     */
    void setDataSocket(Socket socket);

    /**
     * Returns the client communication socket.
     * 
     * @return The socket.
     */
    Socket getClientSocket();

    /**
     * Sets the client communication socket.
     * 
     * @param clientSocket The socket.
     * @throws IOException Thrown if socket is invalid.
     */
    void setClientSocket(Socket clientSocket) throws IOException;

    /**
     * Returns the writer that is used to respond to client requests.
     * 
     * @return Response writer.
     */
    PrintWriter getClientResponseWriter();

    /**
     * Returns the Reader that is used to read client commands.
     * 
     * @return The reader.
     */
    BufferedReader getClientCmdReader();

    /**
     * Gets the user's permission on the passed path.
     * 
     * @param path The path to check the permission on.
     * @return The permission value.
     */
    int getPermission(String path);

    /**
     * Returns the user manager.
     * 
     * @return The user manager.
     */
    UserManager getUserManager();

    /**
     * Returns the charset to be used for text output.
     * 
     * @return The name of the charset.
     */
    String getCharset();

    /**
     * Authenticates the user based on the given user name and password.
     * 
     * @return True, if successful.
     */
    boolean authenticate();

    /**
     * Resets the user's credentials and unflags the authentication.
     */
    void resetCredentials();

    /**
     * Closes all sockets involved in current session.
     */
    void closeSockets();

    /**
     * Date/time the session context was created.
     * 
     * @param The creation time.
     */
    void setCreationTime(Date time);

    /**
     * Date/time the session context was created.
     * 
     * @return The creation time.
     */
    Date getCreationTime();

    /**
     * Returns the session statistics.
     * 
     * @return The statistics.
     */
    Map getSessionStatistics();

    /**
     * {@inheritDoc}
     */
    public void registerResourceConsumption(String limitName, long value) throws FtpQuotaException;

}
