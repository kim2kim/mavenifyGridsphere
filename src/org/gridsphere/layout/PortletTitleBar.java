/*
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id: PortletTitleBar.java 5032 2006-08-17 18:15:06Z novotny $
 */
package org.gridsphere.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridsphere.layout.event.PortletTitleBarEvent;
import org.gridsphere.layout.event.PortletTitleBarListener;
import org.gridsphere.layout.event.PortletWindowEvent;
import org.gridsphere.layout.event.impl.PortletTitleBarEventImpl;
import org.gridsphere.layout.event.impl.PortletWindowEventImpl;
import org.gridsphere.layout.view.Render;
import org.gridsphere.portlet.impl.SportletProperties;
import org.gridsphere.portlet.impl.StoredPortletResponseImpl;
import org.gridsphere.portlet.service.spi.PortletServiceFactory;
import org.gridsphere.portletcontainer.ApplicationPortlet;
import org.gridsphere.portletcontainer.GridSphereEvent;
import org.gridsphere.portletcontainer.impl.PortletInvoker;
import org.gridsphere.services.core.registry.PortletRegistryService;
import org.gridsphere.services.core.security.role.PortletRole;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.security.Principal;
import java.util.*;

/**
 * A <code>PortletTitleBar</code> represents the visual display of the portlet title bar
 * within a portlet frame and is contained by {@link PortletFrame}.
 * The title bar contains portlet mode and window state as well as a title.
 */
public class PortletTitleBar extends BasePortletComponent implements Serializable, Cloneable {

    private Log log = LogFactory.getLog(PortletTitleBar.class);

    private String title = "unknown title";
    private String portletClass = null;

    private transient PortletRegistryService portletRegistryService = null;
    private transient PortletInvoker portletInvoker = null;

    private transient WindowState windowState = WindowState.NORMAL;

    private transient PortletMode portletMode = PortletMode.VIEW;
    private transient PortletMode previousMode = PortletMode.VIEW;
    private transient List allowedWindowStates = new ArrayList();

    private transient String errorMessage = "";
    private transient boolean hasError = false;
    private transient boolean isActive = false;

    private transient List modeLinks = null;
    private transient List windowLinks = null;

    private transient Render titleView = null;


    /**
     * Link is an abstract representation of a hyperlink with an href, image and
     * alt tags.
     */
    abstract class Link {
        protected String href = "";
        protected String imageSrc = "";
        protected String altTag = "";
        protected String symbol = "";
        protected String cursor = "";

        /**
         * Returns the image source attribute in the link
         *
         * @return the image source attribute in the link
         */
        public String getImageSrc() {
            return imageSrc;
        }

        public String getSymbol() { //WAP 2.0 Extention
            return symbol;
        }

        /**
         * Returns the CSS cursor style to use
         *
         * @return the cursor
         */
        public String getCursor() {
            return cursor;
        }

        /**
         * Sets the CSS cursor style to use
         *
         * @param cursor the cursor
         */
        public void setCursor(String cursor) {
            this.cursor = cursor;
        }

        /**
         * Sets the href attribute in the link
         *
         * @param href the href attribute in the link
         */
        public void setHref(String href) {
            this.href = href;
        }

        /**
         * Returns the href attribute in the link
         *
         * @return the href attribute in the link
         */
        public String getHref() {
            return href;
        }

        /**
         * Returns the alt tag attribute in the link
         *
         * @return the alt tag attribute in the link
         */
        public String getAltTag() {
            return altTag;
        }

        /**
         * Returns a string containing the image src, href and alt tag attributes
         * Used primarily for debugging purposes
         */
        public String toString() {
            StringBuffer sb = new StringBuffer("\n");
            sb.append("image src: ").append(imageSrc).append("\n");
            sb.append("href: ").append(href).append("\n");
            sb.append("alt tag: ").append(altTag).append("\n");
            return sb.toString();
        }
    }

    /**
     * PortletModeLink is a concrete instance of a Link used for creating
     * portlet mode hyperlinks
     */
    public class PortletModeLink extends Link {

        public static final String configImage = "images/window_configure.gif";
        public static final String configSymbol = "c";//WAP 2.0 Extention
        public static final String editImage = "images/window_edit.gif";
        public static final String editSymbol = "/";//WAP 2.0 Extention
        public static final String helpImage = "images/window_help.gif";
        public static final String helpSymbol = "?";//WAP 2.0 Extention
        public static final String viewImage = "images/window_view.gif";
        public static final String viewSymbol = "V";//WAP 2.0Extention

        /**
         * Constructs an instance of PortletModeLink with the supplied portlet mode
         *
         * @param mode the portlet mode
         * @param locale the locale
         * @throws IllegalArgumentException if the mode is not supported
         */
        public PortletModeLink(PortletMode mode, Locale locale) throws IllegalArgumentException {
            if (mode == null) return;

            ResourceBundle bundle = ResourceBundle.getBundle("gridsphere.resources.Portlet", locale);
            String key = mode.toString().toUpperCase();
            altTag = bundle.getString(key);
            // Set the image src
            if (mode.equals(new PortletMode("CONFIGURE"))) {
                imageSrc = configImage;
                symbol=configSymbol;//WAP 2.0
            } else if (mode.equals(PortletMode.EDIT)) {
                imageSrc = editImage;
                symbol=editSymbol;//WAP 2.0
            } else if (mode.equals(PortletMode.HELP)) {
                imageSrc = helpImage;
                symbol=helpSymbol;//WAP 2.0
                cursor = "help";
            } else if (mode.equals(PortletMode.VIEW)) {
                imageSrc = viewImage;
                symbol=viewSymbol;//WAP 2.0
            } else {
                throw new IllegalArgumentException("Unsupported portlet mode: " + mode);
            }
        }
    }

    /**
     * PortletStateLink is a concrete instance of a Link used for creating
     * portlet window state hyperlinks
     */
    public class PortletStateLink extends Link {

        public static final String closeImage = "images/window_close.gif";
        public static final String minimizeImage = "images/window_minimize.gif";
        public static final String maximizeImage = "images/window_maximize.gif";
        public static final String resizeImage = "images/window_resize.gif";
        public static final String floatImage = "images/window_float.gif";

        public static final String closeSymbol = "X"; //WAP 2.0
        public static final String minimizeSymbol = "_"; //WAP 2.0
        public static final String maximizeSymbol = "="; //WAP 2.0
        public static final String resizeSymbol = "-"; //WAP 2.0
        public static final String floatSymbol = "^"; //WAP 2.0

        /**
         * Constructs an instance of PortletStateLink with the supplied window state
         *
         * @param state the window state
         * @param locale the client locale
         * @throws IllegalArgumentException if the state is unsupported
         */
        public PortletStateLink(WindowState state, Locale locale) throws IllegalArgumentException {
            if (state == null) return;
            // Set the image src
            if (state.equals(WindowState.MINIMIZED)) {
                imageSrc = minimizeImage;
                symbol = minimizeSymbol;
            } else if (state.equals(WindowState.MAXIMIZED)) {
                imageSrc = maximizeImage;
                symbol = maximizeSymbol;
            } else if (state.equals(new WindowState("resizing"))) {
                 imageSrc = resizeImage;
                symbol = resizeSymbol;
            } else if (state.equals(new WindowState("closed"))) {
                imageSrc = closeImage;
                symbol = closeSymbol;
            } else if (state.equals(new WindowState("floating"))) {
                imageSrc = floatImage;
                symbol = floatSymbol;
            } else {
                throw new IllegalArgumentException("Unsupported window state window mode: " + state);
            }
            ResourceBundle bundle = ResourceBundle.getBundle("gridsphere.resources.Portlet", locale);
            String key = state.toString().toUpperCase();
            altTag = bundle.getString(key);
        }


    }

    /**
     * Constructs an instance of PortletTitleBar
     */
    public PortletTitleBar() {
    }

    /**
     * Sets the portlet class used to render the title bar
     *
     * @param portletClass the concrete portlet class
     */
    public void setPortletClass(String portletClass) {
        this.portletClass = portletClass;
    }

    /**
     * Returns the portlet class used in rendering the title bar
     *
     * @return the concrete portlet class
     */
    public String getPortletClass() {
        return portletClass;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Returns the title of the portlet title bar
     *
     * @return the portlet title bar
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the portlet title bar
     *
     * @param title the portlet title bar
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the window state of this title bar
     *
     * @param state the portlet window state expressed as a string
     */
    public void setWindowState(WindowState state) {
        if (state != null) this.windowState = state;
    }

    /**
     * Returns the window state of this title bar
     *
     * @return the portlet window state expressed as a string
     */
    public WindowState getWindowState() {
        return windowState;
    }

    /**
     * Sets the window state of this title bar
     *
     * @param state the portlet window state expressed as a string
     */
    public void setWindowStateAsString(String state) {
        if (state != null) {
            try {
                this.windowState = new WindowState(state);
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
    }

    /**
     * Returns the window state of this title bar
     *
     * @return the portlet window state expressed as a string
     */
    public String getWindowStateAsString() {
        return windowState.toString();
    }

    /**
     * Sets the portlet mode of this title bar
     *
     * @param mode the portlet mode expressed as a string
     */
    public void setPortletMode(PortletMode mode) {
        if (mode != null) this.portletMode = mode;
    }

    /**
     * Returns the portlet mode of this title bar
     *
     * @return the portlet mode expressed as a string
     */
    public PortletMode getPortletMode() {
        return portletMode;
    }

    /**
     * Sets the portlet mode of this title bar
     *
     * @param mode the portlet mode expressed as a string
     */
    public void setPreviousMode(PortletMode mode) {
        if (mode != null) this.previousMode = mode;
    }

    /**
     * Returns the portlet mode of this title bar
     *
     * @return the portlet mode expressed as a string
     */
    public PortletMode getPreviousMode() {
        return previousMode;
    }

    /**
     * Sets the portlet mode of this title bar
     *
     * @param mode the portlet mode expressed as a string
     */
    public void setPortletModeAsString(String mode) {
        if (mode == null) return;
        try {
            this.portletMode = new PortletMode(mode);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    /**
     * Returns the portlet mode of this title bar
     *
     * @return the portlet mode expressed as a string
     */
    public String getPortletModeAsString() {
        return portletMode.toString();
    }

    /**
     * Adds a title bar listener to be notified of title bar events
     *
     * @param listener a title bar listener
     * @see PortletTitleBarEvent
     */
    public void addTitleBarListener(PortletTitleBarListener listener) {
        listeners.add(listener);
    }

    /**
     * Indicates an error ocurred suring the processing of this title bar
     *
     * @return <code>true</code> if an error occured during rendering,
     *         <code>false</code> otherwise
     */
    public boolean hasRenderError() {
        return hasError;
    }

    /**
     * Returns any errors associated with the functioning of this title bar
     *
     * @return any title bar errors that occured
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Initializes the portlet title bar. Since the components are isolated
     * after Castor unmarshalls from XML, the ordering is determined by a
     * passed in List containing the previous portlet components in the tree.
     *
     * @param list a list of component identifiers
     * @return a list of updated component identifiers
     * @see ComponentIdentifier
     */
    public List init(PortletRequest req, List list) {
        list = super.init(req, list);
        titleView = (Render)getRenderClass(req, "TitleBar");
        portletInvoker = new PortletInvoker();
        ComponentIdentifier compId = new ComponentIdentifier();
        compId.setPortletComponent(this);
        compId.setPortletClass(portletClass);
        compId.setComponentID(list.size());
        compId.setComponentLabel(label);
        compId.setClassName(this.getClass().getName());
        list.add(compId);
        portletRegistryService = (PortletRegistryService)PortletServiceFactory.createPortletService(PortletRegistryService.class, true);
        String appID = portletRegistryService.getApplicationPortletID(portletClass);
        ApplicationPortlet appPortlet = portletRegistryService.getApplicationPortlet(appID);
        if (appPortlet != null) {
            allowedWindowStates = new ArrayList(appPortlet.getAllowedWindowStates());
            allowedWindowStates = sort(allowedWindowStates);
            if (canModify) {
                if (!allowedWindowStates.contains(new WindowState("CLOSED"))) {
                    allowedWindowStates.add(new WindowState("CLOSED"));
                }
            }
        }
        return list;
    }

    /**
     * Simple sorting algoritm that sorts in increasing order a <code>List</code>
     * containing objects that implement <code>Comparator</code>
     *
     * @param list a <code>List</code> to be sorted
     * @return the sorted list
     */
    private List sort(List list) {

        List tmp = new ArrayList();
        if (list.contains(WindowState.NORMAL)) {
            tmp.add(WindowState.NORMAL);
        }
        if (list.contains(WindowState.MINIMIZED)) {
            tmp.add(WindowState.MINIMIZED);
        }
        if (list.contains(new WindowState("RESIZING"))) {
            tmp.add(new WindowState("RESIZING"));
        }
        if (list.contains(WindowState.MAXIMIZED)) {
            tmp.add(WindowState.MAXIMIZED);
        }
        if (list.contains(new WindowState("CLOSED"))) {
            tmp.add(new WindowState("CLOSED"));
        }
        if (list.contains(new WindowState("FLOATING"))) {
            tmp.add(new WindowState("FLOATING"));
        }
        return tmp;
    }

    /**
     * Creates the portlet window state hyperlinks displayed in the title bar
     *
     * @param event the gridsphere event
     * @return a list of window state hyperlinks
     */
    public List createWindowLinks(GridSphereEvent event) {
        super.doRender(event);
        PortletURL portletURL;
        RenderResponse res = event.getRenderResponse();
        WindowState tmp;

        if (allowedWindowStates.isEmpty()) return null;

        //String[] windowStates = new String[allowedWindowStates.size()];
        List windowStates = new ArrayList();
        for (int i = 0; i < allowedWindowStates.size(); i++) {

            tmp = (WindowState) allowedWindowStates.get(i);
            windowStates.add(tmp);
            // remove current state from list
            if (tmp.equals(windowState) && (!windowState.equals(new WindowState("closed")))) {
                windowStates.remove(i);
            }
        }
        // get rid of resized if window state is normal
        if (windowState.equals(WindowState.NORMAL) || windowState.equals(new WindowState("closed"))) {
            windowStates.remove(new WindowState("resizing"));
        }

        // get rid of floating if window state is minimized
        if (windowState.equals(WindowState.MINIMIZED)) {
            windowStates.remove(new WindowState("floating"));
        }


        // Localize the window state names
        RenderRequest req = event.getRenderRequest();

        Locale locale = req.getLocale();

        // create a URI for each of the window states
        PortletStateLink stateLink;
        List stateLinks = new Vector();
        for (int i = 0; i < windowStates.size(); i++) {
            tmp = (WindowState) windowStates.get(i);
            portletURL = res.createActionURL();
            //portletURL.setParameter(this.getComponentIDVar(req), this.componentIDStr);

            try {
                stateLink = new PortletStateLink(tmp, locale);
                portletURL.setParameter(SportletProperties.PORTLET_WINDOW, tmp.toString());
                stateLink.setHref(portletURL.toString());
                if (tmp.equals(new WindowState("floating"))) {
                    stateLink.setHref(portletURL.toString() + "\" onclick=\"return GridSphere_popup(this, 'notes')\"");
                }
                stateLinks.add(stateLink);
            } catch (IllegalArgumentException e) {
                // do nothing
            }

        }
        return stateLinks;
    }

    /**
     * Creates the portlet mode hyperlinks displayed in the title bar
     *
     * @param event the gridsphere event
     * @return a list of portlet mode hyperlinks
     */
    public List createModeLinks(GridSphereEvent event) {
        super.doRender(event);
        int i;
        RenderResponse res = event.getRenderResponse();
        RenderRequest req = event.getRenderRequest();
        // make modes from supported modes

        List supportedModes = (List)req.getAttribute(SportletProperties.ALLOWED_MODES);
        if (supportedModes == null) return null;

        // Unless user is admin they should not see configure mode
        boolean hasConfigurePermission = req.isUserInRole(PortletRole.ADMIN.getName());
        List smodes = new ArrayList();
        String mode;
        for (i = 0; i < supportedModes.size(); i++) {
            mode = (String) supportedModes.get(i);
            if (mode.equalsIgnoreCase("configure")) {
                if (hasConfigurePermission) {
                    smodes.add(mode);
                }
            } else {
                smodes.add(mode);
            }

            // remove current mode from list
            smodes.remove(portletMode.toString());
        }

        // Localize the portlet mode names
        Locale locale = req.getLocale();

        List portletLinks = new ArrayList();
        for (i = 0; i < smodes.size(); i++) {
            // create a URI for each of the portlet modes
            PortletURL portletURL;
            PortletModeLink modeLink;
            mode = (String) smodes.get(i);
            portletURL = res.createActionURL();
            //portletURL.setParameter(this.getComponentIDVar(req), this.componentIDStr);
            //portletURI.addParameter(SportletProperties.PORTLETID, portletClass);
            try {
                modeLink = new PortletModeLink(new PortletMode(mode), locale);
                portletURL.setParameter(SportletProperties.PORTLET_MODE, mode.toString());
                modeLink.setHref(portletURL.toString());
                portletLinks.add(modeLink);
            } catch (IllegalArgumentException e) {
                //log.debug("Unable to get mode for : " + mode.toString());
            }
        }

        return portletLinks;
    }

    /**
     * Performs an action on this portlet title bar component
     *
     * @param event a gridsphere event
     */
    public void actionPerformed(GridSphereEvent event) {
        super.actionPerformed(event);
        isActive = true;

        HttpServletRequest req = event.getHttpServletRequest();
        ActionResponse res = event.getActionResponse();

        req.setAttribute(SportletProperties.PORTLETID, portletClass);

        // Render title bar
        List supportedModes = null;
        String appID = portletRegistryService.getApplicationPortletID(portletClass);
        ApplicationPortlet appPortlet = portletRegistryService.getApplicationPortlet(appID);
        if (appPortlet != null) {
            supportedModes = appPortlet.getSupportedModes(event.getClient().getMimeType());
        }
        req.setAttribute(SportletProperties.ALLOWED_MODES, supportedModes);

        // pop last event off stack
        event.getLastRenderEvent();

        PortletTitleBarEvent titleBarEvent = new PortletTitleBarEventImpl(this, event, COMPONENT_ID);

        Principal principal = event.getActionRequest().getUserPrincipal();
        if (principal != null) {

            if (titleBarEvent.hasAction()) {

                if (titleBarEvent.hasWindowStateAction()) {

                    // don't set window state if it is floating
                    if (!titleBarEvent.getState().equals(new WindowState("floating"))) windowState = titleBarEvent.getState();

                    PortletWindowEvent winEvent = null;

                    // if receive a window state that is not supported do nothing
                    if (!allowedWindowStates.contains(windowState)) return;

                    if (windowState.equals(WindowState.MAXIMIZED)) {
                        winEvent = new PortletWindowEventImpl(req, PortletWindowEvent.WINDOW_MAXIMIZED);
                    } else if (windowState.equals(WindowState.MINIMIZED)) {
                        winEvent = new PortletWindowEventImpl(req, PortletWindowEvent.WINDOW_MINIMIZED);
                    } else if (windowState.equals(new WindowState("resizing"))) {
                        winEvent = new PortletWindowEventImpl(req, PortletWindowEvent.WINDOW_RESTORED);
                    } else if (windowState.equals("CLOSED")) {
                        winEvent = new PortletWindowEventImpl(req, PortletWindowEvent.WINDOW_CLOSED);
                    }
                    if (winEvent != null) {
                        try {
                            portletInvoker.windowEvent((String)req.getAttribute(SportletProperties.PORTLETID), winEvent, req, (HttpServletResponse)res);
                        } catch (Exception e) {
                            hasError = true;
                            errorMessage += "Failed to invoke window event method of portlet: " + portletClass;
                        }
                    }
                }
                if (titleBarEvent.hasPortletModeAction()) {
                    /*
                    if (titleBarEvent.getMode().equals(Portlet.Mode.CONFIGURE)) {
                        @TODO fix me
                        boolean hasrole = aclService.hasRequiredRole(req, portletClass, true);
                        if (!hasrole) return;

                    }*/
                    previousMode = portletMode;
                    portletMode = titleBarEvent.getMode();
                    //System.err.println("mode = " + portletMode);
                    //System.err.println("prev mode = " + previousMode);
                }
            }
        }

        req.setAttribute(SportletProperties.PORTLET_WINDOW, windowState);
        try {
            res.setPortletMode(portletMode);
        } catch (PortletModeException e) {
            System.err.println("Unable to set mode to " + portletMode);
        }
        req.setAttribute(SportletProperties.PREVIOUS_MODE, previousMode);

        Iterator it = listeners.iterator();
        PortletComponent comp;
        while (it.hasNext()) {
            comp = (PortletComponent) it.next();
            event.addNewRenderEvent(titleBarEvent);
            comp.actionPerformed(event);
        }
    }

    /**
     * Fires a title bar event notification
     *
     * @param event a portlet title bar event
     */
    protected void fireTitleBarEvent(PortletTitleBarEvent event) {
        Iterator it = listeners.iterator();
        PortletTitleBarListener l;
        while (it.hasNext()) {
            l = (PortletTitleBarListener) it.next();
            l.handleTitleBarEvent(event);
        }
    }

    public List getModeLinks() {
        return modeLinks;
    }

    public List getWindowLinks() {
        return windowLinks;
    }

    public void doRender(GridSphereEvent event) {
        super.doRender(event);
        hasError = false;

        // title bar: configure, edit, help, title, min, max
        RenderRequest req = event.getRenderRequest();
        RenderResponse res = event.getRenderResponse();

        // get the appropriate title for this client

        Locale locale = req.getLocale();

        Principal principal = req.getUserPrincipal();
        if (principal != null) {
            if (portletClass != null) {
                modeLinks = createModeLinks(event);
                windowLinks = createWindowLinks(event);
            }
        }

        //System.err.println("in title bar render portletclass=" + portletClass + ": setting prev mode= " + previousMode + " cur mode= " + portletMode);

        req.setAttribute(SportletProperties.PORTLET_MODE, portletMode);
        req.setAttribute(SportletProperties.PREVIOUS_MODE, previousMode);
        req.setAttribute(SportletProperties.PORTLET_WINDOW, windowState);

        StringBuffer preTitle = titleView.doStart(event, this);
        req.setAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID + ".pre", preTitle.toString());

        StringBuffer postTitle = titleView.doEnd(event, this);
        req.setAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID + ".post", postTitle.toString());

        StringWriter storedWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(storedWriter);
        PortletResponse wrappedResponse = new StoredPortletResponseImpl((HttpServletRequest)req, (HttpServletResponse)res, writer);

        try {
            //System.err.println("invoking  doTitle:" + title);
            portletInvoker.doTitle((String)req.getAttribute(SportletProperties.PORTLETID), (HttpServletRequest)req, (HttpServletResponse)wrappedResponse);
            //out.println(" (" + portletMode.toString() + ") ");
            title = storedWriter.toString();
        } catch (Exception e) {
            ResourceBundle bundle = ResourceBundle.getBundle("gridsphere.resources.Portlet", locale);
            title = bundle.getString("PORTLET_UNAVAILABLE");
            hasError = true;
            errorMessage = portletClass + " " + title + "!\n"; //"PortletException:" + e.getMessage();
            log.error(portletClass + " is currently unavailable:", e);
        }
    }

    public String getPreBufferedTitle(PortletRequest req) {
        String preTitle = (String)req.getAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID + ".pre");
        req.removeAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID + ".pre");
        return preTitle;
    }

    public String getPostBufferedTitle(PortletRequest req) {
        String postTitle = (String)req.getAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID + ".post");
        req.removeAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID + ".post");
        return postTitle;
    }


    public Object clone() throws CloneNotSupportedException {
        PortletTitleBar t = (PortletTitleBar) super.clone();
        t.title = this.title;
        t.portletClass = this.portletClass;
        t.portletMode = new PortletMode(this.portletMode.toString());
        t.windowState = new WindowState(this.windowState.toString());
        t.previousMode = this.previousMode;
        t.errorMessage = this.errorMessage;
        t.hasError = this.hasError;
        t.allowedWindowStates = new ArrayList(this.allowedWindowStates.size());

        return t;

    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());

        return sb.toString();
    }

}

