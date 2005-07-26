/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.provider.portletui.tags;

import org.gridlab.gridsphere.portlet.impl.SportletProperties;
import org.gridlab.gridsphere.portlet.jsrimpl.RenderResponseImpl;
import org.gridlab.gridsphere.services.core.tracker.TrackerService;

import javax.portlet.RenderResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.net.URLEncoder;

/**
 * The <code>ActionFormTag</code> provides a UI form tag that can also include <code>ActionParam</code> tags
 * nested within it.
 */
public class ActionFormTag extends ActionTag {

    protected boolean isMultipart = false;
    protected String method = "POST";

    /**
     * Sets the form method attribute e.g. POST
     *
     * @param method the form method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Returns the form method attribute e.g. POST
     *
     * @return the form method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets whether this form supports multi-part form data as in the case of file uploading
     *
     * @param isMultipart is true if this form supports multi-part form data, false otherwise
     */
    public void setMultiPartFormData(boolean isMultipart) {
        this.isMultipart = isMultipart;
    }

    /**
     * Returns whether this form supports multi-part form data as in the case of file uploading
     *
     * @return true if this form supports multi-part form data, false otherwise
     */
    public boolean getMultiPartFormData() {
        return isMultipart;
    }

    public int doStartTag() throws JspException {
        paramBeans = new ArrayList();
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();

            out.print("<form ");
            out.print("action=\"");

            // if using JSR then create render link
            RenderResponse res = (RenderResponse) pageContext.getAttribute(SportletProperties.RENDER_RESPONSE, PageContext.REQUEST_SCOPE);
            String actionStr;
            if (res != null) {
                if (res instanceof RenderResponseImpl) {
                    actionStr = createJSRActionURI(res.createRenderURL()).toString();
                } else {
                    // if non-GS container this will have to be an ActionURL
                    actionStr = createJSRActionURI(res.createActionURL()).toString();
                }
            } else {
                actionStr = createActionURI();
            }

            out.print(actionStr);


            out.print("\" method=\"");
            out.print(method);
            out.print("\"");
            if (isMultipart) {
                out.print(" enctype=\"multipart/form-data\"");
            }
            if (name == null) {
                name = "form" + this.getUniqueId("gs_formNumber");
            }

            out.print(" name=\"" + name + "\"");

            out.println(">");
            // add JS info
            out.println("<input name=\"JavaScript\" value=\"\" type=\"hidden\">");


            out.println("<script language=\"JavaScript\">");
            out.println("document." + name + ".JavaScript.value = \"enabled\";");
            out.println("</script>");
            if (trackMe != null) {
                out.println("<input name=\"" + TrackerService.TRACK_PARAM + "\" value=\"" + trackMe + "\" type=\"hidden\"/>");
            }
            if (extUrl != null) {
                out.println("<input name=\"" + TrackerService.REDIRECT_URL + "\" value=\"" +extUrl + "\" type=\"hidden\"/>");
            }


            // write out rest of body
            bodyContent.writeOut(getPreviousOut());
            // end form
            out.print("</form>");
        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
        name = null;
        return EVAL_PAGE;
    }

}
