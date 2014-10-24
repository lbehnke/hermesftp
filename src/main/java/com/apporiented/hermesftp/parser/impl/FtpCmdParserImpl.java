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

package com.apporiented.hermesftp.parser.impl;

import java.util.Map;
import java.util.Set;

import com.apporiented.hermesftp.cmd.FtpCmd;
import com.apporiented.hermesftp.parser.FtpCmdParser;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Default implementation of a command container.
 * 
 * @author Lars Behnke
 */
public class FtpCmdParserImpl implements FtpCmdParser, BeanFactoryAware {

    private Map<String, String> commands;

    private BeanFactory         factory;

    /**
     * Constructor.
     */
    public FtpCmdParserImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void setCommands(Map<String, String> map) {
        this.commands = map;
    }

    /**
     * {@inheritDoc}
     */
    public String findCommandToken(String cmdString) {
        String result = null;
        if (cmdString != null) {
            cmdString = cmdString.trim().toUpperCase();
            Set<Map.Entry<String, String>> entrySet = commands.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                String token = entry.getKey();
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
        String cmdBeanName = commands.get(token);
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
        Set<String> keySet = commands.keySet();
        for (String key : keySet) {
            result[i++] = key;
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
