package net.sf.hermesftp.console;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.usermanager.UserManager;
import net.sf.hermesftp.usermanager.model.UserData;
import net.sf.hermesftp.utils.StringUtils;

/**
 * Provides an overview of system and application properties.
 * 
 * @author Administrator
 * 
 */
public class UserServlet extends AbstractConsoleServlet {

    private static final long serialVersionUID = -594524060863329206L;

    private UserManager       userManager;

    /**
     * {@inheritDoc}
     */
    protected Properties getContentProperties() {
        Properties result = new Properties();
        if (userManager != null) {
            result.put("user.table", createUserTable());
        } else {
            result.put("user.table", "n/a");
        }

        return result;
    }

    private String createUserTable() {

        List list = null;
        try {
            list = getUserManager().getUserDataList();
        } catch (Exception e) {
            return "not available";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>User ID</th>");
        sb.append("<th>Name</th>");
        sb.append("<th>Groups</th>");
        sb.append("<th>DL Bytes</th>");
        sb.append("<th>UL Bytes</th>");
        sb.append("<th>DL Files</th>");
        sb.append("<th>UL Files</th>");
        sb.append("</tr>");

        int rowCount = 0;
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            UserData user = (UserData) iter.next();

            rowCount++;
            String band = rowCount % 2 == 0 ? "a" : "b";
            sb.append("<tr class=\"" + band + "\">");

            sb.append("<td>");
            sb.append(user.getUid());
            sb.append("</td>");

            sb.append("<td>");
            sb.append(user.getFullName());
            sb.append("</td>");

            sb.append("<td>");
            sb.append(StringUtils.convertLstToString(user.getGroupNames()));
            sb.append("</td>");

            sb.append("<td class=\"number\">");
            sb.append(getUserStat(user.getUid(), FtpConstants.STAT_BYTES_DOWNLOADED));
            sb.append("</td>");

            sb.append("<td class=\"number\">");
            sb.append(getUserStat(user.getUid(), FtpConstants.STAT_BYTES_UPLOADED));
            sb.append("</td>");

            sb.append("<td class=\"number\">");
            sb.append(getUserStat(user.getUid(), FtpConstants.STAT_FILES_DOWNLOADED));
            sb.append("</td>");

            sb.append("<td class=\"number\">");
            sb.append(getUserStat(user.getUid(), FtpConstants.STAT_FILES_UPLOADED));
            sb.append("</td>");

            sb.append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    private String getUserStat(String uid, String key) {
        String result;
        Map userStats = getUserManager().getUserStatistics(uid);
        if (userStats != null) {
            Object val = userStats.get(key);
            result = val == null ? "0" : formatNum(val);
        } else {
            result = "n/a";
        }
        return result;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
