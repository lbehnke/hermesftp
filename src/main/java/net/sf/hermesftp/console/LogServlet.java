package net.sf.hermesftp.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

/**
 * Provides an overview of system and application properties.
 * 
 * @author Administrator
 * 
 */
public class LogServlet extends AbstractConsoleServlet {

    private static final long serialVersionUID = -594524060863329206L;

    private String  logFile = "hermesftp.log";

    public LogServlet() {
        super();
        setRefreshInterval(60);
    }
    /**
     * {@inheritDoc}
     */
    protected Properties getContentProperties() {
        Properties result =  new Properties();
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
                result.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            result.append("File " + getCanonicalPath(logFile) + " not found.");
        } catch (IOException e) {
            result.append("Readin file " + getCanonicalPath(logFile) + " failed.");
        }
        return result.toString();
    }

    private String getCanonicalPath(String filename) {
        try {
            filename = new File(filename).getCanonicalPath();
        } catch (Exception e1) {
        }
        return filename;
    }


    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }


}
