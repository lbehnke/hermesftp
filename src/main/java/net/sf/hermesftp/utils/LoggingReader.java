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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A writer that adds logging capability to a passed writer. This mechanism corresponds the
 * decorator pattern.
 *
 * @author Lars Behnke
 *
 */
public class LoggingReader
    extends BufferedReader {

    private static Log log = LogFactory.getLog(LoggingReader.class);

    private static final int LOG_LINE_LENGTH = 80;

    /**
     * Constructor.
     *
     * @param in A reader.
     */
    public LoggingReader(Reader in) {
        super(in);
    }

    /**
     * Constructor.
     *
     * @param in A reader.
     * @param sz Input buffer size.
     */
    public LoggingReader(Reader in, int sz) {
        super(in, sz);
    }

    /**
     * {@inheritDoc}
     */
    public String readLine() throws IOException {
        String result = super.readLine();
        if (log.isDebugEnabled()) {
            String x;
            if (result != null && result.length() >= LOG_LINE_LENGTH) {
                x = result.substring(0, LOG_LINE_LENGTH) + " ["
                    + (result.length() - LOG_LINE_LENGTH) + " more chars]";
            } else {
                x = result;
            }
            log.debug("<--: " + x);
        }
        return result;
    }

}
