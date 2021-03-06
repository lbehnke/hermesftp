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

package com.apporiented.hermesftp.parser;

import java.util.Map;

import com.apporiented.hermesftp.cmd.FtpCmd;

/**
 * Parser and Container class for all registered FTP commands.
 * 
 * @author Lars Behnke
 */
public interface FtpCmdParser {

    /**
     * Registers the implemented FTP commands.
     * 
     * @param map The commands.
     */
    void setCommands(Map<String, String> map);

    /**
     * Lists the known FTP commands.
     * 
     * @return The FTP commands.
     */
    String[] getCommandTokens();

    /**
     * Extracts the command token from the passed command line.
     * 
     * @param cmdString The command string.
     * @return The command token or NULL.
     */
    String findCommandToken(String cmdString);

    /**
     * Returns the command instance corresponding to the passed token.
     * 
     * @param token The command token.
     * @return The command instance.
     */
    FtpCmd createCommandByToken(String token);
}
