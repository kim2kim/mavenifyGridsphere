/*
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.layout.event;

import org.gridsphere.layout.PortletComponent;

import javax.portlet.PortletRequest;

/**
 * A <code>PortletComponentEvent</code> is a general portlet render event
 */
public interface PortletComponentEvent {

    /**
     * Returns the portlet title bar event action
     *
     * @return the portlet title bar event action
     */
    public ComponentAction getAction();

    /**
     * Returns true if this event actually triggered an action
     *
     * @return true if this event actually triggered an action
     */
    public boolean hasAction();

    /**
     * Returns the PortletComponent that was selected
     *
     * @return the PortletComponent that was selcted
     */
    public PortletComponent getPortletComponent();

    /**
     * Returns the component id of the portlet component
     *
     * @return the component id of the portlet component
     */
    public int getID();

}
