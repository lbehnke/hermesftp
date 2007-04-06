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

package net.sf.hermesftp.usermanager.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.usermanager.model.GroupData;
import net.sf.hermesftp.usermanager.model.PermissionData;
import net.sf.hermesftp.usermanager.model.UserData;
import net.sf.hermesftp.usermanager.model.UserManagerData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Reads the user management configuration data from a file.
 *
 * @author Lars Behnke
 *
 */
public class XmlFileReader {

    private static Log log = LogFactory.getLog(XmlFileReader.class);

    private static final String DEFAULT_HERMESFTP_USERS_FILE = "hermesftp-users.xml";

    private static final String ATTR_PATH = "path";

    private static final String XPATH_PERMISSIONS = "permissions/permission";

    private static final String ATTR_VALUE = "value";

    private static final String ATTR_FLAG = "flag";

    private static final String XPATH_LIMITS = "limits/limit";

    private static final String XPATH_GROUPS = "/user-manager/groups/group";

    private static final String ATTR_NAME = "name";

    private static final String ELEM_GROUP_REF = "group-ref";

    private static final String ATTR_DIR = "dir";

    private static final String ATTR_PASSWORD = "password";

    private static final String ATTR_FULLNAME = "fullname";

    private static final String ATTR_ADMINROLE = "adminrole";

    private static final String ATTR_UID = "uid";

    private static final String ELEM_USER = "user";

    private static final String ATTR_DEFAULT_DIR = "default-dir";

    private static final String XPATH_USERS = "/user-manager/users";

    private String filename;

    /**
     * Getter method for the java bean <code>filename</code>.
     *
     * @return Returns the value of the java bean <code>filename</code>.
     */
    public String getFilename() {
        if (filename == null || filename.length() == 0) {
            String ctxDir = System.getProperty("hermes.ctx.dir");
            File file;
            if (ctxDir != null) {
                file = new File(ctxDir, DEFAULT_HERMESFTP_USERS_FILE);
            } else {
                file = new File(DEFAULT_HERMESFTP_USERS_FILE);
            }
            filename = file.getAbsolutePath();
        }
        log.info("User configuration file: " + filename);
        return filename;
    }

    /**
     * Setter method for the java bean <code>filename</code>.
     *
     * @param filename The value of filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Reads the user management data from a file. If the file was not found the classpath is
     * searched.
     *
     * @return The user management data.
     * @throws FtpConfigException Error on reading or processing a configuration file.
     */
    public UserManagerData read() throws FtpConfigException {
        UserManagerData result;
        File file = null;
        try {
            SAXReader reader = new SAXReader();
            file = new File(getFilename());
            BufferedReader br;
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
            } else {
                InputStream is = getClass().getResourceAsStream("/" + DEFAULT_HERMESFTP_USERS_FILE);
                br = new BufferedReader(new InputStreamReader(is));
            }
            Document doc = reader.read(br);
            result = process(doc);
        } catch (IOException e) {
            throw new FtpConfigException("Reading " + getFilename() + " failed.");
        } catch (DocumentException e) {
            throw new FtpConfigException("Error while processing the configuration file " + file + ".");
        }

        return result;
    }

    private UserManagerData process(Document doc) {
        UserManagerData result = new UserManagerData();
        processUserData(doc, result);
        processGroupData(doc, result);

        return result;
    }

    private void processUserData(Document doc, UserManagerData umd) {
        Element usersElement = (Element) doc.selectSingleNode(XPATH_USERS);
        String defaultDir = usersElement.attributeValue(ATTR_DEFAULT_DIR);

        List userElements = usersElement.selectNodes(ELEM_USER);
        for (Iterator iter = userElements.iterator(); iter.hasNext();) {
            Element userElement = (Element) iter.next();
            String uid = userElement.attributeValue(ATTR_UID);
            String fullName = userElement.attributeValue(ATTR_FULLNAME);
            String password = userElement.attributeValue(ATTR_PASSWORD);
            String adminrole = userElement.attributeValue(ATTR_ADMINROLE);
            String dir = userElement.attributeValue(ATTR_DIR);
            if (dir == null || dir.length() == 0) {
                dir = defaultDir;
            }
            UserData userData = new UserData();
            userData.setFullName(fullName);
            userData.setUid(uid);
            userData.setPassword(password);
            userData.setAdminRole(new Boolean(adminrole).booleanValue());
            userData.setDir(dir);
            List groupRefElements = userElement.selectNodes(ELEM_GROUP_REF);
            for (Iterator iterator = groupRefElements.iterator(); iterator.hasNext();) {
                Element element = (Element) iterator.next();
                String groupRefName = element.attributeValue(ATTR_NAME);
                if (groupRefName != null) {
                    userData.addGroupName(groupRefName.trim());
                }
            }
            umd.getUserData().add(userData);
        }
    }

    private void processGroupData(Document doc, UserManagerData umd) {
        List groupElements = doc.selectNodes(XPATH_GROUPS);
        
        for (Iterator iter = groupElements.iterator(); iter.hasNext();) {
            Element groupElement = (Element) iter.next();
            String name = groupElement.attributeValue(ATTR_NAME);
            GroupData groupData = new GroupData();
            groupData.setName(name);

            List limitElements = groupElement.selectNodes(XPATH_LIMITS);
            for (Iterator iterator = limitElements.iterator(); iterator.hasNext();) {
                Element limitElement = (Element) iterator.next();
                String limitName = limitElement.attributeValue(ATTR_NAME);
                String limitValue = limitElement.attributeValue(ATTR_VALUE);
                if (limitName != null && limitValue != null) {
                    Long limitLong = new Long(limitValue);
                    groupData.getLimits().put(limitName, limitLong);
                }
            }
            List permissionElements = groupElement.selectNodes(XPATH_PERMISSIONS);
            for (Iterator iterator = permissionElements.iterator(); iterator.hasNext();) {
                Element permissionElement = (Element) iterator.next();
                String path = permissionElement.attributeValue(ATTR_PATH);

                /* value attribute is supported for backward compatibility */
                String value = permissionElement.attributeValue(ATTR_VALUE);
                String flag = permissionElement.attributeValue(ATTR_FLAG);
                int permissionValue = getPermissionValue(value, flag);
                PermissionData pd = new PermissionData();
                pd.setPermission(permissionValue);
                pd.setTemplate(path);
                groupData.getPermissions().add(pd);
            }
            umd.getGroupData().addGroup(groupData);
        }
    }

    /**
     * Decides on the permission value based on the passed arguments. Value attribute is supported
     * for backward compatibility
     *
     * @param value The permission value 1=read, 3=read/write.
     * @param flag The permission flag R=read, RW=read/write.
     * @return The permission value.
     */
    private int getPermissionValue(String value, String flag) {
        flag = flag == null ? "" : flag.toUpperCase().trim();
        int permissionValue = 0;
        if (value != null && value.length() > 0) {
            permissionValue = Integer.parseInt(value);
        }
        if (flag.indexOf('R') >= 0) {
            permissionValue |= 1;
        }
        if (flag.indexOf('W') >= 0) {
            permissionValue |= 2;
        }
        return permissionValue;
    }

}
