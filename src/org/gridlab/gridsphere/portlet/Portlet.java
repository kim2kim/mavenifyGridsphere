/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.portlet;

import org.gridlab.gridsphere.portlet.impl.*;
import org.gridlab.gridsphere.portletcontainer.descriptor.PortletApp;
import org.gridlab.gridsphere.event.ActionEvent;
import org.gridlab.gridsphere.event.impl.ActionEventImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * The abstract PortletInfo is used by the portlet container to invoke the portlet.
 * Every portlet has to implement this abstract class, either by deriving directly from it,
 * or by using one of the abstract portlet implementations.
 *
 * A portlet is a small Java program that runs within a portlet container.
 * Portlets receive and respond to requests from the portlet container.
 * There is ever only one portlet object instance per portlet configuration in the web deployment descriptor.
 * There may be many PortletSettings objects parameterisng the same portlet object according to the
 * Flyweight pattern, provided on a per-request basis. A concrete parameterization of a portlet object
 * is referred to as a concrete portlet. The settings of concrete portlets may change at any time caused
 * by administrators modifying portlet settings, e.g. using the config mode of a portlet.
 *
 * Additionally, user can have personal views of concrete portlets. Therefore, the transient portlet session
 * and persistent concrete portlet data carries vital information for the portlet to create a personalized
 * user experience. A concrete portlet in conjunction with portlet data creates a concrete portlet instance.
 * This is similar to why a servlet may not store things depending on requests or sessions in instance variables.
 * As a consequence, the portlet should not attempt to store any data that depends on portlet settings,
 * portlet data or the portlet session or any other user-related information as instance or class variables.
 * The general programming rules for servlets also apply to portlets - instance variables should only used
 * when the intent is to share them between all the parallel threads that concurrently execute a portlet, and
 * portlet code should avoid synchronization unless absolutely required.
 *
 * As part of running within the portlet container each portlet has a life-cycle.
 * The corresponding methods are called in the following sequence:
 *
 * 1. The portlet is constructed, then initialized with the init() method.
 * 2. A concrete portlet s initialized with the initConcrete() method for each PortletSettings.
 * 3. Any calls from the portlet container to the service() method are handled.
 * 4. The concrete portlet is taken out of service with the destroyConcrete() method.
 * 5. The portlet is taken out of service, then destroyed with the destroy() method,
 * then garbage collected and finalized.
 *
 * The concrete portlet instance is created and destroyed with the login() and logout() methods, respectively.
 * If a portlet provides personalized views these methods should be implemented.
 *
 * The portlet container loads and instantiates the portlet class.
 * This can happen during startup of the portal server or later,
 * but no later then when the first request to the portlet has to be serviced.
 * Also, if a portlet is taken out of service temporarily, for example while administrating it,
 * the portlet container may finish the life-cycle before taking the portlet out of service.
 * When the administration is done, the portlet will be newly initialized.
 */
public abstract class Portlet extends HttpServlet
        implements Servlet, ServletConfig, java.io.Serializable, PortletSessionListener {

    protected transient static PortletLog log = SportletLog.getInstance(Portlet.class);

    protected PortletConfig portletConfig = null;
    protected PortletSettings portletSettings = null;

    public static class Mode implements Serializable {

        protected static final int VIEW_MODE = 1;
        protected static final int EDIT_MODE = 2;
        protected static final int HELP_MODE = 3;
        protected static final int CONFIGURE_MODE = 4;

        public static final Mode EDIT = new Mode(EDIT_MODE);
        public static final Mode VIEW = new Mode(VIEW_MODE);
        public static final Mode HELP = new Mode(HELP_MODE);
        public static final Mode CONFIGURE = new Mode(CONFIGURE_MODE);

        private int mode = 0;

        private Mode(int mode) {
            this.mode = mode;
        }

        public static Portlet.Mode getInstance(String mode) {
            if (mode == null) return null;
            if (mode.equalsIgnoreCase(EDIT.toString())) {
                return EDIT;
            } else if (mode.equalsIgnoreCase(VIEW.toString())) {
                return VIEW;
            } else if (mode.equalsIgnoreCase(HELP.toString())) {
                return HELP;
            } else if (mode.equalsIgnoreCase(CONFIGURE.toString())) {
                return CONFIGURE;
            }
            return null;
        }

        public int getMode() {
            return mode;
        }

        public Object readResolve() {
            // XXX: FILL ME IN
            return null;
        }

        public String toString() {
            String tagstring = null;
            if (mode == EDIT_MODE) {
                tagstring = "EDIT";
            } else if (mode == HELP_MODE) {
                tagstring = "HELP";
            } else if (mode == CONFIGURE_MODE) {
                tagstring = "CONFIGURE";
            } else if (mode == VIEW_MODE) {
                tagstring = "VIEW";
            }
            return tagstring;
        }

    }

    public static class ModeModifier implements Serializable {

        public static final int CURRENT_MODE = 0;
        public static final int PREVIOUS_MODE = 1;
        public static final int REQUESTED_MODE = 2;

        public static final ModeModifier CURRENT = new ModeModifier(CURRENT_MODE);
        public static final ModeModifier PREVIOUS = new ModeModifier(PREVIOUS_MODE);
        public static final ModeModifier REQUESTED = new ModeModifier(REQUESTED_MODE);

        private int modifier = CURRENT_MODE;

        private ModeModifier(int modifier) {
            this.modifier = modifier;
        }

        public int getId() {
            return modifier;
        }

        public Object readResolve() {
            // XXX: FILL ME IN
            return null;
        }

        public String toString() {
            String tagstring = null;
            if (modifier == PREVIOUS_MODE) {
                tagstring = "PREVIOUS";
            } else if (modifier == REQUESTED_MODE) {
                tagstring = "REQUESTED";
            } else if (modifier == CURRENT_MODE) {
                tagstring = "CURRENT";
            }
            return tagstring;
        }

    }

    public Portlet() {
    }

    /**
     * Called by the portlet container to indicate to this portlet that it is put into service.
     *
     * The portlet container calls the init() method for the whole life-cycle of the portlet.
     * The init() method must complete successfully before concrete portlets are created through
     * the initConcrete() method.
     *
     * The portlet container cannot place the portlet into service if the init() method
     *
     * 1. throws UnavailableException
     * 2. does not return within a time period defined by the portlet container.
     *
     * @param config the portlet configuration
     * @throws UnavailableException if an exception has occurrred that interferes with the portlet's
     * normal initialization
     */
    public abstract void init(PortletConfig config) throws UnavailableException;

    /**
     * Called by the portlet container to indicate to this portlet that it is taken out of service.
     * This method is only called once all threads within the portlet's service() method have exited
     * or after a timeout period has passed. After the portlet container calls this method,
     * it will not call the service() method again on this portlet.
     *
     * This method gives the portlet an opportunity to clean up any resources that are
     * being held (for example, memory, file handles, threads).
     *
     * @param config the portlet configuration
     */
    public abstract void destroy(PortletConfig config);

    /**
     * Called by the portlet container to indicate that the concrete portlet is put into service.
     * The portlet container calls the initConcrete() method for the whole life-cycle of the portlet.
     * The initConcrete() method must complete successfully before concrete portlet instances can be
     * created through the login() method.
     *
     * The portlet container cannot place the portlet into service if the initConcrete() method
     *
     * 1. throws UnavailableException
     * 2. does not return within a time period defined by the portlet container.
     *
     * @param settings the portlet settings
     */
    public abstract void initConcrete(PortletSettings settings) throws UnavailableException;

    /**
     * Called by the portlet container to indicate that the concrete portlet is taken out of service.
     * This method is only called once all threads within the portlet's service() method have exited
     * or after a timeout period has passed. After the portlet container calls this method,
     * it will not call the service() method again on this portlet.
     *
     * This method gives the portlet an opportunity to clean up any resources that are being
     * held (for example, memory, file handles, threads).
     *
     * @param settings the portlet settings
     */
    public abstract void destroyConcrete(PortletSettings settings);

    /**
     * Called by the portlet container to ask this portlet to generate its markup using the given
     * request/response pair. Depending on the mode of the portlet and the requesting client device,
     * the markup will be different. Also, the portlet can take language preferences and/or
     * personalized settings into account.
     *
     * @param request the portlet request
     * @param response the portlet response
     *
     * @throws PortletException if the portlet has trouble fulfilling the rendering request
     * @throws IOException if the streaming causes an I/O problem
     */
    public abstract void service(PortletRequest request, PortletResponse response)
            throws PortletException, IOException;

    /**
     * Description copied from interface: PortletSessionListener
     * Called by the portlet container to ask the portlet to initialize a personalized user experience.
     * In addition to initializing the session this method allows the portlet to initialize the
     * concrete portlet instance, for example, to store attributes in the session.
     *
     * @param request the portlet request
     */
    public abstract void login(PortletRequest request);

    /**
     * Description copied from interface: PortletSessionListener
     * Called by the portlet container to indicate that a concrete portlet instance is being removed.
     * This method gives the concrete portlet instance an opportunity to clean up any resources
     * (for example, memory, file handles, threads), before it is removed.
     * This happens if the user logs out, or decides to remove this portlet from a page.
     *
     * @param session the portlet session
     */
    public abstract void logout(PortletSession session);

    /**
     * Returns the time the response of the PortletInfo  object was last modified, in milliseconds since midnight
     * January 1, 1970 GMT. If the time is unknown, this method returns a negative number (the default).
     *
     * Portlets that can quickly determine their last modification time should override this method.
     * This makes browser and proxy caches work more effectively, reducing the load on server and network resources.
     *
     * @param request the portlet request
     * @return long a long integer specifying the time the response of the PortletInfo
     * object was last modified, in milliseconds since midnight, January 1, 1970 GMT, or -1 if the time is not known
     */
    public abstract long getLastModified(PortletRequest request);

    /**
     * Returns the PortletConfig object of the portlet
     *
     * @return the PortletConfig object
     */
    protected abstract PortletConfig getPortletConfig();

    /**
     * Returns the PortletSettings object of the concrete portlet.
     *
     * @return the PortletSettings object, or NULL if no PortletSettings object is available.
     */
    protected PortletSettings getPortletSettings() {
        return this.portletSettings;
    }

    /**
     * Initializes the PortletConfig using the web.xml file entry for this portlet
     */
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("in init(ServletConfig)");
    }

    public final void init() throws ServletException {
        super.init();
    }

    public final ServletConfig getServletConfig() {
        return super.getServletConfig();
    }

    public final String getInitParameter(String name) {
        return super.getInitParameter(name);
    }

    public final Enumeration getInitParameterNames() {
        return super.getInitParameterNames();
    }

    public final ServletContext getServletContext() {
        return super.getServletContext();
    }

    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }

    public String getServletInfo() {
        return super.getServletInfo();
    }

    public final void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("in Portlet: service(HttpServletRequest, HttpServletResponse)");

        // create portlet request and response objects
        PortletRequest portletRequest = new SportletRequestImpl(request);
        PortletResponse portletResponse = new SportletResponse(response, portletRequest);

        String method = (String)request.getAttribute(PortletProperties.PORTLET_LIFECYCLE_METHOD);
        log.info("in Portlet: lifecycle method=" + method);
        try {
            if (method.equals(PortletProperties.INIT)) {
                PortletApp app = (PortletApp)request.getAttribute(PortletProperties.PORTLET_APPLICATION);
                this.portletConfig = new SportletConfig(getServletConfig(), app);
                init(this.portletConfig);
            } else if (method.equals(PortletProperties.SERVICE)) {
                service(portletRequest, portletResponse);
            } else if (method.equals(PortletProperties.DESTROY)) {
                destroy(this.portletConfig);
            } else if (method.equals(PortletProperties.INIT_CONCRETE)) {
                PortletSettings settings = (PortletSettings)request.getAttribute(PortletProperties.PORTLET_SETTINGS);
                initConcrete(settings);
            } else if (method.equals(PortletProperties.DESTROY_CONCRETE)) {
                PortletSettings settings = (PortletSettings)request.getAttribute(PortletProperties.PORTLET_SETTINGS);
                destroyConcrete(settings);
            } else if (method.equals(PortletProperties.LOGIN)) {
                login(portletRequest);
            } else if (method.equals(PortletProperties.LOGOUT)) {
                PortletSession portletSession = (PortletSession)portletRequest.getPortletSession();
                logout(portletSession);
            } else {
                log.error("Portlet received unsupported lifecycle method: " + method);
            }
        } catch (UnavailableException e) {

        }
        request.removeAttribute(PortletProperties.PORTLET_LIFECYCLE_METHOD);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        log.info("in Portlet: doGet");
        super.doGet(req, res);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        log.info("in Portlet: doPost");
        super.doPut(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        log.info("in Portlet: doPost");
        super.doPost(req, res);
    }

    protected void doTrace(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        log.info("in Portlet: doTrace");
        super.doTrace(req, res);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        log.info("in Portlet: doDelete");
        super.doDelete(req, res);
    }

    public final void destroy() {
        log.info("in Portlet: destroy");
        super.destroy();
    }

}










































