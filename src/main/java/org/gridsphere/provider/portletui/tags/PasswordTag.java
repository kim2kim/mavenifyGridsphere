/*
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @author <a href="mailto:oliver.wehrens@aei.mpg.de">Oliver Wehrens</a>
 * @version $Id$
 */

package org.gridsphere.provider.portletui.tags;

import org.gridsphere.provider.portletui.beans.PasswordBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * A <code>PasswordTag</code> provides a password tag for represnting HTML password input elements
 */
public class PasswordTag extends InputTag {

    protected PasswordBean passwordBean = null;

    protected int size = 10;
    protected int maxlength = 15;

    /**
     * Returns the (html) size of the field
     *
     * @return size of the field
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the (html) size of the field
     *
     * @param size size of the field
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Returns the (html) max length of the field
     *
     * @return the max length of the field
     */
    public int getMaxlength() {
        return maxlength;
    }

    /**
     * Sets the (html) max length of the field
     *
     * @param maxlength the max length of the field
     */
    public void setMaxlength(int maxlength) {
        this.maxlength = maxlength;
    }

    public int doStartTag() throws JspException {
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        if (!beanId.equals("")) {
            passwordBean = (PasswordBean) getTagBean();
            if (passwordBean == null) {
                passwordBean = new PasswordBean();
                this.setBaseComponentBean(passwordBean);
            } else {
                this.updateBaseComponentBean(passwordBean);
            }
        } else {
            passwordBean = new PasswordBean();
            passwordBean.setMaxLength(maxlength);
            passwordBean.setSize(size);
            this.setBaseComponentBean(passwordBean);
        }

        if (maxlength != 0) passwordBean.setMaxLength(maxlength);
        if (size != 0) passwordBean.setSize(size);
        if (onFocus != null) passwordBean.setOnFocus(onFocus);
        if (onClick != null) passwordBean.setOnClick(onClick);
        if (onChange != null) passwordBean.setOnChange(onChange);
        if (onBlur != null) passwordBean.setOnBlur(onBlur);
        if (onSelect != null) passwordBean.setOnSelect(onBlur);

        if (cssClass != null) passwordBean.addCssClass(cssClass);
        if (cssStyle != null) passwordBean.addCssStyle(cssStyle);

        try {
            JspWriter out = pageContext.getOut();
            out.print(passwordBean.toStartString());
        } catch (Exception e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

}
