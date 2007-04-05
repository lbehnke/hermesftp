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

package net.sf.hermesftp.server.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpServerOptions;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.utils.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The FTP server options used throughout the application.
 * 
 * @author Lars Behnke
 */
public class FtpServerOptionsImpl implements FtpServerOptions, FtpConstants {

    private static final int    DEFAULT_BUFFER_SIZE       = 1024;

    private static final int    DEFAULT_FTP_IMPL_SSL_PORT = 990;

    private static final int    DEFAULT_FTP_PORT          = 21;

    private static final String SYS_OPT_VERSION           = "version";

    private static final String SYS_OPT_TITLE             = "title";

    private static final String SYS_OPT_BUILD_INFO        = "info";

    private static Log          log                       = LogFactory.getLog(FtpServerOptionsImpl.class);

    private Properties          properties;

    private Integer[]           allowedPassivePorts;

    private SSLContext          sslContext;

    /**
     * {@inheritDoc}
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    public int getBufferSize() {
        return getInt(OPT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    public String getRootDir() {
        File defaultDir = new File(System.getProperty("user.home"), "hermesftp");
        String dir = getString(OPT_REMOTE_DIR, defaultDir.getAbsolutePath());
        return dir;
    }

    /**
     * {@inheritDoc}
     */
    public int getFtpPort() {
        return getInt(OPT_FTP_PORT, DEFAULT_FTP_PORT);
    }

    /**
     * {@inheritDoc}
     */
    public int getImplicitSslPort() {
        return getInt(OPT_SSL_PORT_IMPLICIT, DEFAULT_FTP_IMPL_SSL_PORT);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String optionName, boolean defaultValue) {
        boolean result;
        String boolStr = getProperties().getProperty(optionName);
        if ("true".equalsIgnoreCase(boolStr) || "false".equalsIgnoreCase(boolStr)) {
            result = Boolean.valueOf(boolStr).booleanValue();
        } else {
            result = defaultValue;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int getInt(String optionName, int defaultValue) {
        int result = defaultValue;
        String intStr = getProperties().getProperty(optionName);
        if (intStr != null && intStr.length() > 0) {
            try {
                result = Integer.parseInt(intStr);
            } catch (NumberFormatException e) {
                log.error("Invalid integer: " + intStr + ". Fall back to default value " + result);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getString(String optionName, String defaultValue) {
        String result = getProperties().getProperty(optionName);
        if (result == null || result.length() == 0) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getStringArray(String optionName, String[] defaultValues) {
        String[] result = defaultValues;
        String strList = getProperties().getProperty(optionName);
        if (strList != null && strList.length() > 0) {
            result = strList.split(SEPARATOR);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getIntArray(String optionName, int[] defaultValues) {
        int[] result = defaultValues;
        String strList = getProperties().getProperty(optionName);
        if (strList != null && strList.length() > 0) {
            String[] elems = strList.split(SEPARATOR);
            result = new int[elems.length];
            for (int i = 0; i < elems.length; i++) {
                result[i] = Integer.parseInt(elems[i].trim());
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Integer[] getAllowedPorts() {
        if (allowedPassivePorts == null) {
            List portList = new ArrayList();
            String[] allowedPorts = getStringArray(OPT_ALLOWED_PASSIVE_PORTS, null);
            if (allowedPorts != null && allowedPorts.length > 0 && allowedPorts[0] != null
                    && allowedPorts[0].trim().length() > 0) {
                for (int i = 0; i < allowedPorts.length; i++) {
                    String[] portRange = allowedPorts[i].split("\\-");
                    if (portRange.length > 1) {
                        int portMin = Integer.parseInt(portRange[0].trim());
                        int portMax = Integer.parseInt(portRange[1].trim());
                        for (int port = portMin; port <= portMax; port++) {
                            portList.add(new Integer(port));
                        }
                    } else {
                        int port = Integer.parseInt(portRange[0].trim());
                        portList.add(new Integer(port));
                    }
                }
            }
            allowedPassivePorts = (Integer[]) portList.toArray(new Integer[portList.size()]);

        }
        return allowedPassivePorts;
    }

    /**
     * {@inheritDoc}
     */
    public SSLContext getSslContext() throws FtpConfigException {
        if (sslContext == null) {
            sslContext = createSslContext();
        }
        return sslContext;
    }

    private SSLContext createSslContext() throws FtpConfigException {
        SSLContext sslContext = null;
        try {
            /* Get keystore file and password */
            InputStream ksInputStream = getKeyStoreInputStream();
            char[] ksPass = getKeyStorePassword();

            /*
             * Get the java keystore object an key manager. A keystore is where keys and
             * certificates are kept.
             */
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(ksInputStream, ksPass);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, ksPass);

            /*
             * An SSLContext is an environment for implementing JSSE. It is used to create a
             * ServerSocketFactory
             */
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(kmf.getKeyManagers(), null, null);
        } catch (KeyManagementException e) {
            throw new FtpConfigException("A key management authorization problem occurred.");
        } catch (FileNotFoundException e) {
            throw new FtpConfigException("The key store file could not be found.");
        } catch (KeyStoreException e) {
            throw new FtpConfigException("A key store problem occurred.");
        } catch (NoSuchAlgorithmException e) {
            throw new FtpConfigException("The hash algorithm is not supported.");
        } catch (CertificateException e) {
            throw new FtpConfigException("Certificate could not be loaded.");
        } catch (UnrecoverableKeyException e) {
            throw new FtpConfigException("Key store cannot be recovered.");
        } catch (IOException e) {
            throw new FtpConfigException("Reading the key store failed.");
        }
        return sslContext;
    }

    private char[] getKeyStorePassword() {
        char[] ksPass;
        String ksFile = getProperty(OPT_SSL_KEYSTORE_FILE);
        if (ksFile != null && ksFile.length() > 0) {
            String ksPassStr = getProperty(OPT_SSL_KEYSTORE_PASS);
            ksPass = ksPassStr == null ? new char[0] : ksPassStr.toCharArray();
        } else {
            ksPass = DEFAULT_KEYSTORE_PASS.toCharArray();
        }
        return ksPass;
    }

    private InputStream getKeyStoreInputStream() throws FileNotFoundException {
        String ksFile = getProperty(OPT_SSL_KEYSTORE_FILE);
        if (ksFile == null) {
            throw new FileNotFoundException("Keystore file not defined.");
        }
        return new FileInputStream(ksFile);

    }

    /**
     * {@inheritDoc}
     */
    public String getAppTitle() {
        return getSystemProperties().getProperty(SYS_OPT_TITLE);
    }

    /**
     * {@inheritDoc}
     */
    public String getAppVersion() {
        return getSystemProperties().getProperty(SYS_OPT_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    public String getAppBuildInfo() {
        return getSystemProperties().getProperty(SYS_OPT_BUILD_INFO);
    }

    /**
     * {@inheritDoc}
     */
    public Properties getSystemProperties() {
        return IOUtils.getAppProperties();
    }

}
