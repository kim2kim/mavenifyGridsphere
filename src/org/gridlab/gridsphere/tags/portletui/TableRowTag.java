package org.gridlab.gridsphere.tags.portletui;

import org.gridlab.gridsphere.portlet.PortletRequest;
import org.gridlab.gridsphere.provider.portletui.beans.TableRowBean;
import org.gridlab.gridsphere.provider.portletui.beans.TagBean;
import org.gridlab.gridsphere.provider.portletui.beans.BaseComponentBean;
import org.gridlab.gridsphere.provider.portletui.model.DefaultTableModel;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

/**
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id$
 */
public class TableRowTag extends ContainerTag {

    protected TableRowBean rowBean = null;
    protected boolean isHeader = false;

    public void setTableRowBean(TableRowBean tableRowBean) {
        rowBean = tableRowBean;
    }

    public TableRowBean getTableRowBean() {
        return rowBean;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public boolean getHeader() {
        return isHeader;
    }

    public int doStartTag() throws JspException {
        list = new Vector();
        if (!beanId.equals("")) {
            rowBean = (TableRowBean)pageContext.getAttribute(getBeanKey(), PageContext.REQUEST_SCOPE);
            if (rowBean == null) rowBean = new TableRowBean();
        } else {
            rowBean = new TableRowBean();
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        TableTag tableTag = (TableTag)getParent();
        if (tableTag != null) {
            rowBean.setHeader(isHeader);
            Iterator it = list.iterator();
            while (it.hasNext()) {
                BaseComponentBean bean = (BaseComponentBean)it.next();
                rowBean.addBean(bean);
            }

            DefaultTableModel tableModel = tableTag.getTableModel();
            tableModel.addTableRowBean(rowBean);
            tableTag.setTableModel(tableModel);
        }

        return EVAL_PAGE;
    }
}
