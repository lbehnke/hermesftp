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

package net.sf.hermesftp;

import java.io.File;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Set;

import net.sf.hermesftp.common.BeanConstants;
import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.common.FtpServerOptions;
import net.sf.hermesftp.console.ConsoleServer;
import net.sf.hermesftp.server.FtpServer;
import net.sf.hermesftp.utils.IOUtils;
import net.sf.hermesftp.utils.NetUtils;
import net.sf.hermesftp.utils.SecurityUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Hermes FTP application.
 * 
 * @author Lars Behnke
 */
public final class FtpServerApp {

    private static final int THREAD_ALIVE_CHECK_INTERVAL = 1000;

    private static final int PASSWORD_ARG_COUNT          = 3;

    private static Log       log                         = LogFactory.getLog(FtpServerApp.class);

    /**
     * Constructor.
     */
    public FtpServerApp() {
        super();
    }

    /**
     * Entry point of the application.
     *
     * @param args Optionally the bean resource file can be passed.
     */
    public static void main(String[] args) {
        // TODO Use commons-cli
        if (args.length > 0 && args[0].trim().equalsIgnoreCase("-password")) {
            generatePassword(args);
        } else {
            PluginManager.startApplication(FtpServerApp.class.getName(), "startServer", args);
        }
    }

    private static void generatePassword(String[] args) {
        if (args.length != PASSWORD_ARG_COUNT) {
            System.err
                    .println("Please adhere to the following synthax: FtpServerApp password <password> <algorithm>");
            return;
        }
        String password = args[1];
        String algorithm = args[2];
        try {
            String hash = SecurityUtil.encodePassword(password, algorithm);
            System.out.print("Hash: " + hash + "\n");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("ERROR: " + e);
        }
    }

    public void startServer(String[] args) {
        if (!NetUtils.isSSLAvailable()) {
            System.exit(1);
        }
        String beanRes = args.length > 0 ? args[0] : FtpConstants.DEFAULT_BEAN_RES;
        File file = new File(beanRes);

        log.info("Hermes Home: " + IOUtils.getHomeDir());

        log.info("Application context: " + file);
        if (file != null && file.getParent() != null) {
            System.setProperty("hermes.ctx.dir", file.getParent());
            log.info("Application context path: " + file.getParent());
        }

        /* Prepare three main threads */
        ApplicationContext appContext = getApplicationContext(beanRes, file);
        FtpServer svr = (FtpServer) appContext.getBean(BeanConstants.BEAN_SERVER);
        FtpServer sslsvr = (FtpServer) appContext.getBean(BeanConstants.BEAN_SSL_SERVER);
        ConsoleServer console = (ConsoleServer) appContext.getBean(BeanConstants.BEAN_CONSOLE);

        /* Log settings */
        log.info(svr.getOptions().getAppTitle());
        log.info("Version " + svr.getOptions().getAppVersion());
        log.info("Build info: " + svr.getOptions().getAppBuildInfo());
        printOptions(svr.getOptions());

        /* Check local ip addresses */
        InetAddress addr = NetUtils.getMachineAddress(true);
        if (addr == null) {
            log.error("No local network ip address available.");
            System.exit(1);
        }
        log.info("Local ip address: " + addr);

        /* Start servers */
        Thread svrThread;
        Thread sslSvrThread;
        try {
            svrThread = new Thread(svr);
            svrThread.start();
            sslSvrThread = new Thread(sslsvr);
            sslSvrThread.start();

            /* Start web console */
            if (svr.getOptions().getBoolean("console.enabled", false)) {
                console.start();
            }

            /* Waiting... */
            while (svrThread.isAlive() || sslSvrThread.isAlive()) {
                try {
                    // TODO Replace by wait/NotifyAll
                    Thread.sleep(THREAD_ALIVE_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
    }

    private static ApplicationContext getApplicationContext(String beanRes, File file) {
        ApplicationContext appContext;
        if (file.exists()) {
            appContext = new FileSystemXmlApplicationContext(new String[] { beanRes });
        } else {
            log.error("Hermes FTP application context not found: " + file
                    + ". Trying to read context from classpath...");
            appContext = new ClassPathXmlApplicationContext(new String[] { "/"
                    + FtpConstants.DEFAULT_BEAN_RES });
        }
        return appContext;
    }

    private static void printOptions(FtpServerOptions aOptions) {
        log.info("Ftp server options:");
        Set keyset = aOptions.getProperties().keySet();
        for (Iterator iter = keyset.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String value = aOptions.getProperty(key);
            log.info("    " + key + ": " + value);
        }
    }
}
