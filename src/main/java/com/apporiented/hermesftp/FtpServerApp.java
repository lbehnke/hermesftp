/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2014 Lars Behnke
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

package com.apporiented.hermesftp;

import java.io.File;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.apporiented.hermesftp.common.BeanConstants;
import com.apporiented.hermesftp.common.FtpConstants;
import com.apporiented.hermesftp.common.FtpServerOptions;
import com.apporiented.hermesftp.console.ConsoleServer;
import com.apporiented.hermesftp.server.FtpServer;
import com.apporiented.hermesftp.utils.IOUtils;
import com.apporiented.hermesftp.utils.NetUtils;
import com.apporiented.hermesftp.utils.SecurityUtil;

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

    //private static final int THREAD_ALIVE_CHECK_INTERVAL = 1000;

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
            log.info("Starting Hermes FTP Server...");
            PluginManager.startApplication(FtpServerApp.class.getName(), "startServer", args);
            log.info("Hermes FTP Server ready.");
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
            String hash = SecurityUtil.digestPassword(password, algorithm);
            System.out.print("Hash: " + hash + "\n");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Starts the FTP servers(s).
     * 
     * @param args The arguments passed with main method.
     */
    public void startServer(String[] args) {
        if (!NetUtils.isSSLAvailable()) {
            System.exit(1);
        }
        String beanRes = args.length > 0 ? args[0] : FtpConstants.DEFAULT_BEAN_RES;
        File file = new File(beanRes);

        logPaths(file);

        /* Prepare three main threads */
        ApplicationContext appContext = getApplicationContext(beanRes, file);
        FtpServer svr = (FtpServer) appContext.getBean(BeanConstants.BEAN_SERVER);
        FtpServer sslsvr = (FtpServer) appContext.getBean(BeanConstants.BEAN_SSL_SERVER);
        ConsoleServer console = (ConsoleServer) appContext.getBean(BeanConstants.BEAN_CONSOLE);

        /* Log settings */

        logOptions(svr.getOptions());

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
            
            /* Register Shutdown Hook */
            List<FtpServer> serverList = new ArrayList<FtpServer>();
            serverList.add(svr);
            serverList.add(sslsvr);
            addShutdownHook(serverList);
            
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
    }

    /**
     * Add shutdown hook.
     */
    private static void addShutdownHook(final List<FtpServer> servers) {

        Runnable shutdownHook = new Runnable() {
            public void run() {
                for (FtpServer ftpServer : servers) {
                    log.info("Stopping server '" + ftpServer.getName() + "'.");
                    ftpServer.abort();
                }
                log.info("All servers down.");
            }
        };
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(shutdownHook));
    }
    
    private void logPaths(File file) {
        log.info("Hermes Home: " + IOUtils.getHomeDir());

        log.info("Application context: " + file);
        if (file != null && file.getParent() != null) {
            System.setProperty("hermes.ctx.dir", file.getParent());
            log.info("Application context path: " + file.getParent());
        }
    }

    private static ApplicationContext getApplicationContext(String beanRes, File file) {
        ApplicationContext appContext;
        if (file.exists()) {
            appContext = new FileSystemXmlApplicationContext(new String[] {beanRes});
        } else {
            log.error("Hermes FTP application context not found: " + file
                    + ". Trying to read context from classpath...");
            appContext = new ClassPathXmlApplicationContext(
                new String[] {"/" + FtpConstants.DEFAULT_BEAN_RES});
        }
        return appContext;
    }

    private static void logOptions(FtpServerOptions aOptions) {
        log.info(aOptions.getAppTitle());
        log.info("Version " + aOptions.getAppVersion());
        log.info("Build info: " + aOptions.getAppBuildInfo());
        log.info("OS name: " + System.getProperty("os.name"));
        log.info("OS file encoding (System): " + System.getProperty( "file.encoding"));
        log.info("OS file encoding (NIO): " + Charset.defaultCharset().name());
        log.info("Ftp server options:");
        Set<Object> keyset = aOptions.getProperties().keySet();
        for (Object key : keyset) {
            String value = aOptions.getProperty(key.toString());
            log.info("    " + key + ": " + value);
        }
    }
}
