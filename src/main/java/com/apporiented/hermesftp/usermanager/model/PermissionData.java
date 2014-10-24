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

package com.apporiented.hermesftp.usermanager.model;

import java.util.Properties;

import com.apporiented.hermesftp.exception.FtpConfigException;
import com.apporiented.hermesftp.utils.VarMerger;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.AntPathMatcher;

/**
 * Represents the permission on one or more file pathes. The paths a configured using ant style path
 * naming conventions.
 *
 * @author Lars Behnke
 *
 */
public class PermissionData {

    private String template;

    private int permission;

    /**
     * Getter method for the java bean <code>permission</code>.
     *
     * @return Returns the value of the java bean <code>permission</code>.
     */
    public int getPermission() {
        return permission;
    }

    /**
     * Setter method for the java bean <code>permission</code>.
     *
     * @param permission The value of permission to set.
     */
    public void setPermission(int permission) {
        this.permission = permission;
    }

    /**
     * Getter method for the java bean <code>template</code>.
     *
     * @return Returns the value of the java bean <code>template</code>.
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Setter method for the java bean <code>template</code>.
     *
     * @param template The value of template to set.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Fills the placeholders in the path template and checks if the passed path matches the
     * template.
     *
     * @param checkPath The path to check.
     * @param ftproot The ftp root folder.
     * @param username The username.
     * @return True, if the path matches the configured pattern.
     * @throws FtpConfigException Error on reading or processing a configuration file.
     */
    public boolean matches(String checkPath, String ftproot, String username)
                                                                             throws FtpConfigException {
        if (checkPath == null) {
            return false;
        }
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String antPath = replacePlaceholders(ftproot, username);
        antPath = FilenameUtils.normalizeNoEndSeparator(antPath);
        antPath = FilenameUtils.separatorsToUnix(antPath);
        checkPath = FilenameUtils.normalizeNoEndSeparator(checkPath);
        checkPath = FilenameUtils.separatorsToUnix(checkPath);
        return pathMatcher.match(antPath, checkPath);

    }

    private String replacePlaceholders(String ftproot, String username) throws FtpConfigException {
        VarMerger varMerger = new VarMerger(getTemplate());
        Properties props = new Properties();
        props.setProperty("ftproot", FilenameUtils.separatorsToUnix(ftproot));
        props.setProperty("user", username);
        varMerger.merge(props);
        if (!varMerger.isReplacementComplete()) {
            throw new FtpConfigException("Unresolved placeholders in user configuration file found.");
        }
        return varMerger.getText();
    }
}
