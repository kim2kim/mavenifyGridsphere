/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.provider.portletui.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.gridsphere.portlet.impl.PortletURLImpl;
import org.gridsphere.portlet.impl.SportletProperties;

/**
 * An <code>ActionLinkBean</code> is a visual bean that represents a hyperlink
 * containing a portlet action
 */
public class ActionLinkBean extends ActionBean implements TagBean {

	protected String style = "none";
	protected List<TagBean> beans = new ArrayList<TagBean>();

	/**
	 * Constructs a default action link bean
	 */
	public ActionLinkBean() {
	}

	/**
	 * Constructs an action link bean from a portlet request and supplied bean
	 * identifier
	 * 
	 * @param beanId
	 *            the bean id used to reference this ActionLinkBean
	 */
	public ActionLinkBean(String beanId) {
		this.beanId = beanId;
	}

	/**
	 * Returns the style of the text: Available styles are
	 * <ul>
	 * <li>nostyle - plain text</li>
	 * <li>error - error text</li>
	 * <li>info - default info text</li>
	 * <li>status - status text</li>
	 * <li>alert - alert text</li>
	 * <li>success - success text</li>
	 * 
	 * @return the text style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the style of the text: Available styles are
	 * <ul>
	 * <li>error</li>
	 * <li>info</li>
	 * <li>status</li>
	 * <li>alert</li>
	 * <li>success</li>
	 * 
	 * @param style
	 *            the text style
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Gets the beans to be rendered before the text in the ActionLink.
	 * 
	 * @return TagBean
	 */

	public List<TagBean> getTagBeans() {
		return beans;
	}

	/**
	 * Sets beans to be rendered before the text in the ActionLink.
	 * 
	 * @param beans
	 *            Bean to be rendered
	 */
	public void setTagBeans(List<TagBean> beans) {
		this.beans = beans;
	}

	public String toStartString() {
		return "";
	}

	public String toEndString() {
		// now do the string rendering
		if (portletURI == null) {

			PortletURL url = renderResponse.createActionURL();
			if (url instanceof PortletURLImpl) {
				((PortletURLImpl) url).setAction(action);
			} else {
				// this is code for non-GS containers
				url.setParameter(SportletProperties.DEFAULT_PORTLET_ACTION,	action);
			}
			String id = "gsal"; // a prefix for parameters
			url.setParameter(SportletProperties.PREFIX, id);
			if ((paramBeanList != null) && (paramBeanList.size() > 0)) {
				Iterator<ParamBean> it = paramBeanList.iterator();
				while (it.hasNext()) {
					ParamBean paramBean = it.next();
					url.setParameter(id + "_" + paramBean.getName(), paramBean.getValue());
				}
			}
			portletURI = url.toString();
		}
		action = portletURI.toString();

		// now do the string rendering
		action = this.portletURI.toString();

		if (anchor != null)
			action += "#" + anchor;

		// String hlink = "<a href=\"" + action + "\"" +
		// " onClick=\"this.href='" + action + "&amp;JavaScript=enabled'\"/>" +
		// value + "</a>";
		if (style.equalsIgnoreCase("error") || (style.equalsIgnoreCase("err"))) {
			this.cssClass = MessageStyle.MSG_ERROR;
		} else if (style.equalsIgnoreCase("status")) {
			this.cssClass = MessageStyle.MSG_STATUS;
		} else if (style.equalsIgnoreCase("info")) {
			this.cssClass = MessageStyle.MSG_INFO;
		} else if (style.equalsIgnoreCase("alert")) {
			this.cssClass = MessageStyle.MSG_ALERT;
		} else if (style.equalsIgnoreCase("success")) {
			this.cssClass = MessageStyle.MSG_SUCCESS;
		} else if (style.equalsIgnoreCase(MessageStyle.MSG_BOLD)) {
			this.addCssStyle("font-weight: bold;");
		} else if (style.equalsIgnoreCase(MessageStyle.MSG_ITALIC)) {
			this.addCssStyle("font-style: italic;");
		} else if (style.equalsIgnoreCase(MessageStyle.MSG_UNDERLINE)) {
			this.addCssStyle("text-decoration: underline;");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<a");
		if (name != null)
			sb.append(" name=\"").append(name).append("\"");
		if (id != null)
			sb.append(" id=\"").append(id).append("\" ");
		if (useAjax)
			action = "#";
		sb.append(" href=\"").append(action).append("\"");

		sb.append(getFormattedCss());
		if (onClick != null)
			sb.append(" onclick=\"").append(onClick).append("\"");
		if (onMouseOut != null)
			sb.append(" onMouseOut=\"").append(onMouseOut).append("\"");
		if (onMouseOver != null)
			sb.append(" onMouseOver=\"").append(onMouseOver).append("\"");
		sb.append(">");

		for (TagBean bean : beans) {
			sb.append(bean.toStartString());
			sb.append(bean.toEndString());
		}

		sb.append(value).append("</a>");
		return sb.toString();
	}

}