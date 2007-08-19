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

package net.sf.hermesftp.console;

import java.util.Map;
import java.util.Properties;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpSessionContext;
import net.sf.hermesftp.server.FtpServer;
import net.sf.hermesftp.session.FtpSession;

/**
 * Provides an overview of system and application properties.
 * 
 * @author Administrator
 */
public class SessionsServlet extends AbstractConsoleServlet {

    private static final long serialVersionUID = -594524060863329206L;

    private FtpServer         server;

    private String            pageTitle;

    /**
     * {@inheritDoc}
     */
    protected Properties getContentProperties() {
        Properties result = new Properties();
        int count = getServer().getConnectionCount();
        result.put("page.title", getPageTitle());
        result.put("session.count", formatNum(count));
        result.put("session.count.hwm", formatNum(getServer().getConnectionCountHWMark()));
        result.put("session.count.hwm.date", "" + formatDate(getServer().getConnectionCountHWMarkDate()));

        String table = count == 0 ? "" : createSessionTable();
        result.put("session.table", table);

        return result;
    }

    private String createSessionTable() {
        StringBuffer sb = new StringBuffer();
        fillColumnHeader(sb);

        int rowCount = 0;
        getServer().cleanUpSessions();
        synchronized (getServer()) {
            for (FtpSession session : getServer().getSessions()) {
                FtpSessionContext ctx = session.getFtpContext();
                rowCount++;
                fillRow(sb, rowCount, session, ctx);
            }
            sb.append(ConsoleConstants.TABLE_END_TAG);
        }
        return sb.toString();
    }

    private void fillRow(StringBuffer sb, int rowCount, FtpSession session, FtpSessionContext ctx) {

        String band = rowCount % 2 == 0 ? ConsoleConstants.TR_START_TAG_CLASS_A
                : ConsoleConstants.TR_START_TAG_CLASS_B;
        sb.append(band);

        fillField(sb, ctx.getClientSocket().getInetAddress().getHostName());
        fillField(sb, ctx.getUser());
        fillField(sb, session.isTerminated() ? "Terminated" : "Running");
        fillField(sb, formatDate(ctx.getCreationTime()));
        fillNumField(sb, getSessionStat(ctx, FtpConstants.STAT_BYTES_DOWNLOADED));
        fillNumField(sb, getSessionStat(ctx, FtpConstants.STAT_BYTES_UPLOADED));
        fillNumField(sb, getSessionStat(ctx, FtpConstants.STAT_FILES_DOWNLOADED));
        fillNumField(sb, getSessionStat(ctx, FtpConstants.STAT_FILES_UPLOADED));
        fillNumField(sb, getSessionStat(ctx, FtpConstants.STAT_DOWNLOAD_RATE));
        fillNumField(sb, getSessionStat(ctx, FtpConstants.STAT_UPLOAD_RATE));

        sb.append(ConsoleConstants.TR_END_TAG);
    }

    private void fillColumnHeader(StringBuffer sb) {
        sb.append(ConsoleConstants.TABLE_START_TAG);
        sb.append(ConsoleConstants.TR_START_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "IP" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "User" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "Status" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "Online since" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "DL Bytes" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "UL Bytes" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "DL Files" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "UL Files" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "DL KB/s" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TH_START_TAG + "UL KB/s" + ConsoleConstants.TH_END_TAG);
        sb.append(ConsoleConstants.TR_END_TAG);
    }

    private String getSessionStat(FtpSessionContext ctx, String key) {
        String result;
        Map<String, Long> userStats = ctx.getSessionStatistics();
        if (userStats != null) {
            Object val = userStats.get(key);
            result = val == null ? "0" : formatNum(val);
        } else {
            result = "n/a";
        }
        return result;
    }

    /**
     * Getter methode for property <code>pageTitle</code>.
     * 
     * @return Property <code>pageTitle</code>.
     */
    public String getPageTitle() {
        return pageTitle;
    }

    /**
     * Setter methode for property <code>pageTitle</code>.
     * 
     * @param pageTitle Value for <code>pageTitle</code>.
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    /**
     * Getter methode for property <code>server</code>.
     * 
     * @return Property <code>server</code>.
     */
    public FtpServer getServer() {
        return server;
    }

    /**
     * Setter methode for property <code>server</code>.
     * 
     * @param server Value for <code>server</code>.
     */
    public void setServer(FtpServer server) {
        this.server = server;
    }

}
