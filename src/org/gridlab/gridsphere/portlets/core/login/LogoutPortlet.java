/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.portlets.core.login;

import org.gridlab.gridsphere.portlet.AbstractPortlet;
import org.gridlab.gridsphere.portlet.PortletException;
import org.gridlab.gridsphere.portlet.PortletRequest;
import org.gridlab.gridsphere.portlet.PortletResponse;
import org.gridlab.gridsphere.portlet.impl.SportletProperties;

import java.io.IOException;

public class LogoutPortlet extends AbstractPortlet {

    public void doView(PortletRequest request, PortletResponse response) throws PortletException, IOException {
        String title = getPortletSettings().getTitle(request.getLocale(), null);
        request.setAttribute("GRIDSPHERE_LOGOUT_LABEL", title);
        getPortletConfig().getContext().include("/jsp/login/logout.jsp", request, response);
    }

}
