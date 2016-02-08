/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.provider.portletui.tags;

import org.gridsphere.portlet.impl.SportletProperties;
import org.gridsphere.portlet.impl.StoredPortletResponseImpl;
import org.gridsphere.provider.portletui.beans.TabBean;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>TabbedPaneTag</code> represents a tabbed pane</code>
 */
public class TabbedPaneTag extends BaseComponentTag {


    protected String TAB_LABEL_PARAM = "ui.tab.label";
    protected String currentPage = "";
    protected String currentTabLabel = "";

    protected List<TabBean> tabBeans = new ArrayList<TabBean>();

    public void addTabBean(TabBean tabBean) {
        tabBeans.add(tabBean);
    }

    public void setCurrentTab(String currentTab) {
        this.currentTabLabel = currentTab;
    }

    public String getCurrentTab() {
        return currentTabLabel;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public int doStartTag() throws JspException {
        super.doStartTag();
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        if (tabBeans.isEmpty()) return EVAL_PAGE;

        try {

            RenderRequest req = (RenderRequest) pageContext.getAttribute(SportletProperties.RENDER_REQUEST, PageContext.REQUEST_SCOPE);
            currentTabLabel = req.getParameter(TAB_LABEL_PARAM);
            if (currentTabLabel == null) {
                currentTabLabel = (String) req.getPortletSession(true).getAttribute(getClass().getName());
            }

            JspWriter out = pageContext.getOut();
            out.println("<ul class=\"ui-tab\">");
            // if this tab is not set, then use this tab (the first tab in the sequence)
            if (currentTabLabel == null) {
                currentTabLabel = tabBeans.get(0).getLabel();
            }

            RenderResponse res = (RenderResponse) pageContext.getAttribute(SportletProperties.RENDER_RESPONSE, PageContext.REQUEST_SCOPE);

            // print out all tabs
            for (TabBean tabBean : tabBeans) {
                PortletURL url = res.createRenderURL();
                url.setParameter(TAB_LABEL_PARAM, tabBean.getLabel());
                String href = url.toString();

                if (tabBean.getLabel().equals(currentTabLabel)) {
                    req.getPortletSession(true).setAttribute(getClass().getName(), currentTabLabel);
                    currentPage = tabBean.getPage();
                    out.println("<li class=\"selected\">");
                } else {
                    out.println("<li>");
                }
                out.println("<a href=\"" + href + "\">" + tabBean.getValue() + "</a>");
                out.println("</li>");
            }

            out.println("</ul>");

            StringWriter writer = new StringWriter();
            ServletResponse sres = pageContext.getResponse();
            if (res instanceof HttpServletResponse) {
                HttpServletResponse hres = (HttpServletResponse) sres;
                HttpServletRequest hreq = (HttpServletRequest) pageContext.getRequest();
                StoredPortletResponseImpl resWrapper = new StoredPortletResponseImpl(hreq, hres, writer);
                pageContext.getServletContext().getRequestDispatcher(currentPage).include(pageContext.getRequest(), resWrapper);
                out.println(writer.getBuffer());
            }
            tabBeans.clear();
        } catch (Exception e) {
            throw new JspException(e);
        }
        super.release();
        return EVAL_PAGE;
    }
}
