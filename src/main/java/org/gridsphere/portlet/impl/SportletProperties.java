/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.portlet.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <code>SportletProperties</code> conatins all the "hidden" variable names
 * that get transmitted between the portlet container and the portlets to
 * request a particular portlet lifecycle action.
 * <p/>
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
public class SportletProperties {

    protected static Properties props = null;

    private static SportletProperties instance = new SportletProperties();

    /**
     * Determines which lifecycle command to invoke
     */
    public static final String PORTLET_LIFECYCLE_METHOD = "org.gridsphere.portlet.portletLifecycleMethod";

    // Portlet Lifecyle methods
    /**
     * Command to load a portlet
     */
    public static final String LOAD = "org.gridsphere.portlet.lifecycle.load";

    /**
     * Command to perform the init method on a portlet
     */
    public static final String INIT = "org.gridsphere.portlet.lifecycle.init";

    /**
     * Command to perform the destroy method on a portlet
     */
    public static final String DESTROY = "org.gridsphere.portlet.lifecycle.destroy";

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
    public static final String SERVICE = "org.gridsphere.portlet.lifecycle.service";


    // Portlet events
    /**
     * The variable name of the ActionEvent object
     */
    public static final String ACTION_EVENT = "org.gridsphere.event.ActionEvent";

    /**
     * The variable name of the RenderEvent object
     */
    public static final String RENDER_EVENT = "org.gridsphere.event.RenderEvent";


    /**
     * The variable name of the WindowEvent object
     */
    public static final String WINDOW_EVENT = "org.gridsphere.event.WindowEvent";

    /**
     * Determines which event listener to notify
     */
    public static final String PORTLET_ACTION_METHOD = "org.gridsphere.portlet.lifecycle.portletActionMethod";

    /**
     * Command to perform the actionPerformed method on a portlet
     */
    public static final String ACTION_PERFORMED = "org.gridsphere.portlet.lifecycle.actionPerformed";

    public static final String INIT_PAGE = "org.gridsphere.layout.INIT_PAGE";
    /**
     * Command to perform the doTitle method on a portlet
     */
    public static final String DO_TITLE = "org.gridsphere.portlet.lifecycle.doTitle";

    public static final String COMPONENT_ID = "cid";

    public static final String COMPONENT_ID_2 = "cid2";

    public static final String COMPONENT_ID_VAR = "org.gridsphere.layout.COMPONENT_ID_VAR";

    public static final String COMPONENT_ID_NUM = "org.gridsphere.layout.COMPONENT_ID_NUM";

    public static final String COMPONENT_LABEL = "org.gridsphere.layout.COMPONENT_LABEL";

    public static final String LAYOUT_EDIT_MODE = "org.gridsphere.layout.LAYOUT_EDIT_MODE";


    public static final String LAYOUT_THEME = "org.gridsphere.layout.THEME";

    public static final String LAYOUT_RENDERKIT = "org.gridsphere.layout.RENDERKIT";


    public static final String LAYOUT_PAGE_PARAM = "gs_PageLayout";

    public static final String LAYOUT_PAGE = "org.gridsphere.layout.PAGE";

    public static final String PORTLET_SETUP_PAGE_INCLUDE = "org.gridsphere.services.core.setup.PAGE_INCLUDE";

    public static final String PORTLET_SETUP_PAGE_CONTEXT = "org.gridsphere.services.core.setup.PAGE_CONTEXT";

    public static final String PORTLET_SETUP_TYPE = "org.gridsphere.services.core.setup.TYPE";

    public static final String PORTLET_SETUP_TYPE_PRE = "org.gridsphere.services.core.setup.TYPE_PRE";

    public static final String PORTLET_SETUP_TYPE_POST = "org.gridsphere.services.core.setup.TYPE_POST";

    public static final String PORTLET_SETUP_OPERATION = "org.gridsphere.services.core.setup.OPERATION";

    public static final String PORTLET_SETUP_TITLE = "org.gridsphere.services.core.setup.TITLE";

    public static final String PORTLET_SETUP_DESCRIPTION = "org.gridsphere.services.core.setup.DESCRIPTION";    

    public static final String PORTLET_SETUP_MODULE_NUMBER = "org.gridsphere.services.core.setup.MODULE_NUM";    

    public static final String PORTLET_SETUP_NUMBER_OF_MODULES = "org.gridsphere.services.core.setup.NUM_OF_MODULES";

    public static final String IGNORE_PARSING = "org.gridsphere.portlet.impl.IGNORE_PARSING";

    // Used for "action component model" in grid portlets currently
    public static final String GP_COMPONENT_ID = "gpcompid";

    public static final String DEFAULT_PORTLET_ACTION_VAR = "org.gridsphere.DEFAULT_PORTLET_ACTION_VAR";

    public static final String DEFAULT_PORTLET_ACTION = "gs_action";

    public static final String DEFAULT_PORTLET_ACTION_2 = "gs_action2";

    public static final String IS_ACTION = "gs_isaction";

    public static final String DEFAULT_PORTLET_RENDER = "gs_render";

    public static final String DEFAULT_PORTLET_MESSAGE = "message";

    public static final String PORTLETID = "pid";

    public static final String ERROR = "org.gridsphere.portlet.error";
    // Portlet API objects

    public static final String CLIENT = "org.gridsphere.portletcontainer.Client";

    public static final String PORTLET_MODE = "gs_mode";

    public static final String PORTLET_TITLE = "title";

    public static final String PREVIOUS_MODE = "org.gridsphere.portlet.PreviousMode";

    public static final String MODEMODIFIER = "org.gridsphere.portlet.ModeModifier";

    public static final String PORTLET_WINDOW = "gs_state";

    public static final String PORTLET_WINDOW_ID = "org.gridsphere.layout.WINDOW_ID";

    public static final String PORTLET_DATA_MANAGER = "org.gridsphere.portletcontainer.PortletDataManager";

    public static final String PORTLETERROR = "org.gridsphere.portlet.PortletError";

    public static final String PREFIX = "up";

    public static final String PORTLET_USER = "org.gridsphere.services.core.user.User";

    /**
     * The variable name of the PortletConfig object
     */
    public static final String PORTLET_CONFIG = "javax.portlet.config";

    public static final String PORTAL_CONTEXT = "javax.portlet.context";

    public static final String PORTLET_MIMETYPES = "javax.portlet.mimetypes";

    public static final String PORTLET_PREFERENCES = "javax.portlet.preferences";

    public static final String PORTLET_PREFERENCES_MANAGER = "org.gridsphere.portletcontainer.impl.PortletPreferencesManager";

    public static final String PORTLET_GROUP = "org.gridsphere.portlet.PortletGroup";

    public static final String PORTLET_ROLE = "org.gridsphere.portlet.PortletRole";

    public static final String RENDER_REQUEST = "javax.portlet.request";

    public static final String RENDER_RESPONSE = "javax.portlet.response";

    public static final String PORTLETGROUPS = "org.gridsphere.portlet.groups";

    public static final String LOCALE = "org.gridsphere.portlet.Locale";

    public static final String PORTLET_SERVLET = "org.gridsphere.portlets.PortletServlet";

    public static final String FILE_DOWNLOAD_NAME = "org.gridsphere.portletcontainer.FILE_DOWNLOAD_NAME";

    public static final String FILE_DOWNLOAD_PATH = "org.gridsphere.portletcontainer.FILE_DOWNLOAD_PATH";

    public static final String FILE_DOWNLOAD_BINARY = "org.gridsphere.portletcontainer.FILE_DOWNLOAD_BINARY";

    public static final String FILE_DELETE = "org.gridsphere.portletcontainer.FILE_DELETE";

    public static final String FILE_DOWNLOAD_ERROR = "org.gridsphere.portletcontainer.FILE_DOWNLOAD_ERROR";

    public static final String PORTAL_PROPERTIES = "org.gridsphere.PORTAL_PROPERTIES";

    public static final String ALLOWED_MODES = "org.gridsphere.ALLOWED_MODES";

    public static final String MIME_TYPES = "org.gridsphere.MIME_TYPES";

    public static final String RESPONSE_COMMITTED = "org.gridsphere.RESPONSE_COMMITTED";

    public static final String RENDER_PARAM_PREFIX = "rp_";

    public static final String RENDER_OUTPUT = "org.gridsphere.layout.RENDER_OUTPUT.";

    public static final String FLOAT_STATE = "org.gridsphere.portlet.FLOAT_STATE";

    public static final String DISPLAY_MODES = "org.gridsphere.layout.DISPLAY_MODES";

    public static final String DISPLAY_STATES = "org.gridsphere.layout.DISPLAY_STATES";

    public static final String SSL_REQUIRED = "javax.portlet.SSL_REQUIRED";

    public static final String PORTLET_USER_PRINCIPAL = "org.gridsphere.services.core.user.UserPrincipal";

    public static final String EXTRA_QUERY_INFO = "org.gridsphere.layout.EXTRA_QUERY_INFO";

    public static final String CONTEXT_PATH = "org.gridsphere.CONTEXT_PATH";

    public static final String SERVLET_PATH = "org.gridsphere.SERVLET_PATH";

    public static final String PAGE_BUFFER = "org.gridsphere.PAGE_BUFFER";

    public static final String USE_AJAX = "org.gridsphere.USE_AJAX";

    public static final String COMPONENT_NAME = "org.gridsphere.layout.COMP_NAME";

    public static final String PORTLET_NAME = "org.gridsphere.PORTLET_NAME";

    public static final String PORTAL_REDIRECT_PATH = "org.gridsphere.PORTAL_REDIRECT_PATH";

    // Portal Filter events
    /**
     * The variable name of the method to be performed on PortalFilters
     */
    public static final String PORTAL_FILTER_EVENT = "org.gridsphere.services.core.filter.EVENT";

    /**
     * Command to perform the doAfterLogin method on a PortalFilters
     */
    public static final String PORTAL_FILTER_EVENT_AFTER_LOGIN = "org.gridsphere.services.core.filter.EVENT_AFTER_LOGIN";

    
    private SportletProperties() {
        if (props == null) {
            InputStream propsStream = getClass().getResourceAsStream("/config/portlet.properties");
            props = new Properties();
            try {
                props.load(propsStream);
            } catch (IOException e) {
                System.err.println("Unable to load portlet.properties");
                e.printStackTrace();
            }finally {
            	try {
					propsStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }

    public static SportletProperties getInstance() {
        return instance;
    }

    public String getProperty(String key) {
        if (key == null) throw new IllegalArgumentException("property key cannot be null!");
        return props.getProperty(key);
    }

}

