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

package de.apporiented.hermesftp.cmd.impl;

import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
import de.apporiented.hermesftp.exception.FtpCmdException;

/**
 * <b>FILE STRUCTURE (STRU)</b>
 * <p>
 * The argument is a single Telnet character code specifying file structure described in the Section
 * on Data Representation and Storage. The following codes are assigned for structure:
 * <ul>
 * <li> F - File (no record structure)
 * <li> R - Record structure
 * <li> P - Page structure
 * </ul>
 * The default structure is File.
 * <p>
 * <i>[Excerpt from RFC-959, Postel and Reynolds]</i>
 * </p>
 * 
 * @author Lars Behnke
 */
public class FtpCmdStru extends AbstractFtpCmd {

    /**
     * Currently file and record structure are supported. {@inheritDoc}
     */
    public void execute() throws FtpCmdException {
        String stru = getArguments().toUpperCase().trim();
        if ("F".equals(stru)) {
            getCtx().setStorageStructure(STRUCT_FILE);
            msgOut(MSG200);
        } else if ("R".equals(stru)) {
            getCtx().setStorageStructure(STRUCT_RECORD);
            msgOut(MSG200);
        } else if ("P".equals(stru)) {
            msgOut(MSG504);
        } else {
            msgOut(MSG501);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getHelp() {
        return "Sets the file structure";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticationRequired() {
        return true;
    }

}
