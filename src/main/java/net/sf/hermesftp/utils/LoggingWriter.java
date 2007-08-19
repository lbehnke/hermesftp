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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class that logs server responses.
 *
 * @author Lars Behnke
 */
public class LoggingWriter
    extends PrintWriter {

    private static final int LOG_LINE_LENGTH = 80;

    private static Log log = LogFactory.getLog(LoggingWriter.class);

    /**
     * Constructor.
     *
     * @param out The output stream.
     * @param flush Automatic flush
     */
    public LoggingWriter(OutputStream out, boolean flush) {
        super(out, flush);
    }
    
    /**
     * Constructor.
     *
     * @param out The output writer.
     * @param flush Automatic flush
     */
    public LoggingWriter(Writer out, boolean flush) {
        super(out, flush);
    }

    /**
     * {@inheritDoc}
     */
    public void println(String text) {
        if (log.isDebugEnabled()) {
            String x;
            if (text != null && text.length() >= LOG_LINE_LENGTH) {
                x = text.substring(0, LOG_LINE_LENGTH) + " [" + (text.length() - LOG_LINE_LENGTH)
                    + " more chars]";
            } else {
                x = text;
            }
            log.debug("-->: " + x);
        }
        super.println(text);
//        if (checkError()) {
//            log.debug("Writing to control stream failed.");
//        }
    }
}
