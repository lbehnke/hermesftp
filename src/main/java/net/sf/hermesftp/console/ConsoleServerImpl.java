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

package net.sf.hermesftp.console;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.exception.FtpConsoleException;
import net.sf.hermesftp.usermanager.UserManager;
import net.sf.hermesftp.usermanager.model.UserData;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Web based administration console. The console is powered by an embedded jetty web server.
 * 
 * @author Administrator
 */
public class ConsoleServerImpl implements ConsoleServer {

    private static final int DEFAULT_PORT = 9988;

    private int              port         = DEFAULT_PORT;

    private Map              servlets;

    private UserManager      userManager;

    private UserRealm        realm;

    /**
     * {@inheritDoc}
     */
    public void start() throws FtpConsoleException {

        try {
            Server server = new Server(getPort());

            ContextHandlerCollection contexts = new ContextHandlerCollection();
            server.setHandler(contexts);
            Context ctx = new Context(contexts, "/", Context.SESSIONS | Context.SECURITY);
            configureSecurity(ctx);
            configureServlets(ctx);
            server.start();
            server.join();
        } catch (Exception e) {
            throw new FtpConsoleException("Starting web server failed: " + e);
        }
    }

    private void configureServlets(Context ctx) {
        for (Iterator iter = getServlets().entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String path = entry.getKey().toString();
            Servlet servlet = (Servlet) entry.getValue();
            ctx.addServlet(new ServletHolder(servlet), path);
        }
    }

    /**
     * Configures the Console security. Per default basic authentication is enforced.
     * 
     * @param ctx The Web context.
     * @throws FtpConfigException Error in configuration.
     */
    protected void configureSecurity(Context ctx) throws FtpConfigException {
        SecurityHandler sh = ctx.getSecurityHandler();
        if (!userManager.isLoaded()) {
            userManager.load();
        }
        configureRealm(realm);
        sh.setUserRealm(realm);
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[] {ConsoleConstants.ROLE_ADMIN});
        constraint.setAuthenticate(true);
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");
        sh.setConstraintMappings(new ConstraintMapping[] {cm});
    }

    /**
     * Configures the security realm.
     * 
     * @param realm The realm to configure.
     * @throws FtpConfigException Error due to an flow in the configuration.
     */
    protected void configureRealm(UserRealm realm) throws FtpConfigException {
        if (realm instanceof HashUserRealm) {
            HashUserRealm hur = (HashUserRealm) realm;
            List users = userManager.getUserDataList();
            for (Iterator iter = users.iterator(); iter.hasNext();) {
                UserData ud = (UserData) iter.next();
                hur.put(ud.getUid(), new ConsolePassword(ud.getPassword()));
                if (ud.isAdminRole()) {
                    hur.addUserToRole(ud.getUid(), ConsoleConstants.ROLE_ADMIN);
                }
            }
        }
    }

    /**
     * Getter methode for property <code>port</code>.
     * 
     * @return Property <code>port</code>.
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter methode for property <code>port</code>.
     * 
     * @param port Value for <code>port</code>.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Getter methode for property <code>realm</code>.
     * 
     * @return Property <code>realm</code>.
     */
    public UserRealm getRealm() {
        return realm;
    }

    /**
     * Setter methode for property <code>realm</code>.
     * 
     * @param realm Value for <code>realm</code>.
     */
    public void setRealm(UserRealm realm) {
        this.realm = realm;
    }

    /**
     * Getter methode for property <code>servlets</code>.
     * 
     * @return Property <code>servlets</code>.
     */
    public Map getServlets() {
        return servlets;
    }

    /**
     * Setter methode for property <code>servlets</code>.
     * 
     * @param servlets Value for <code>servlets</code>.
     */
    public void setServlets(Map servlets) {
        this.servlets = servlets;
    }

    /**
     * Getter methode for property <code>userManager</code>.
     * 
     * @return Property <code>userManager</code>.
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Setter methode for property <code>userManager</code>.
     * 
     * @param userManager Value for <code>userManager</code>.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
