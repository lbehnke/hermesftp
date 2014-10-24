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
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.apporiented.hermesftp.common.FtpConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class that manages the plug-in classpath and class loading.
 * 
 * @author Lars Behnke.
 */
public final class PluginManager {

    private static final String   PATH_SEPARATOR  = "path.separator";

    private static final String   JAVA_CLASS_PATH = "java.class.path";

    private static final String   JAR_EXT         = ".jar";

    private static Log            log             = LogFactory.getLog(PluginManager.class);

    private static DynClassLoader classLoader;

    static {
        List<URL> jars = new ArrayList<URL>();

        StringBuffer cpExtension = new StringBuffer();
        File[] jarFiles = collectJars(new String[] {getPluginDir().getPath()});

        for (File jarFile : jarFiles) {
            try {
                File file = jarFile;
                jars.add(file.toURI().toURL());
                cpExtension.append(System.getProperty(PATH_SEPARATOR));
                cpExtension.append(file.toString());
            } catch (MalformedURLException e) {
                log.error(e);
            }
        }
        String cp = System.getProperty(JAVA_CLASS_PATH) + cpExtension.toString();
        System.setProperty(JAVA_CLASS_PATH, cp);
        if (log.isDebugEnabled()) {
            log.debug("Classpath: " + cp);
        }
        classLoader = new DynClassLoader(jars.toArray(new URL[jars.size()]));

        /*
         * Necessary for Spring to find plugin classes. Don't do this in servlet/ejb container!!
         */
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * Hidden constructor.
     */
    private PluginManager() {
    }

    private static File[] collectJars(String[] paths) {
        Set<File> jarList = new HashSet<File>();
        for (String path : paths) {
            File dir = new File(path);
            if (log.isWarnEnabled() && !dir.exists()) {
                log.warn("JAR folder not found: " + dir);
            }
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles(new JarFileFilter());
                Collections.addAll(jarList, files);
            }
        }
        return jarList.toArray(new File[jarList.size()]);
    }

    /**
     * Adds a resource to the classpath. Note that the resource is not available before the update
     * method of the classloader is called.
     * 
     * @param jarOrPath The resource to add.
     */
    public static void addResource(File jarOrPath) {
        try {
            classLoader.addURL(jarOrPath.toURI().toURL());
        } catch (MalformedURLException e) {
            log.error(e);
        }
    }

    /**
     * Get the directory where plugins are installed.
     * 
     * @return the directory where plugins are installed.
     */
    public static File getPluginDir() {
        String dir = System.getProperty(FtpConstants.HERMES_HOME);
        if (StringUtils.isEmpty(dir)) {
            dir = System.getProperty("user.dir");
        }
        File file = new File(dir, "plugins");
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            log.debug("No canonical path: " + file);
        }
        return file;
    }

    /**
     * Need to be invoked from the application's main in order to utilize the dynamic class loader.
     * 
     * @param mainClassName Main class.
     * @param startMethod Start method that accepts the main arguments.
     * @param args Array of optional arguments
     */
    public static void startApplication(String mainClassName, String startMethod, String[] args) {
        try {
            classLoader.update();
            Class<?> clazz = classLoader.loadClass(mainClassName);
            Object instance = clazz.newInstance();
            Method startup = clazz.getMethod(startMethod, new Class[] {(new String[0]).getClass()});
            startup.invoke(instance, new Object[] {args});
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * Filter for JAR files.
     * 
     * @author developer
     */
    private static final class JarFileFilter implements FilenameFilter {
        public boolean accept(File f, String name) {
            return name.endsWith(JAR_EXT);
        }
    }

    /**
     * Class loader that is capable of adding new resources on the fly. In contrast to the default
     * class loader the addURL method is public. The approach was inspired by the Apache JMeter
     * project.
     * 
     * @author developer
     */
    private static class DynClassLoader extends URLClassLoader {

        private static final int EXTENSION_LENGTH = 6;

        public DynClassLoader(URL[] urls) {
            super(urls);
        }

        /**
         * @see java.net.URLClassLoader#addURL(java.net.URL)
         */
        public void addURL(URL url) {
            super.addURL(url);
        }

        /**
         * Loads all class files known to the class loader.
         */
        public void update() {
            try {
                loadClasses(classLoader.getURLs());
            } catch (IOException e) {
                log.error("Loading classes failed: " + e);
            }
        }

        private void loadClasses(URL[] urls) throws IOException {
            for (URL url : urls) {
                JarInputStream jis = new JarInputStream(urls[0].openStream());
                JarEntry entry = jis.getNextJarEntry();
                int loadedCount = 0;
                int totalCount = 0;

                while ((entry = jis.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        totalCount++;
                        name = name.substring(0, name.length() - EXTENSION_LENGTH);
                        name = name.replace('/', '.');

                        try {
                            classLoader.loadClass(name);
                            log.debug("Plugin class " + name + "\t- loaded");
                            loadedCount++;
                        } catch (Throwable e) {
                            log.debug("Plugin class " + name + "\t- not loaded - " + e);
                        }

                    }

                }
                log.debug("Classes loaded: " + loadedCount);
            }
        }
    }

}
