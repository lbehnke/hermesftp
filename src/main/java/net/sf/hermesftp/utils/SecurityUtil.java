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

package net.sf.hermesftp.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import net.sf.hermesftp.exception.FtpConfigException;

import org.apache.commons.codec.binary.Base64;

/**
 * Security related utility methods.
 * 
 * @author Lars Behnke
 */
public final class SecurityUtil {

    private static final String ALG_END   = "}";

    private static final String ALG_START = "{";

    /**
     * Constructor hidden.
     */
    private SecurityUtil() {
        super();
    }

    /**
     * Calculates based on the passed parameters an hash code and returns its BASE64 representation. The
     * used algorithm is prepended.
     * 
     * @param password The password to encode.
     * @param algorithm The algorithm to use (MD5 e.g.)
     * @return The encoded password as string.
     * @throws NoSuchAlgorithmException Passed algorithm is not supported.
     */
    public static String digestPassword(String password, String algorithm) throws NoSuchAlgorithmException {
        if (password == null) {
            throw new IllegalArgumentException("No password passed");
        }
        String result = password.trim();
        if (algorithm == null) {
            return result;
        }
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(password.getBytes());
        byte[] digestedPassword = digest.digest();
        String base64password = new String(Base64.encodeBase64(digestedPassword));
        return ALG_START + algorithm + ALG_END + base64password;
    }

    /**
     * Checks the validity of the password password based on a given hash code. The hash code to check
     * against contains the used algorithm has prefix. Example: {MD5}Cwz8B/yoHJVquRgdhXb0qA==.
     * 
     * @param passwordHash The hash code to check against.
     * @param password The password to check.
     * @return True, if password is valid.
     * @throws NoSuchAlgorithmException Algorithm (from hash prefix) is not supported.
     */
    public static boolean checkPassword(String passwordHash, String password) throws NoSuchAlgorithmException {
        if (passwordHash == null || password == null) {
            return false;
        }
        String algorithm = null;
        int startIdx = passwordHash.indexOf(ALG_START);
        int endIdx = passwordHash.indexOf(ALG_END);
        if (startIdx == 0 && endIdx > startIdx) {
            algorithm = passwordHash.substring(startIdx + 1, endIdx);
            String hashStr = digestPassword(password.trim(), algorithm);
            return passwordHash.equals(hashStr);
        } else {
            return passwordHash.trim().equals(password.trim());
        }
    }

    /**
     * Create the security context required for SSL communication.
     * 
     * @param keyStoreFile The name of the keystore file.
     * @param keyStorePassword The password for the keystore.
     * @return The context.
     * @throws FtpConfigException Thrown on error in configuration.
     */
    public static SSLContext createSslContext(String keyStoreFile, char[] keyStorePassword)
            throws FtpConfigException {
        SSLContext sslContext = null;
        try {
            /* Get keystore file and password */
            InputStream ksInputStream = getKeyStoreInputStream(keyStoreFile);

            /*
             * Get the java keystore object an key manager. A keystore is where keys and
             * certificates are kept.
             */
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(ksInputStream, keyStorePassword);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, keyStorePassword);

            /*
             * An SSLContext is an environment for implementing JSSE. It is used to create a
             * ServerSocketFactory
             */
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(kmf.getKeyManagers(), null, null);
        } catch (KeyManagementException e) {

            throw new SecurityException("A key management authorization problem occurred.");
        } catch (FileNotFoundException e) {
            throw new SecurityException("The key store file could not be found.");
        } catch (KeyStoreException e) {
            throw new SecurityException("A key store problem occurred.");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("The hash algorithm is not supported.");
        } catch (CertificateException e) {
            throw new SecurityException("Certificate could not be loaded.");
        } catch (UnrecoverableKeyException e) {
            throw new SecurityException("Key store cannot be recovered.");
        } catch (IOException e) {
            throw new SecurityException("Reading the key store failed.");
        }
        return sslContext;
    }

    private static InputStream getKeyStoreInputStream(String ksFile) throws FileNotFoundException {
        if (ksFile == null) {
            throw new FileNotFoundException("Keystore file not defined.");
        }
        return new FileInputStream(ksFile);

    }

}
