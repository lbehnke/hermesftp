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

package net.sf.hermesftp.common;

import java.util.Properties;

import javax.net.ssl.SSLContext;

import net.sf.hermesftp.exception.FtpConfigException;

/**
 * Defines the contract for classes representing the static server options retrieved from the
 * configuration file.
 * 
 * @author Lars Behnke
 */
public interface FtpServerOptions {

    /**
     * Getter method for the java bean <code>properties</code>.
     * 
     * @return Returns the value of the java bean <code>properties</code>.
     */
    Properties getProperties();

    /**
     * Setter method for the java bean <code>properties</code>.
     * 
     * @param properties The value of properties to set.
     */
    void setProperties(Properties properties);

    /**
     * Convenience method for accessing the options.
     * 
     * @param key The option key.
     * @return The option value as string.
     */
    String getProperty(String key);

    /**
     * Read the buffer size for downloading or uploading files. The default buffer size is 1024
     * bytes.
     * 
     * @return The buffer size;
     */
    int getBufferSize();

    /**
     * Getter method for the server side remote directory.
     * 
     * @return The directory
     */
    String getRootDir();

    /**
     * Returns the FTP port.
     * 
     * @return The port.
     */
    int getFtpPort();

    /**
     * Returns the implicit SSL port.
     * 
     * @return The port.
     */
    int getImplicitSslPort();

    /**
     * Returns a an option as boolean value.
     * 
     * @param optionName The name of the option.
     * @param defaultValue The default value.
     * @return The boolean value.
     */
    boolean getBoolean(String optionName, boolean defaultValue);

    /**
     * Returns a an option as integer value.
     * 
     * @param optionName The name of the option.
     * @param defaultValue The default value.
     * @return The integer.
     */
    int getInt(String optionName, int defaultValue);

    /**
     * Returns a an option as string value.
     * 
     * @param optionName The name of the option.
     * @param defaultValue The default value.
     * @return The string.
     */
    String getString(String optionName, String defaultValue);

    /**
     * Returns an array of options. The corresponding property value has to be a comma separated
     * list.
     * 
     * @param optionName The name of the option.
     * @param defaultValues The default values.
     * @return The array.
     */
    String[] getStringArray(String optionName, String[] defaultValues);

    /**
     * Returns an array of integer options. The corresponding property value has to be a comma
     * separated list.
     * 
     * @param optionName The name of the option.
     * @param defaultValues The default values.
     * @return The array.
     */
    int[] getIntArray(String optionName, int[] defaultValues);

    /**
     * Returns a set of all allowed passive ports.
     * 
     * @return The set.
     */
    Integer[] getAllowedPorts();

    /**
     * Returns the SSL context to be used for creating SSL sockets.
     * 
     * @return The SSL context.
     * @throws FtpConfigException Error initializing SSL context or keystore.
     */
    SSLContext getSslContext() throws FtpConfigException;

    /**
     * Getter method for the java bean <code>systemProperties</code>.
     * 
     * @return Returns the value of the java bean <code>systemProperties</code>.
     */
    Properties getSystemProperties();

    /**
     * Returns the application title as defined in the POM file.
     * 
     * @return The application title.
     */
    String getAppTitle();

    /**
     * Returns the application version as defined in the POM file.
     * 
     * @return The application version.
     */
    String getAppVersion();

    /**
     * Returns the application build info as defined in the POM file.
     * 
     * @return The application build info.
     */
    String getAppBuildInfo();

}
