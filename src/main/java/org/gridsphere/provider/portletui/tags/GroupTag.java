package org.gridsphere.provider.portletui.tags;

import org.gridsphere.provider.portletui.beans.GroupBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/*
 * @author <a href="mailto:oliver.wehrens@aei.mpg.de">Oliver Wehrens</a>
 * @version $Id$
 */

public class GroupTag extends BaseComponentTag {

    private String label = null;
    private String width = null;
    private String height = null;
    private GroupBean groupBean = null;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int doStartTag() throws JspException {
        groupBean = new GroupBean();
        groupBean.setHeight(height);
        groupBean.setWidth(width);
        groupBean.setLabel(label);
        groupBean.setCssClass(cssClass);
        groupBean.setCssStyle(cssStyle);
        if (key != null) {
            groupBean.setLabel(getLocalizedText(key));
        }

        try {
            JspWriter out = pageContext.getOut();
            out.print(groupBean.toStartString());
        } catch (Exception e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.print(groupBean.toEndString());
        } catch (Exception e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

}
