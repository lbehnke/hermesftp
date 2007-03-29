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

package net.sf.hermesftp.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * This class contains some functionallity concerning I/O communication not covered by
 * {@link org.apache.commons.io.IOUtils}.
 * 
 * @author Lars Behnke
 * 
 */
public final class IOUtils {

    private static final int        FILE_SIZE_LENGTH_UNIX = 11;

    private static final DateFormat DATE_FORMAT_UNIX      = new SimpleDateFormat("MMM dd HH:mm",
                                                                  Locale.US);

    private static final String     APP_PROPERTIES        = "/app.properties";

    private static Properties       appProperties;

    /**
     * Constructor hidden.
     */
    private IOUtils() {
        super();
    }

    /**
     * Closes a file, socket, stream, writer etc. without throwing exceptions. Any exception is
     * catched and logged. In contrast to the "Commons IO" method <code>closeQuietly()</code> this
     * method closes any object that has a <code>close()</code>-method and is not restricted to
     * readers, writers and streams.
     * 
     * @param o The object to be closed.
     * @return True, if object could be closed.
     */
    public static boolean closeGracefully(Object o) {
        boolean result;
        try {
            Method closeMethod = BeanUtils.findMethod(o.getClass(), "close", null);
            closeMethod.invoke(o, null);
            result = true;
        } catch (IllegalArgumentException e) {
            result = false;
        } catch (IllegalAccessException e) {
            result = false;
        } catch (InvocationTargetException e) {
            result = false;
        } catch (NullPointerException e) {
            result = false;
        }
        return result;
    }

    /**
     * Returns a line formated directory entry. The permissions are set as follows: The passed read
     * flag is relevant for owner, group and others. The passed write flag is only relevant for the
     * owner.
     * 
     * @param file The file to be formatted.
     * @param read True if readable.
     * @param write True if writable.
     * @return The formatted line.
     */
    public static String formatUnixFtpFileInfo(File file, boolean read, boolean write) {
        long size;
        StringBuffer sb = new StringBuffer();
        String wFlag = write ? "w" : "-";
        String rFlag = read ? "r" : "-";
        String permflags;
        if (file.isDirectory()) {
            permflags = MessageFormat.format("d{0}{1}x{0}-x{0}-x", new Object[] { rFlag, wFlag });
            size = 0;
        } else {
            permflags = MessageFormat.format("-{0}{1}-{0}--{0}--", new Object[] { rFlag, wFlag });
            size = file.length();
        }
        Date date = new Date(file.lastModified());
        sb.append(permflags);
        sb.append(" 1 ftp ftp ");
        sb.append(StringUtils.leftPad("" + size, FILE_SIZE_LENGTH_UNIX));
        sb.append(" ");
        sb.append(DATE_FORMAT_UNIX.format(date));
        sb.append(" ");
        sb.append(file.getName());
        return sb.toString();
    }

    /**
     * Reads an arbitrary text resource from the class path.
     * 
     * @param name
     * @param encoding
     * @return The text.
     * @throws IOException
     */
    public static String loadTextResource(String name, String encoding) throws IOException {
        String result = null;
        StringWriter sw = null;
        InputStreamReader isr = null;
        if (encoding == null) {
            encoding = "UTF-8";
        }
        try {
            InputStream is = IOUtils.class.getResourceAsStream(name);
            isr = new InputStreamReader(is, encoding);
            sw = new StringWriter();
            int c;
            while ((c = isr.read()) != -1) {
                sw.write(c);
            }
            result = sw.getBuffer().toString();
        } finally {
            closeGracefully(isr);
            closeGracefully(sw);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public static Properties getAppProperties() {
        if (appProperties == null) {
            appProperties = new Properties();
            InputStream is = IOUtils.class.getResourceAsStream(APP_PROPERTIES);
            try {
                appProperties.load(is);
            } catch (IOException e) {
            }
        }
        return appProperties;
    }
}
