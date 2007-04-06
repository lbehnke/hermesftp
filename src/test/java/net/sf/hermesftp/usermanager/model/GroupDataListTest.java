package net.sf.hermesftp.usermanager.model;

import net.sf.hermesftp.SpringUtil;
import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.exception.FtpConfigException;
import net.sf.hermesftp.usermanager.impl.XmlFileUserManager;
import junit.framework.TestCase;

public class GroupDataListTest extends TestCase 
implements FtpConstants {

    private XmlFileUserManager userManager;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        userManager = (XmlFileUserManager) SpringUtil.getBean("userManager");
        userManager.load();
    }

    /**
     * Checks permissions of the default users.
     */
    public void testPermissions() {
        // CHECKSTYLE:OFF
        String root = "c:/test";
        GroupDataList groupList;
        try {
            String user = "user";
            groupList = userManager.getGroupDataList(user);
            assertEquals(PRIV_READ, groupList.getPermission("c:\\test", user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission("c:\\test\\user", user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission("c:\\test\\user\\dir1\\dir2",
                                                                    user,
                                                                    root));
            assertEquals(PRIV_NONE, groupList.getPermission("c:\\test\\admin", user, root));

            user = "admin";
            groupList = userManager.getGroupDataList(user);
            assertEquals(PRIV_READ_WRITE, groupList.getPermission("c:\\test", user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission("c:\\test\\user", user, root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission("c:\\test\\user\\dir1\\dir2",
                                                                    user,
                                                                    root));
            assertEquals(PRIV_READ_WRITE, groupList.getPermission("c:\\test\\admin", user, root));

        } catch (FtpConfigException e) {
            fail(e.toString());
        }
        // CHECKSTYLE:ON
    }


}
