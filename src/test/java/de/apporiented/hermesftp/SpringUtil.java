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

package de.apporiented.hermesftp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.apporiented.hermesftp.common.FtpConstants;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Helper class for accessing the spring application context.
 * 
 * @author Lars Behnke
 */
public final class SpringUtil implements FtpConstants {

    private static Map<String, ApplicationContext> appCtx = Collections
                                                              .synchronizedMap(new HashMap<String, ApplicationContext>());

    /**
     * Constructor hidden.
     */
    private SpringUtil() {
        super();
    }

    /**
     * Returns the bean factory that uses a given application context.
     * 
     * @param res Application context file.
     * @return Die Beanfactory des Spring-Frameworks.
     */
    public static ApplicationContext getApplicationContext(String res) {
        ApplicationContext applicationContext = appCtx.get(res);
        if (applicationContext == null) {
            String[] rs = new String[] {res};
            applicationContext = new ClassPathXmlApplicationContext(rs);
            appCtx.put(res, applicationContext);
        }
        return applicationContext;
    }

    /**
     * Returns the bean factory that uses the default application context.
     * 
     * @return Die Beanfactory des Spring-Frameworks.
     */
    public static ApplicationContext getApplicationContext() {
        return getApplicationContext(DEFAULT_BEAN_RES);
    }

    /**
     * Convenience method for accessing beans.
     * 
     * @param name Name des Beans in der Spring-Konfiguration.
     * @return Das Bean.
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * Convenience method for accessing beans.
     * 
     * @param appCtx Application context file name.
     * @param name Name des Beans in der Spring-Konfiguration.
     * @return Das Bean.
     */
    public static Object getBean(String appCtx, String name) {
        return getApplicationContext(appCtx).getBean(name);
    }

}
