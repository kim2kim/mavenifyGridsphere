/**
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.event.impl;

import org.gridlab.gridsphere.event.ActionEvent;
import org.gridlab.gridsphere.portlet.PortletAction;
import org.gridlab.gridsphere.portlet.PortletRequest;
import org.gridlab.gridsphere.portlet.PortletResponse;
import org.gridlab.gridsphere.portlet.DefaultPortletAction;

import java.util.EventObject;

/**
 * An ActionEvent is sent by the portlet container when an HTTP request is received that is associated with an action.
 */
public class ActionEventImpl extends EventObject implements ActionEvent {

    private DefaultPortletAction action;
    private PortletRequest request;
    private PortletResponse response;

    /**
     * Create an ActionEventImpl
     *
     * @param action a DefaultPortletAction
     * @param request the PortletRequest
     * @param response the PortletResponse
     */
    public ActionEventImpl(DefaultPortletAction action, PortletRequest request, PortletResponse response) {
        super(action);
        this.action = action;
        this.request = request;
        this.response = response;
    }

    /**
     * Returns the action that this action event carries.
     *
     * @return the portlet action
     */
    public DefaultPortletAction getAction() {
        return action;
    }

    /**
     * Return the portlet request associated with this action event
     *
     * @return portletRequest the PortletRequest
     */
    public PortletRequest getPortletRequest() {
        return request;
    }

    /**
     * Return the portlet response associated with this action event
     *
     * @return portletResponse the PortletResponse
     */
    public PortletResponse getPortletResponse() {
        return response;
    }

}
