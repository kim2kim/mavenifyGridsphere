/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.layout.view.classic;

import org.gridlab.gridsphere.layout.PortletComponent;
import org.gridlab.gridsphere.layout.PortletTitleBar;
import org.gridlab.gridsphere.layout.view.BaseRender;
import org.gridlab.gridsphere.layout.view.Render;
import org.gridlab.gridsphere.portletcontainer.GridSphereEvent;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class TitleBar extends BaseRender implements Render {

    /**
     * Constructs an instance of PortletTitleBar
     */
    public TitleBar() {
    }

    public StringBuffer doStart(GridSphereEvent event, PortletComponent comp) {
        PortletTitleBar titleBar = (PortletTitleBar)comp;
        StringBuffer titleBuffer = new StringBuffer();
        if (titleBar.isActive()) {
            titleBuffer.append("<tr><td class=\"window-title-active\">");
        } else {
            titleBuffer.append("<tr><td class=\"window-title-inactive\">");
        }
        titleBar.setActive(false);

        titleBuffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
        // Output portlet mode icons
        List modeLinks = titleBar.getModeLinks();
        if (modeLinks != null) {
            Iterator modesIt = modeLinks.iterator();
            titleBuffer.append("<td class=\"window-icon-left\">");
            PortletTitleBar.PortletModeLink mode;
            while (modesIt.hasNext()) {
                mode = (PortletTitleBar.PortletModeLink) modesIt.next();
                titleBuffer.append("<a href=\"" + mode.getHref() + "\"><img border=\"0\" src=\"themes" + File.separator + titleBar.getTheme() + File.separator + mode.getImageSrc() + "\" title=\"" + mode.getAltTag() + "\"/></a>");
            }
            titleBuffer.append("</td>");
        }
        titleBuffer.append("<td class=\"window-title-name\">");
        return titleBuffer;
    }

    public StringBuffer doEnd(GridSphereEvent event, PortletComponent comp) {
        PortletTitleBar titleBar = (PortletTitleBar)comp;
        StringBuffer titleBuffer = new StringBuffer();
        titleBuffer.append("</td>");
        // Output window state icons
        List windowLinks = titleBar.getWindowLinks();
        if (windowLinks != null) {
            Iterator windowsIt = windowLinks.iterator();
            PortletTitleBar.PortletStateLink state;

            titleBuffer.append("<td class=\"window-icon-right\">");
            while (windowsIt.hasNext()) {
                state = (PortletTitleBar.PortletStateLink) windowsIt.next();
                titleBuffer.append("<a href=\"" + state.getHref() + "\"><img border=\"0\" src=\"themes/" + titleBar.getTheme() + File.separator + state.getImageSrc() + "\" title=\"" + state.getAltTag() + "\"/></a>");
            }
            titleBuffer.append("</td>");
        }
        titleBuffer.append("</tr></table>");
        titleBuffer.append("</td></tr>");
        return titleBuffer;
    }


}

