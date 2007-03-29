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

package net.sf.hermesftp.parser.impl;

import java.util.Iterator;
import java.util.Map;

import net.sf.hermesftp.cmd.FtpCmd;
import net.sf.hermesftp.parser.FtpCmdParser;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Default implementation of a command container.
 *
 * @author Lars Behnke
 *
 */
public class FtpCmdParserImpl
    implements FtpCmdParser, BeanFactoryAware {

    private Map commands;

    private BeanFactory factory;

    /**
     * Constructor.
     */
    public FtpCmdParserImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void setCommands(Map map) {
        this.commands = map;
    }

    /**
     * {@inheritDoc}
     */
    public String findCommandToken(String cmdString) {
        String result = null;
        if (cmdString != null) {
            cmdString = cmdString.trim().toUpperCase();
            for (Iterator iter = commands.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String token = entry.getKey().toString();
                if (cmdString.startsWith(token)) {
                    result = token;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public FtpCmd createCommandByToken(String token) {
        FtpCmd result = null;
        String cmdBeanName = (String) commands.get(token);
        if (cmdBeanName != null) {
            result = (FtpCmd) factory.getBean(cmdBeanName);
        }
        if (result != null) {
            result.setToken(token);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getCommandTokens() {
        String[] result = new String[commands.size()];
        int i = 0;
        for (Iterator iter = commands.keySet().iterator(); iter.hasNext();) {
            result[i++] = (String) iter.next();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanFactory(BeanFactory f) {
        this.factory = f;

    }

}
