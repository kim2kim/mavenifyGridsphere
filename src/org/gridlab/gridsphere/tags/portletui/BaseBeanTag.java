/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
package org.gridlab.gridsphere.tags.portletui;

import org.gridlab.gridsphere.portletcontainer.GridSphereProperties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Enumeration;

public abstract class BaseBeanTag extends TagSupport {

    protected String beanId = "";

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    protected String getBeanKey() {
        String compId = (String)pageContext.findAttribute(GridSphereProperties.COMPONENT_ID);
        System.err.println("in BaseBeanTag: beankey: " + beanId + "_" + compId);
        return beanId + "_" + compId;
    }

    protected void store(String id, Object object) {
        if (!beanId.equals("")) {
            System.err.println("in BaseBeanTag: saving " + id + " into session");
            pageContext.getSession().setAttribute(id, object);
        }
    }

    public void debug() {
        Enumeration enum = pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE);
        System.err.println("Printing attribues in request scope");
        while (enum.hasMoreElements()) {
            System.err.println((String)enum.nextElement());
        }
        enum = pageContext.getAttributeNamesInScope(PageContext.SESSION_SCOPE);
        System.err.println("Printing attribues in session scope");
        while (enum.hasMoreElements()) {
            System.err.println((String)enum.nextElement());
        }
        enum = pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);
        System.err.println("Printing attribues in page scope");
        while (enum.hasMoreElements()) {
            System.err.println((String)enum.nextElement());
        }
        enum = pageContext.getAttributeNamesInScope(PageContext.APPLICATION_SCOPE);
        System.err.println("Printing attribues in application scope");
        while (enum.hasMoreElements()) {
            System.err.println((String)enum.nextElement());
        }
    }


}
