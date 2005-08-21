/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.layout.view.classic;

import org.gridlab.gridsphere.portlet.*;
import org.gridlab.gridsphere.portlet.impl.SportletProperties;
import org.gridlab.gridsphere.portletcontainer.GridSphereEvent;
import org.gridlab.gridsphere.layout.PortletPage;
import org.gridlab.gridsphere.layout.PortletComponent;
import org.gridlab.gridsphere.layout.view.Render;
import org.gridlab.gridsphere.layout.view.BaseRender;

import java.util.*;
import java.awt.*;

public class Page extends BaseRender implements Render {

    /**
     * Constructs an instance of PortletPage
     */
    public Page() {
    }

    public StringBuffer doStart(GridSphereEvent event, PortletComponent component) {

        PortletRequest req = event.getPortletRequest();

        StringBuffer page = new StringBuffer();

        PortletPage portletPage = (PortletPage)component;
        // page header
        Locale locale = req.getLocale();
        ComponentOrientation orientation = ComponentOrientation.getOrientation(locale);
        if (orientation.isLeftToRight()) {
            page.append("<html>");
        } else {
            page.append("<html dir=\"rtl\">");
        }

        page.append("\n\t<head>");
        page.append("\n\t<title>" + portletPage.getTitle() + "</title>");
        page.append("\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
        page.append("\n\t<meta name=\"keywords\" content=\"" + portletPage.getKeywords() + "\"/>");
        page.append("\n\t<meta http-equiv=\"Pragma\" content=\"no-cache\"/>");

        page.append("\n\t<link type=\"text/css\" href=\"" +
                req.getContextPath() + "/themes/" + portletPage.getTheme() + "/css" +
                    "/default.css\" rel=\"stylesheet\"/>");

        // Add portlet defined stylesheet if defined
        Map props = (Map)req.getAttribute(SportletProperties.PORTAL_PROPERTIES);
        if (props != null) {
            String cssHref = (String)props.get("CSS_HREF");
            if (cssHref != null) {
                page.append("\n\t<link type=\"text/css\" href=\"" + cssHref + " rel=\"stylesheet\"/>");
            }
        }
        page.append("\n\t<link rel=\"icon\" href=\"images/favicon.ico\" type=\"imge/x-icon\"/>");
        page.append("\n\t<link rel=\"shortcut icon\" href=\"" + req.getContextPath() + "/images/favicon.ico\" type=\"image/x-icon\"/>");
        page.append("\n\t<script language=\"JavaScript\" src=\"" + req.getContextPath() + "/javascript/gridsphere.js\"></script>");
        page.append("\n\t</head>\n\t<body>\n");
        return page;
    }

    public StringBuffer doEnd(GridSphereEvent event, PortletComponent comp) {
        return new StringBuffer("\n</body></html>");
    }
}
