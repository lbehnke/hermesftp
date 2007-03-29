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
 *
 */
public abstract class AbstractConsoleServlet extends HttpServlet {

    private static final Date         APP_START          = new Date();

    private static final String       TEXT_ENCODING      = "UTF-8";

    private String                    headerResourceName = "/console-header.html";

    private String                    footerResourceName = "/console-footer.html";

    private String                    contentResourceName;

    private static final DateFormat   DATE_FORMAT        = new SimpleDateFormat(
                                                                 "yyyy-MM-dd HH:mm:ss");

    private static final NumberFormat NUM_FORMAT         = new DecimalFormat("###,###,###,###,##0");

    private int                       refreshInterval    = 10;

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
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

    protected String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    protected String formatNum(Object num) {
        return NUM_FORMAT.format(num);
    }

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

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * @return the headerResourceName
     */
    public String getHeaderResourceName() {
        return headerResourceName;
    }

    /**
     * @param headerResourceName the headerResourceName to set
     */
    public void setHeaderResourceName(String headerResourceName) {
        this.headerResourceName = headerResourceName;
    }

    /**
     * @return the footerResourceName
     */
    public String getFooterResourceName() {
        return footerResourceName;
    }

    /**
     * @param footerResourceName the footerResourceName to set
     */
    public void setFooterResourceName(String footerResourceName) {
        this.footerResourceName = footerResourceName;
    }

    /**
     * Returns the name of the content resource file.
     * 
     * @return The content resource file.
     */
    public String getContentResourceName() {
        return contentResourceName;
    }

    /**
     * @param contentResourceName the contentResourceName to set
     */
    public void setContentResourceName(String contentResourceName) {
        this.contentResourceName = contentResourceName;
    }

}
