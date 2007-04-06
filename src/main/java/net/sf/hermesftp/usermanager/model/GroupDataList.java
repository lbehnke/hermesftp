package net.sf.hermesftp.usermanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.hermesftp.common.FtpConstants;
import net.sf.hermesftp.exception.FtpConfigException;

public class GroupDataList {

    private List list = Collections.synchronizedList(new ArrayList());

    public void addGroup(GroupData data) {
        list.add(data);
    }

    public void clear() {
        list.clear();
    }

    public GroupData getGroup(String name) {
        if (name == null) {
            return null;
        }
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            GroupData data = (GroupData) iter.next();
            if (data.getName().equalsIgnoreCase(name)) {
                return data;
            }
        }
        return null;
    }

    public long getUpperLimit(String key) {
        long limit = -1;
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            GroupData data = (GroupData) iter.next();
            long l = data.getLimit(key);
            if (l < 0) {
                return l;
            }
            limit = Math.max(l, limit);

        }
        return limit;
    }
    
    public int getPermission (String path, String username, String ftproot) throws FtpConfigException {
        int result = FtpConstants.PRIV_NONE;
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            GroupData groupData = (GroupData) iter.next();
            int permission = groupData.getPermission(path, ftproot, username);
            result = Math.max(result, permission);
        }
        return result;
 
    }

}
