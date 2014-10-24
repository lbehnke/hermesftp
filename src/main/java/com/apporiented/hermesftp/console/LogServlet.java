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

package com.apporiented.hermesftp.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

/**
 * Provides an overview of system and application properties.
 * 
 * @author Administrator
 */
public class LogServlet extends AbstractConsoleServlet {

    private static final int  DEFAULT_REFRESH_INTERVAL_SEC = 60;

    private static final long serialVersionUID             = -594524060863329206L;

    private String            logFile                      = "hermesftp.log";

    /**
     * Constructor.
     */
    public LogServlet() {
        super();
        setRefreshInterval(DEFAULT_REFRESH_INTERVAL_SEC);
    }

    /**
     * {@inheritDoc}
     */
    protected Properties getContentProperties() {
        Properties result = new Properties();
        result.put("log.file.name", logFile);
        result.put("log.file.content", getLogFileContent());
        return result;
    }

    private String getLogFileContent() {
        StringBuffer result = new StringBuffer();
        try {
            RandomAccessFile raf = new RandomAccessFile(logFile, "r");
            String line;
            while ((line = raf.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            result.append("File ").append(getCanonicalPath(logFile)).append(" not found.");
        } catch (IOException e) {
            result.append("Readin file ").append(getCanonicalPath(logFile)).append(" failed.");
        }
        return result.toString();
    }

    private String getCanonicalPath(String filename) {
        try {
            filename = new File(filename).getCanonicalPath();
        } catch (IOException e1) {
            log("Canonical path cannot be resolved.");
        }
        return filename;
    }

    /**
     * Getter methode for property <code>logFile</code>.
     * 
     * @return Property <code>logFile</code>.
     */
    public String getLogFile() {
        return logFile;
    }

    /**
     * Setter methode for property <code>logFile</code>.
     * 
     * @param logFile Value for <code>logFile</code>.
     */
    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }


}
