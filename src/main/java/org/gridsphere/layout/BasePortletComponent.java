/*
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridsphere.portlet.impl.SportletProperties;
import org.gridsphere.portletcontainer.GridSphereEvent;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * <code>BasePortletComponent</code> represents an abstract portlet component with a particular
 * size, layout and theme and is subclasses by concrete portlet component instances.
 */
public abstract class BasePortletComponent extends BaseComponentLifecycle implements PortletComponent, Serializable {

    protected URL LAYOUT_MAPPING_PATH = this.getClass().getResource("/config/mapping/layout-mapping.xml");

    protected PortletComponent parent = null;
    protected String defaultWidth = "";
    protected String width = "";
    protected String label = "";
    protected String style = "";

    protected boolean isVisible = true;
    protected String requiredRoleName = "";

    protected List<PortletComponent> listeners = null;

    protected boolean canModify = false;

    protected Log log = LogFactory.getLog(PortletPageFactory.class);


    /**
     * Initializes the portlet component. Since the components are isolated
     * after Castor unmarshalls from XML, the ordering is determined by a
     * passed in List containing the previous portlet components in the tree.
     *
     * @param list a list of component identifiers
     * @return a list of updated component identifiers
     * @see ComponentIdentifier
     */
    public List<ComponentIdentifier> init(PortletRequest req, List<ComponentIdentifier> list) {
        listeners = new Vector<PortletComponent>();
        defaultWidth = width;

        if ((label == null) || label.equals("")) {
            return super.init(req, list);
        } else {
            this.COMPONENT_ID = list.size();
            componentIDStr = label;
            return list;
        }
    }

    /**
     * Returns the portlet component label
     *
     * @return the portlet component label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the portlet component label
     *
     * @param label the portlet component label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Allows a required role to be associated with viewing this portlet
     *
     * @param roleString the required portlet role expressed as a <code>String</code>
     */
    public void setRequiredRole(String roleString) {
        this.requiredRoleName = roleString;
    }

    /**
     * Allows a required role to be associated with viewing this portlet
     *
     * @return the required portlet role expressed as a <code>String</code>
     */
    public String getRequiredRole() {
        return requiredRoleName;
    }

    /**
     * Sets the portlet component width
     *
     * @param width the portlet component width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Returns the portlet component width
     *
     * @return the portlet component width
     */
    public String getWidth() {
        return width;
    }

    public void setCanModify(boolean canModify) {
        this.canModify = canModify;
    }

    public boolean getCanModify() {
        return canModify;
    }


    /**
     * Returns the portlet component css.
     *
     * @return portlet component css
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the portlet component css
     *
     * @param style style to be set
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Returns the default portlet component width
     *
     * @return the default portlet component width
     */
    public String getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * When set to true the portlet component is visible and will be rendered
     *
     * @param isVisible if <code>true</code> portlet component is rendered,
     *                  <code>false</code> otherwise
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * Return true if the portlet component visibility is true
     *
     * @return the portlet component visibility
     */
    public boolean getVisible() {
        return isVisible;
    }

    public PortletComponent getParentComponent() {
        return parent;
    }

    public void setParentComponent(PortletComponent parent) {
        this.parent = parent;
    }

    public void remove(PortletComponent pc) {
        if (parent != null) parent.remove(this);
    }

    public void remove() {
        if (parent != null) parent.remove();
    }

    /**
     * Performs an action on this portlet component
     *
     * @param event a gridsphere event
     */
    public void actionPerformed(GridSphereEvent event) {
        super.actionPerformed(event);
    }

    /**
     * Renders the portlet component
     *
     * @param event a gridsphere event
     */
    public void doRender(GridSphereEvent event) {
        PortletRequest req = event.getRenderRequest();
        req.setAttribute(SportletProperties.COMPONENT_ID, componentIDStr);
        req.setAttribute(SportletProperties.COMPONENT_ID_NUM, String.valueOf(COMPONENT_ID));
    }

    public void setBufferedOutput(PortletRequest req, StringBuffer sb) {
        req.setAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID, sb);
    }

    public StringBuffer getBufferedOutput(PortletRequest req) {
        StringBuffer sb = (StringBuffer) req.getAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID);
        req.removeAttribute(SportletProperties.RENDER_OUTPUT + COMPONENT_ID);
        return ((sb != null) ? sb : new StringBuffer());
    }

    public void addComponentListener(PortletComponent component) {
        listeners.add(component);
    }

    public String getLocalizedText(PortletRequest req, String key) {
        Locale locale = req.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("gridsphere.resources.Portlet", locale);
        return bundle.getString(key);
    }

    public Object clone() throws CloneNotSupportedException {
        BasePortletComponent b = (BasePortletComponent) super.clone();
        b.width = this.width;
        b.isVisible = this.isVisible;
        b.label = this.label;
        b.requiredRoleName = this.requiredRoleName;
        b.defaultWidth = defaultWidth;
        b.canModify = canModify;
        return b;
    }

    protected Object getRenderClass(PortletRequest req, String renderClassName) {
        Object render = null;
        String renderKit = (String) req.getPortletSession().getAttribute(SportletProperties.LAYOUT_RENDERKIT, PortletSession.APPLICATION_SCOPE);
        try {
            render = Class.forName("org.gridsphere.layout.view." + renderKit + "." + renderClassName).newInstance();
        } catch (Exception e) {
            log.error("Problems using files for renderkit: '" + renderKit + "' renderclass: " + renderClassName, e);
        }
        return render;
    }

}
