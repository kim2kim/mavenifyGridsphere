/**
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.portlet.impl;

/**
 * <code>SportletProperties</code> conatins all the "hidden" variable names
 * that get transmitted between the portlet container and the portlets to
 * request a particular portlet lifecycle action.
 * <p>
 * SportletProperties comtains three kinds of attributes
 * <ul>
 * <li>Lifecycle atttributes specify the lifecycle method to invoke on a
 * portlet</li>
 * <li>Portlet object attributes contain the actual portlet object e.g.
 * PortletConfig, PortletSettings which must be transferred in the
 * servlet request.</li>
 * <li>Portlet event objects</li>
 * </ul>
 */
public interface SportletProperties {

    /**
     *  Determines which lifecycle command to invoke
     */
    public static final String PORTLET_LIFECYCLE_METHOD = "org.gridlab.gridsphere.portlet.portletLifecycleMethod";

    // Portlet Lifecyle methods
    /**
     * Command to perform the init method on a portlet
     */
    public static final String INIT = "org.gridlab.gridsphere.portlet.lifecycle.init";

    /**
     * Command to perform the destroy method on a portlet
     */
    public static final String DESTROY = "org.gridlab.gridsphere.portlet.lifecycle.destroy";

    /**
     * Command to perform the init concrete method on a portlet
     */
    public static final String INIT_CONCRETE = "org.gridlab.gridsphere.portlet.lifecycle.initConcrete";

    /**
     * Command to perform the destroy concrete method on a portlet
     */
    public static final String DESTROY_CONCRETE = "org.gridlab.gridsphere.portlet.lifecycle.destroyConcrete";

    /**
     * Command to perform the login method on a portlet
     */
    public static final String LOGIN = "gs_login";

    /**
     * Command to perform the logout method on a portlet
     */
    public static final String LOGOUT = "gs_logout";

    /**
     * Command to perform the service method on a portlet
     */
    public static final String SERVICE = "org.gridlab.gridsphere.portlet.lifecycle.service";


    // Portlet obects
    /**
     * The variable name of the PortletConfig object
     */
    public static final String PORTLET_CONFIG = "org.gridlab.gridsphere.portlet.PortletConfig";

    /**
     * The variable name of the PortletApplication object
     */
    public static final String PORTLET_APPLICATION = "org.gridlab.gridsphere.portletcontainer.PortletApplication";

    /**
     *  The variable name of the PortletSettings object
     */
    public static final String PORTLET_SETTINGS = "org.gridlab.gridsphere.portlet.PortletSettings";

    // Portlet events
    /**
     *  The variable name of the ActionEvent object
     */
    public static final String ACTION_EVENT = "org.gridlab.gridsphere.event.ActionEvent";

    /**
     *  The variable name of the MessageEvent object
     */
    public static final String MESSAGE_EVENT = "org.gridlab.gridsphere.event.MessageEvent";

    /**
     *  The variable name of the WindowEvent object
     */
    public static final String WINDOW_EVENT = "org.gridlab.gridsphere.event.WindowEvent";

    /**
     *  Determines which event listener to notify
     */
    public static final String PORTLET_ACTION_METHOD = "org.gridlab.gridsphere.portlet.lifecycle.portletActionMethod";

    /**
     * Command to perform the actionPerformed method on a portlet
     */
    public static final String ACTION_PERFORMED = "org.gridlab.gridsphere.portlet.lifecycle.actionPerformed";

    /**
     * Command to perform the messageReceived method on a portlet
     */
    public static final String MESSAGE_RECEIVED = "org.gridlab.gridsphere.portlet.lifecycle.messageReceived";

    /**
     * Command to perform the windowDetached method on a portlet
     */
    public static final String WINDOW_DETACHED = "org.gridlab.gridsphere.portlet.lifecycle.windowDetached";

    /**
     * Command to perform the windowMinimized method on a portlet
     */
    public static final String WINDOW_MINIMIZED = "org.gridlab.gridsphere.portlet.lifecycle.windowMinimized";

    /**
     * Command to perform the windowMaximized method on a portlet
     */
    public static final String WINDOW_MAXIMIZED = "org.gridlab.gridsphere.portlet.lifecycle.windowMaximized";

    /**
     * Command to perform the windowClosing method on a portlet
     *
     * *NOT IMPLEMENTED*
     */
    public static final String WINDOW_CLOSING = "org.gridlab.gridsphere.portlet.lifecycle.windowClosing";

    /**
     * Command to perform the windowRestored method on a portlet
     */
    public static final String WINDOW_RESTORED = "org.gridlab.gridsphere.portlet.lifecycle.windowRestored";

    /**
     * Command to perform the doTitle method on a portlet
     */
    public static final String DO_TITLE = "org.gridlab.gridsphere.portlet.lifecycle.doTitle";

    public static final String COMPONENT_ID = "cid";

    public static final String DEFAULT_PORTLET_ACTION = "action";

    public static final String DEFAULT_PORTLET_MESSAGE = "message";

    public static final String PORTLETID = "pid";

    public static final String ERROR = "org.gridlab.gridsphere.portlet.error";
    // Portlet API objects

    public static final String CLIENT = "org.gridlab.gridsphere.portlet.Client";

    public static final String PORTLET_MODE = "gs_mode";

    public static final String PREVIOUS_MODE = "org.gridlab.gridsphere.portlet.PreviousMode";

    public static final String MODEMODIFIER = "org.gridlab.gridsphere.portlet.ModeModifier";

    public static final String PORTLET_WINDOW = "gs_state";

    public static final String PORTLET_DATA = "org.gridlab.gridsphere.portlet.PortletData";

    public static final String PORTLETERROR = "org.gridlab.gridsphere.portlet.PortletError";

    public static final String PREFIX = "up";

    public static final String PORTLET_USER = "org.gridlab.gridsphere.portlet.User";

    public static final String PORTLET_GROUP = "org.gridlab.gridsphere.portlet.PortletGroup";

    public static final String PORTLET_ROLE = "org.gridlab.gridsphere.portlet.PortletRole";

    public static final String PORTLETGROUPS = "org.gridlab.gridsphere.portlet.groups";

}
