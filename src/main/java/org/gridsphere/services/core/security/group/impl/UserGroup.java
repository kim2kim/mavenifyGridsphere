/*
 * @version: $Id$
 */
package org.gridsphere.services.core.security.group.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridsphere.services.core.user.User;
import org.gridsphere.services.core.user.impl.UserImpl;
import org.gridsphere.services.core.security.group.PortletGroup;
import org.gridsphere.services.core.security.role.PortletRole;

public class UserGroup {

    protected transient static Log log = LogFactory.getLog(UserGroup.class);

    private String oid = null;
    private UserImpl user = null;
    private PortletGroup sgroup = null;
    // deprecated
    private String role = "";
    private PortletRole portletRole;

    public UserGroup() {
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getID() {
        return getOid();
    }

    public PortletGroup getGroup() {
        return this.sgroup;
    }

    public void setGroup(PortletGroup group) {
        this.sgroup = (PortletGroup) group;
    }

    /**
     * @deprecated
     * @return the portlet role
     */
    public PortletRole getRole() {
        return portletRole;
    }

    /**
     * @deprecated
     * @param role the portlet role
     */
    public void setRole(PortletRole role) {
        this.portletRole = role;
    }

    /**
     * @deprecated
     * @return the role name
     */
    public String getRoleName() {
        return this.role;
    }

    /**
     * @deprecated
     * @param roleName the role name
     */
    public void setRoleName(String roleName) {
        this.role = roleName;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = (UserImpl) user;
    }

}
