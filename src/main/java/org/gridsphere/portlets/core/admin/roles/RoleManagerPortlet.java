/*
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.portlets.core.admin.roles;

import org.gridsphere.layout.PortletPageFactory;
import org.gridsphere.provider.event.jsr.ActionFormEvent;
import org.gridsphere.provider.event.jsr.FormEvent;
import org.gridsphere.provider.event.jsr.RenderFormEvent;
import org.gridsphere.provider.portlet.jsr.ActionPortlet;
import org.gridsphere.provider.portletui.beans.*;
import org.gridsphere.services.core.persistence.QueryFilter;
import org.gridsphere.services.core.security.role.PortletRole;
import org.gridsphere.services.core.security.role.RoleManagerService;
import org.gridsphere.services.core.user.User;
import org.gridsphere.services.core.user.UserManagerService;

import javax.portlet.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wehrens
 */
public class RoleManagerPortlet extends ActionPortlet {

    // JSP pages used by this portlet
    /**
     *
     */
    public static final String ROLES_LIST = "admin/roles/doViewRolesList.jsp";
    /**
     *
     */
    public static final String ROLES_EDIT = "admin/roles/doEditRole.jsp";
    /**
     *
     */
    public static final String ROLES_CREATE = "admin/roles/doCreateRole.jsp";

    // Portlet services
    private RoleManagerService roleManagerService = null;
    private UserManagerService userManagerService = null;

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        roleManagerService = (RoleManagerService) createPortletService(RoleManagerService.class);
        userManagerService = (UserManagerService) createPortletService(UserManagerService.class);
        DEFAULT_HELP_PAGE = "admin/roles/help.jsp";
        DEFAULT_VIEW_PAGE = "doListRoles";
    }

    /**
     * @param evt
     * @throws javax.portlet.PortletException
     */
    public void doListRoles(RenderFormEvent evt)
            throws PortletException {
        PortletRequest req = evt.getRenderRequest();
        List roleList = this.roleManagerService.getRoles();
        req.setAttribute("roleList", roleList);
        List<String> coreRolesList = new ArrayList<String>();
        coreRolesList.add(PortletRole.USER.getName());
        coreRolesList.add(PortletRole.ADMIN.getName());
        req.setAttribute("coreRoleList", coreRolesList);
        List<PortletRole> defRoles = roleManagerService.getDefaultRoles();
        req.setAttribute("defRoles", defRoles);
        setNextState(req, ROLES_LIST);
    }

    /**
     * @param event
     */
    public void doReturn(ActionFormEvent event) {
        setNextState(event.getActionRequest(), DEFAULT_VIEW_PAGE);
    }

    /**
     * @param event
     */
    public void doShowRole(RenderFormEvent event) {
        HiddenFieldBean roleHF = event.getHiddenFieldBean("roleHF");
        String roleName = roleHF.getValue();
        doPrepareRole(event, event.getRenderRequest(), event.getRenderResponse(), roleName);
    }

    /**
     * @param event
     */
    public void doEditRole(ActionFormEvent event) {
        String roleName = event.getAction().getParameter("roleName");
        doPrepareRole(event, event.getActionRequest(), event.getActionResponse(), roleName);
    }

    /**
     * @param event
     */
    public void doSaveDefaultRoles(ActionFormEvent event) {
        ActionRequest request = event.getActionRequest();
        String[] rolesCB = request.getParameterValues("rolesDefCB");
        List<PortletRole> allRoles = roleManagerService.getRoles();
        List<User> users = userManagerService.getUsers();
        for (PortletRole role : allRoles) {
            roleManagerService.removeDefaultRole(role);
            for (int i = 0; i < rolesCB.length; i++) {
                if (rolesCB[i].equals(role.getName())) {
                    roleManagerService.addDefaultRole(role);
                    for (User user : users) {
                        roleManagerService.addUserToRole(user, role);
                    }
                    break;
                }
            }
        }
    }

    /**
     * @param event
     * @param req
     * @param res
     * @param roleName
     */
    public void doPrepareRole(FormEvent event, PortletRequest req, PortletResponse res, String roleName) {
        HiddenFieldBean roleHF = event.getHiddenFieldBean("roleHF");
        PortletRole role = null;
        List users = new ArrayList();
        if (roleName != null) {
            role = roleManagerService.getRole(roleName);

            if (role.equals(PortletRole.ADMIN) || role.equals(PortletRole.USER)) req.setAttribute("canEdit", "false");
            roleHF = event.getHiddenFieldBean("roleHF");
            roleHF.setValue(roleName);
            TextFieldBean roleNameTF = event.getTextFieldBean("roleNameTF");
            roleNameTF.setValue(role.getName());
            TextFieldBean roleDescTF = event.getTextFieldBean("roleDescTF");
            roleDescTF.setValue(role.getDescription());

            int numUsers = roleManagerService.getNumUsersInRole(role);
            QueryFilter filter = event.getQueryFilter(20, numUsers);

            users = roleManagerService.getUsersInRole(role, filter);

            List notusers = userManagerService.getUsers();
            for (int i = 0; i < users.size(); i++) {
                User u = (User) users.get(i);
                if (notusers.contains(u)) notusers.remove(u);
            }

            TableBean userTable = event.getTableBean("userTable");
            userTable.setQueryFilter(filter);

            ListBoxBean addUsersLB = event.getListBoxBean("addusersLB");
            addUsersLB.clear();
            if (notusers.isEmpty()) {
                req.setAttribute("nousers", "true");
            }
            for (int i = 0; i < notusers.size(); i++) {
                User user = (User) notusers.get(i);
                ListBoxItemBean item = new ListBoxItemBean();
                item.setName(user.getID());
                item.setValue(user.getFullName());
                addUsersLB.addBean(item);
            }
        } else {
            HiddenFieldBean isNewRoleHF = event.getHiddenFieldBean("isNewRoleHF");
            isNewRoleHF.setValue("true");
            users = userManagerService.getUsers();
            req.setAttribute("newrole", "true");
        }

        req.setAttribute("userList", users);

        setNextState(req, ROLES_EDIT);
    }

    /**
     * @param evt
     */
    public void doDeleteRole(ActionFormEvent evt) {
        PortletRequest req = evt.getActionRequest();
        String roleStr = "";
        String[] roleNames = req.getParameterValues("rolesCB");
        if (roleNames != null) {
            for (int i = 0; i < roleNames.length; i++) {
                PortletRole role = roleManagerService.getRole(roleNames[i]);
                if (role != null) {
                    // remove users in role first
                    List<User> users = roleManagerService.getUsersInRole(role);
                    if (!users.isEmpty()) {
                        for (User user : users) {
                            roleManagerService.deleteUserInRole(user, role);
                        }
                    }
                    roleManagerService.deleteRole(role);
                    // if role has been used in layouts, rename it to empty role
                    PortletPageFactory pageFactory = PortletPageFactory.getInstance();
                    pageFactory.renameRole(req, role.getName(), "");
                    roleStr += " " + role.getName();
                }
            }
            createSuccessMessage(evt, this.getLocalizedText(req, "ROLE_DELETE_MSG") + ": " + roleStr);
        }
        setNextState(req, DEFAULT_VIEW_PAGE);
    }

    /**
     * @param evt
     */
    public void doSaveRole(ActionFormEvent evt) {
        PortletRequest req = evt.getActionRequest();
        boolean isNewRole = false;
        HiddenFieldBean isNewRoleHF = evt.getHiddenFieldBean("isNewRoleHF");
        if (isNewRoleHF.getValue().equals("true")) isNewRole = true;

        TextFieldBean roleNameTF = evt.getTextFieldBean("roleNameTF");
        // check if role name is already taken
        if ((roleManagerService.getRole(roleNameTF.getValue()) != null) && (isNewRole)) {
            createErrorMessage(evt, this.getLocalizedText(req, "ROLE_EXISTS_MSG"));
            setNextState(req, ROLES_EDIT);
            return;
        }
        TextFieldBean roleDescTF = evt.getTextFieldBean("roleDescTF");

        HiddenFieldBean roleHF = evt.getHiddenFieldBean("roleHF");
        PortletRole role = roleManagerService.getRole(roleHF.getValue());
        if (role != null) {
            role.setName(roleNameTF.getValue());
            role.setDescription(roleDescTF.getValue());
        } else {
            role = new PortletRole(roleNameTF.getValue());
            role.setDescription(roleDescTF.getValue());
        }
        roleManagerService.saveRole(role);
        if (isNewRole) {
            createSuccessMessage(evt, this.getLocalizedText(req, "ROLE_CREATE_MSG") + ": " + role.getName());
        } else {
            createSuccessMessage(evt, this.getLocalizedText(req, "ROLE_UPDATE_MSG") + ": " + role.getName());
            PortletPageFactory pageFactory = PortletPageFactory.getInstance();
            String oldRole = roleHF.getValue();
            String newRole = roleNameTF.getValue();
            //might need to rename role in page layouts
            if (oldRole.equalsIgnoreCase(newRole)) {
                pageFactory.renameRole(req, oldRole, newRole);
            }
        }

    }

    /**
     * @param event
     */
    public void doAddUser(ActionFormEvent event) {
        PortletRequest req = event.getActionRequest();
        ListBoxBean addusersLB = event.getListBoxBean("addusersLB");
        String userid = addusersLB.getSelectedName();
        HiddenFieldBean roleHF = event.getHiddenFieldBean("roleHF");
        String roleName = roleHF.getValue();
        PortletRole role = roleManagerService.getRole(roleName);
        User user = userManagerService.getUser(userid);
        if ((user != null) && (role != null)) {
            roleManagerService.addUserToRole(user, role);
        }
        setNextState(req, "doShowRole");
    }

    /**
     * @param event
     */
    public void doRemoveUser(ActionFormEvent event) {
        PortletRequest req = event.getActionRequest();
        HiddenFieldBean roleHF = event.getHiddenFieldBean("roleHF");
        PortletRole role = roleManagerService.getRole(roleHF.getValue());
        String[] users = req.getParameterValues("userCB");
        if ((users != null) && (role != null)) {
            for (int i = 0; i < users.length; i++) {
                User user = userManagerService.getUser(users[i]);
                roleManagerService.deleteUserInRole(user, role);
            }

        }
        setNextState(req, "doShowRole");
    }

}
