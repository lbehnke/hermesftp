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

package net.sf.hermesftp.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.hermesftp.utils.IOUtils;
import net.sf.hermesftp.utils.VarMerger;

/**
 * Abstract super class of the servlets of the FTP console.
 * 
 * @author Administrator
 */
public abstract class AbstractConsoleServlet extends HttpServlet {
    
    private static final int          DEFAULT_REFRESH_SECONDS = 10;

    private static final DateFormat   DATE_FORMAT             = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final NumberFormat NUM_FORMAT              = new DecimalFormat("###,###,###,###,##0");

    private static final Date         APP_START               = new Date();

    private static final String       TEXT_ENCODING           = "UTF-8";

    private String                    headerResourceName      = "/console-header.html";

    private String                    footerResourceName      = "/console-footer.html";

    private String                    contentResourceName;

    private int                       refreshInterval         = DEFAULT_REFRESH_SECONDS;

    /**
     * {@inheritDoc}
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        String page = readPage();
        VarMerger vm = new VarMerger(page);
        Properties p = new Properties();
        p.putAll(System.getProperties());
        p.putAll(IOUtils.getAppProperties());
        p.putAll(getContentProperties());
        p.put("current.date", "" + formatDate(new Date()));
        p.put("startup.date", "" + formatDate(APP_START));
        p.put("refresh.interval", "" + getRefreshInterval());
        vm.merge(p);
        page = vm.getText();
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.write(page);

    }

    private String readPage() throws IOException {
        StringBuffer responseBuffer = new StringBuffer();
        String part;
        part = IOUtils.loadTextResource(getHeaderResourceName(), TEXT_ENCODING);
        responseBuffer.append(part);
        part = IOUtils.loadTextResource(getContentResourceName(), TEXT_ENCODING);
        responseBuffer.append(part);
        part = IOUtils.loadTextResource(getFooterResourceName(), TEXT_ENCODING);
        responseBuffer.append(part);
        return responseBuffer.toString();
    }

    /**
     * Creates an HTML table fied (TD element).
     * @param sb The stringbuffer to write to.
     * @param value The value to fill.
     */
    protected void fillField(StringBuffer sb, String value) {
        sb.append(ConsoleConstants.TD_START_TAG);
        sb.append(value);
        sb.append(ConsoleConstants.TD_END_TAG);
    }
    
    /**
     * Creates an HTML table fied (TD element).
     * @param sb The stringbuffer to write to.
     * @param value The numeric value to fill.
     */
    protected void fillNumField(StringBuffer sb, String value) {
        sb.append(ConsoleConstants.TD_START_TAG_NUM);
        sb.append(value);
        sb.append(ConsoleConstants.TD_END_TAG);
    }
    
    /**
     * Formats a date value.
     * 
     * @param date The date to format.
     * @return The formatted date.
     */
    protected String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * Formats a numeric value.
     * 
     * @param num The number to format.
     * @return The formatted number.
     */
    protected String formatNum(Object num) {
        return NUM_FORMAT.format(num);
    }

    /**
     * Formats a long value.
     * 
     * @param num The number to format.
     * @return The formatted number.
     */
    protected String formatNum(long num) {
        return NUM_FORMAT.format(num);
    }

    /**
     * The content properties object. The properties object represents a mapping of placeholder keys
     * and their corresponding values.
     * 
     * @return The properties.
     */
    protected abstract Properties getContentProperties();

    /**
     * Getter methode for property <code>contentResourceName</code>.
     * 
     * @return Property <code>contentResourceName</code>.
     */
    public String getContentResourceName() {
        return contentResourceName;
    }

    /**
     * Setter methode for property <code>contentResourceName</code>.
     * 
     * @param contentResourceName Value for content resource name.
     */
    public void setContentResourceName(String contentResourceName) {
        this.contentResourceName = contentResourceName;
    }

    /**
     * Getter methode for property <code>footerResourceName</code>.
     * 
     * @return Property <code>footerResourceName</code>.
     */
    public String getFooterResourceName() {
        return footerResourceName;
    }

    /**
     * Setter methode for property <code>footerResourceName</code>.
     * 
     * @param footerResourceName Value for <code>footerResourceName</code>.
     */
    public void setFooterResourceName(String footerResourceName) {
        this.footerResourceName = footerResourceName;
    }

    /**
     * Getter methode for property <code>headerResourceName</code>.
     * 
     * @return Property <code>headerResourceName</code>.
     */
    public String getHeaderResourceName() {
        return headerResourceName;
    }

    /**
     * Setter methode for property <code>headerResourceName</code>.
     * 
     * @param headerResourceName Value for <code>headerResourceName</code>.
     */
    public void setHeaderResourceName(String headerResourceName) {
        this.headerResourceName = headerResourceName;
    }

    /**
     * Getter methode for property <code>refreshInterval</code>.
     * 
     * @return Property <code>refreshInterval</code>.
     */
    public int getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Setter methode for property <code>refreshInterval</code>.
     * 
     * @param refreshInterval Value for <code>refreshInterval</code>.
     */
    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

}
