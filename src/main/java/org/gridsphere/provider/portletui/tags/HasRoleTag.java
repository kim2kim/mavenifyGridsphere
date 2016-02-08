/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.provider.portletui.tags;

import org.gridsphere.portlet.impl.SportletProperties;

import javax.portlet.RenderRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The <code>HasRoleTag</code> can be used to selectively display presentation based upon a user's role
 */
public class HasRoleTag extends TagSupport {

    protected String role = "";

    /**
     * Sets the user's role
     *
     * @param role the user's role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the user's role
     *
     * @return the user's role
     */
    public String getRole() {
        return role.toString();
    }

    public int doStartTag() throws JspException {
        RenderRequest req = (RenderRequest) pageContext.getAttribute(SportletProperties.RENDER_REQUEST, PageContext.REQUEST_SCOPE);
        if ((req.isUserInRole(role))) {
            return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

}
