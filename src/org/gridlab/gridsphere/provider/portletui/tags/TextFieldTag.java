/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @author <a href="mailto:oliver.wehrens@aei.mpg.de">Oliver Wehrens</a>
 * @version $Id$
 */

package org.gridlab.gridsphere.provider.portletui.tags;

import org.gridlab.gridsphere.portlet.PortletLog;
import org.gridlab.gridsphere.portlet.impl.SportletLog;
import org.gridlab.gridsphere.provider.portletui.beans.TextFieldBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * A <code>TextFieldTag</code> represents a text field element
 */
public class TextFieldTag extends BaseComponentTag {

    private transient static PortletLog log = SportletLog.getInstance(TextFieldTag.class);

    protected TextFieldBean textFieldBean = null;
    protected int size = 0;
    protected int maxlength = 0;
    protected String beanIdSource = null;

    public String getBeanidsource() {
        return beanIdSource;
    }

    public void setBeanidsource(String beanIdSource) {
        this.beanIdSource = beanIdSource;
    }

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
        if (!beanId.equals("")) {
            textFieldBean = (TextFieldBean) getTagBean();
            if (textFieldBean == null) {
                //log.debug("Creating new text field bean");
                textFieldBean = new TextFieldBean();
                if (maxlength != 0) textFieldBean.setMaxLength(maxlength);
                if (size != 0) textFieldBean.setSize(size);
                this.setBaseComponentBean(textFieldBean);
            } else {
                //log.debug("Using existing text field bean");
                if (maxlength != 0) textFieldBean.setMaxLength(maxlength);
                if (size != 0) textFieldBean.setSize(size);
                this.updateBaseComponentBean(textFieldBean);
            }
        } else {
            textFieldBean = new TextFieldBean();
            if (maxlength != 0) textFieldBean.setMaxLength(maxlength);
            if (size != 0) textFieldBean.setSize(size);
            this.setBaseComponentBean(textFieldBean);
        }

        //debug();
        Tag parent = getParent();
        if (parent instanceof DataGridColumnTag) {
            DataGridColumnTag dataGridColumnTag = (DataGridColumnTag) parent;
            textFieldBean.setBeanIdSource(this.beanIdSource);
            dataGridColumnTag.addTagBean(this.textFieldBean);
        } else {

            try {
                JspWriter out = pageContext.getOut();
                out.print(textFieldBean.toStartString());
            } catch (Exception e) {
                throw new JspException(e.getMessage());
            }
        }
        return SKIP_BODY;
    }

}
