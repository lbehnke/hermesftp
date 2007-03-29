package net.sf.hermesftp.console;

import java.util.Properties;

import net.sf.hermesftp.common.FtpServerOptions;

/**
 * Provides an overview of system and application properties.
 * 
 * @author Administrator
 * 
 */
public class OverviewServlet extends AbstractConsoleServlet {

    private static final long serialVersionUID = -594524060863929206L;

    private FtpServerOptions  options;

    /**
     * {@inheritDoc}
     */
    protected Properties getContentProperties() {
        Properties result = new Properties();
        result.putAll(options.getProperties());
        result.put("memory.max", formatNum(Runtime.getRuntime().maxMemory() / 1024));
        result.put("memory.free", formatNum(Runtime.getRuntime().freeMemory() / 1024));
        result.put("memory.total", formatNum(Runtime.getRuntime().totalMemory() / 1024));
        result.put("memory.total.free", formatNum((Runtime.getRuntime().freeMemory() + (Runtime
                .getRuntime().maxMemory() - Runtime.getRuntime().totalMemory())) / 1024));
        return result;
    }

    public FtpServerOptions getOptions() {
        return options;
    }

    public void setOptions(FtpServerOptions options) {
        this.options = options;
    }

}
