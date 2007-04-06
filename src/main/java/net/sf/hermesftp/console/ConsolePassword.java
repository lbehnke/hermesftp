package net.sf.hermesftp.console;

import java.security.NoSuchAlgorithmException;

import net.sf.hermesftp.utils.SecurityUtil;
import net.sf.hermesftp.utils.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.security.Password;

public class ConsolePassword extends Password {

    private static Log log = LogFactory.getLog(ConsolePassword.class);
    private String ftpPassword;

    /**
     * Constructor.
     * 
     * @param password The String password.
     */
    public ConsolePassword(String password) {
        super(password);
        ftpPassword = password;
        while (ftpPassword != null && ftpPassword.startsWith(__OBFUSCATE))
            ftpPassword = deobfuscate(ftpPassword);
    }

    public boolean check(Object credentials) {
        if (this == credentials)
            return true;
        if (credentials == null) {
            return false;
        }

        if (!(credentials instanceof ConsolePassword)) {
            boolean result = false;
            String password = credentials.toString();
            if (ftpPassword == null) {
                result = password != null && StringUtils.validateEmail(password);
            } else {
                try {
                    result = SecurityUtil.checkPassword(ftpPassword, password);
                } catch (NoSuchAlgorithmException e) {
                    log.error("Algorithm not supported: " + ftpPassword);
                }
            }
            return result;
        } else {
            return super.check(credentials);
        }

    }

}
