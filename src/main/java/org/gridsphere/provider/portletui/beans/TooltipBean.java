package org.gridsphere.provider.portletui.beans;

import org.gridsphere.portlet.impl.SportletProperties;
import org.gridsphere.portlet.service.spi.PortletServiceFactory;
import org.gridsphere.services.core.portal.PortalConfigService;

import javax.portlet.RenderResponse;

public class TooltipBean extends BaseComponentBean {

    public String name = null;
    public String key = null;
    public String value = "";
    public String id = null;

    /**
     * Constructs a default table row bean
     */
    public TooltipBean() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toStartString() {
        StringBuffer sb = new StringBuffer();
        if (key != null) value = getLocalizedText(key);
        value = value.replaceAll("\n", "<br>");
        PortalConfigService configService = (PortalConfigService) PortletServiceFactory.createPortletService(PortalConfigService.class, true);
        String contextPath = "/" + configService.getProperty("gridsphere.deploy");
        renderResponse.setProperty("CSS_HREF", contextPath + "/css/yahoo/container.css");
        renderResponse.addProperty("JAVASCRIPT_SRC", contextPath + "/javascript/yahoo/dom.js");
        renderResponse.addProperty("JAVASCRIPT_SRC", contextPath + "/javascript/yahoo/event.js");
        renderResponse.addProperty("JAVASCRIPT_SRC", contextPath + "/javascript/yahoo/container.js");
        sb.append("<script type=\"text/javascript\">");
        sb.append("var " + id + " = new YAHOO.widget.Tooltip(\"" + id + "\", { context:\"" + name + "\", text:\"" + value + "\" } );");
        sb.append("</script>");
        return sb.toString();
    }

    public String toEndString() {
        return "";
    }

}
