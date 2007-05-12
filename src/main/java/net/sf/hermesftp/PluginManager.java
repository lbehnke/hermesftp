package net.sf.hermesftp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import net.sf.hermesftp.common.FtpConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class that manages the plugin classpath and class loading
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
        List jars = new LinkedList();

        StringBuffer cpExtension = new StringBuffer();
        File[] jarFiles = collectJars(new String[] { getPluginDir().getPath() });

        for (int i = 0; i < jarFiles.length; i++) {
            try {
                File file = jarFiles[i];
                jars.add(file.toURL());
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
        classLoader = new DynClassLoader((URL[]) jars.toArray(new URL[jars.size()]));

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
        Set jarList = new HashSet();
        for (int i = 0; i < paths.length; i++) {
            File dir = new File(paths[i]);
            if (log.isWarnEnabled() && !dir.exists()) {
                log.warn("JAR folder not found: " + dir);
            }
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles(new JarFileFilter());
                for (int j = 0; j < files.length; j++) {
                    jarList.add(files[j]);
                }
            }
        }
        return (File[]) jarList.toArray(new File[jarList.size()]);
    }

    public static void addResource(File jarOrPath) {
        try {
            classLoader.addURL(jarOrPath.toURL());
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
            Class clazz = classLoader.loadClass(mainClassName);
            Object instance = clazz.newInstance();
            Method startup = clazz.getMethod(startMethod,
                    new Class[] { (new String[0]).getClass() });
            startup.invoke(instance, new Object[] { args });
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    private static final class JarFileFilter implements FilenameFilter {
        public boolean accept(File f, String name) {
            return name.endsWith(JAR_EXT);
        }
    }

    private static class DynClassLoader extends URLClassLoader {

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
            for (int i = 0; i < urls.length; i++) {
                JarInputStream jis = new JarInputStream(urls[0].openStream());
                JarEntry entry = jis.getNextJarEntry();
                int loadedCount = 0, totalCount = 0;

                while ((entry = jis.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        totalCount++;
                        name = name.substring(0, name.length() - 6);
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
