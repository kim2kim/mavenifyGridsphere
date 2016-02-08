/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */

package org.gridsphere.provider.portletui.beans;

/**
 * The <code>TextAreaBean</code> represents a text area element
 */
public class TextAreaBean extends InputBean implements TagBean {

    private int cols = 0;
    private int rows = 0;

    /**
     * Constructs a default text area bean
     */
    public TextAreaBean() {
        super(TagBean.TEXTAREA_NAME);
        this.cssClass = MessageStyle.MSG_INFO;
    }

    /**
     * Constructs a text area bean from a supplied portlet request and bean identifier
     *
     * @param beanId the bean identifier
     */
    public TextAreaBean(String beanId) {
        super(TagBean.TEXTAREA_NAME);
        this.cssClass = MessageStyle.MSG_INFO;
        this.beanId = beanId;
    }

    /**
     * Gets the number of columns of the TextArea.
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Sets the number of columns of the TextArea.
     *
     * @param cols number of cols
     */
    public void setCols(int cols) {
        this.cols = cols;
    }

    /**
     * Return the number of rows of the textarea.
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows of the textarea.
     *
     * @param rows number of rows
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    public String toStartString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<textarea ");
        sb.append(getFormattedCss());
        if (id != null) sb.append(" id=\"").append(id).append("\"");
        String sname = createTagName(name);

        sb.append("name=\"").append(sname).append("\" ");
        if (cols != 0) sb.append(" cols=\"").append(cols).append("\" ");
        if (rows != 0) sb.append(" rows=\"").append(rows).append("\" ");
        sb.append(" ").append(checkDisabled());
        sb.append(" ").append(checkReadOnly());
        if (onFocus != null) sb.append("onfocus=\"").append(onFocus).append("\"");
        if (onClick != null) sb.append("onclick=\"").append(onClick).append("\"");
        if (onChange != null) sb.append("onchange=\"").append(onChange).append("\"");
        if (onBlur != null) sb.append("onblur=\"").append(onBlur).append("\"");
        if (onSelect != null) sb.append("onselect=\"").append(onSelect).append("\"");

        sb.append(">");
        return sb.toString();
    }

    public String toEndString() {
        String result = (value != null) ? value : "";
        return result + "</textarea>";
    }

}
