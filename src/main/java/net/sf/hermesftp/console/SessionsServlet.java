package net.sf.hermesftp.console;

import java.util.Iterator;
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
 * 
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
        result.put("session.count.hwm.date", ""
                + formatDate(getServer().getConnectionCountHWMarkDate()));
        
        String table = count == 0 ? "" : createSessionTable();
        result.put("session.table", table);

        return result;
    }

    private String createSessionTable() {
        StringBuffer sb = new StringBuffer();
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>IP</th>");
        sb.append("<th>User</th>");
        sb.append("<th>Status</th>");
        sb.append("<th>Online since</th>");
        sb.append("<th>DL Bytes</th>");
        sb.append("<th>UL Bytes</th>");
        sb.append("<th>DL Files</th>");
        sb.append("<th>UL Files</th>");
        sb.append("</tr>");

        int rowCount = 0;
        getServer().cleanUpSessions();
        synchronized (getServer()) {
            for (Iterator iter = getServer().getSessions().iterator(); iter.hasNext();) {
                FtpSession session = (FtpSession) iter.next();
                FtpSessionContext ctx = session.getFtpContext();
                rowCount++;
                String band = rowCount % 2 == 0 ? "a" : "b";
                sb.append("<tr class=\"" + band + "\">");

                sb.append("<td>");
                sb.append(ctx.getClientSocket().getInetAddress().getHostName());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(ctx.getUser());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(session.isTerminated() ? "Terminated" : "Running");
                sb.append("</td>");

                sb.append("<td>");
                sb.append(formatDate(ctx.getCreationTime()));
                sb.append("</td>");

                sb.append("<td class=\"number\">");
                sb.append(getSessionStat(ctx, FtpConstants.STAT_BYTES_DOWNLOADED));
                sb.append("</td>");

                sb.append("<td class=\"number\">");
                sb.append(getSessionStat(ctx, FtpConstants.STAT_BYTES_UPLOADED));
                sb.append("</td>");

                sb.append("<td class=\"number\">");
                sb.append(getSessionStat(ctx, FtpConstants.STAT_FILES_DOWNLOADED));
                sb.append("</td>");

                sb.append("<td class=\"number\">");
                sb.append(getSessionStat(ctx, FtpConstants.STAT_FILES_UPLOADED));
                sb.append("</td>");

                sb.append("</tr>");
            }

            sb.append("</table>");
        }
        
       
        return sb.toString();
    }

    private String getSessionStat(FtpSessionContext ctx, String key) {
        String result;
        Map userStats = ctx.getSessionStatistics();
        if (userStats != null) {
            Object val = userStats.get(key);
            result = val == null ? "0" : formatNum(val);
        } else {
            result = "n/a";
        }
        return result;
    }

    public FtpServer getServer() {
        return server;
    }

    public void setServer(FtpServer server) {
        this.server = server;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

}
