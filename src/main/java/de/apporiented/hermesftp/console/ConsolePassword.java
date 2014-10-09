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

package de.apporiented.hermesftp.console;

import java.security.NoSuchAlgorithmException;

import de.apporiented.hermesftp.utils.SecurityUtil;
import de.apporiented.hermesftp.utils.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.security.Password;

/**
 * The class encapsulating the password required to access the web console.
 * 
 * @author Behnke
 */
public class ConsolePassword extends Password {

    private static Log log = LogFactory.getLog(ConsolePassword.class);

    private String     ftpPassword;

    /**
     * Constructor.
     * 
     * @param password The String password.
     */
    public ConsolePassword(String password) {
        super(password);
        ftpPassword = password;
        while (ftpPassword != null && ftpPassword.startsWith(__OBFUSCATE)) {
            ftpPassword = deobfuscate(ftpPassword);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean check(Object credentials) {
        if (this == credentials) {
            return true;
        }
        if (credentials == null) {
            return false;
        }

        if (!(credentials instanceof ConsolePassword)) {
            boolean result = false;
            String password = credentials.toString();
            if (ftpPassword == null) {
                result = password != null && StringUtils.validateEmail(password);
            } else {
                try {
                    result = SecurityUtil.checkPassword(ftpPassword, password);
                } catch (NoSuchAlgorithmException e) {
                    log.error("Algorithm not supported: " + ftpPassword);
                }
            }
            return result;
        } else {
            return super.check(credentials);
        }

    }

}
