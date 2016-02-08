/**
 * Copyright 2003 IBM Corporation and Sun Microsystems, Inc.
 * All rights reserved.
 * Use is subject to license terms.
 */

package javax.portlet;


/**
 * The <CODE>PortletSession</CODE> interface provides a way to identify a user
 * across more than one request and to store transient information about that user.
 * <p/>
 * A <code>PortletSession</code> is created per user client per portlet application.
 * <p/>
 * A portlet can bind an object attribute into a <code>PortletSession</code> by name.
 * The <code>PortletSession</code> interface defines two scopes for storing objects:
 * <ul>
 * <li><code>APPLICATION_SCOPE</code>
 * <li><code>PORTLET_SCOPE</code>
 * </ul>
 * All objects stored in the session using the <code>APPLICATION_SCOPE</code>
 * must be available to all the portlets, servlets and
 * JSPs that belongs to the same portlet application and that handles a
 * request identified as being a part of the same session.
 * Objects stored in the session using the <code>PORTLET_SCOPE</code> must be
 * available to the portlet during requests for the same portlet window
 * that the objects where stored from. Attributes stored in the
 * <code>PORTLET_SCOPE</code> are not protected from other web components
 * of the portlet application. They are just conveniently namespaced.
 * <P>
 * The portlet session is based on the <code>HttpSession</code>. Therefore all
 * <code>HttpSession</code> listeners do apply to the portlet session and
 * attributes set in the portlet session are visible in the <code>HttpSession</code>
 * and vice versa.
 */
public interface PortletSession {

    /**
     * This constant defines an application wide scope for the session attribute.
     * <code>APPLICATION_SCOPE</code> session attributes enable Portlets
     * within one portlet application to share data.
     * <p/>
     * Portlets may need to prefix attributes set in this scope with some
     * ID, to avoid overwriting each other's attributes in the
     * case where two portlets of the same portlet definition
     * are created.
     * <p/>
     * Value: <code>0x01</code>
     */
    public static final int APPLICATION_SCOPE = 0x01;

    /**
     * This constant defines the scope of the session attribute to be
     * private to the portlet and its included resources.
     * <p/>
     * Value: <code>0x02</code>
     */
    public static final int PORTLET_SCOPE = 0x02;


    /**
     * Returns the object bound with the specified name in this session
     * under the <code>PORTLET_SCOPE</code>, or <code>null</code> if no
     * object is bound under the name in that scope.
     *
     * @param name a string specifying the name of the object
     * @throws java.lang.IllegalStateException
     *          if this method is called on an
     *          invalidated session.
     * @throws java.lang.IllegalArgumentException
     *          if name is <code>null</code>.
     * @return			the object with the specified name for
     * the <code>PORTLET_SCOPE</code>.
     */

    public java.lang.Object getAttribute(java.lang.String name);


    /**
     * Returns the object bound with the specified name in this session,
     * or <code>null</code> if no object is bound under the name in the given scope.
     *
     * @param name  a string specifying the name of the object
     * @param scope session scope of this attribute
     * @throws java.lang.IllegalStateException
     *          if this method is called on an
     *          invalidated session
     * @throws java.lang.IllegalArgumentException
     *          if name is <code>null</code>.
     * @return			the object with the specified name
     */

    public java.lang.Object getAttribute(java.lang.String name, int scope);


    /**
     * Returns an <code>Enumeration</code> of String objects containing the names of
     * all the objects bound to this session under the <code>PORTLET_SCOPE</code>, or an
     * empty <code>Enumeration</code> if no attributes are available.
     *
     * @throws java.lang.IllegalStateException
     *          if this method is called on an
     *          invalidated session
     * @return			an <code>Enumeration</code> of
     * <code>String</code> objects specifying the
     * names of all the objects bound to
     * this session, or an empty <code>Enumeration</code>
     * if no attributes are available.
     */

    public java.util.Enumeration getAttributeNames();


    /**
     * Returns an <code>Enumeration</code> of String objects containing the names of
     * all the objects bound to this session in the given scope, or an
     * empty <code>Enumeration</code> if no attributes are available in the
     * given scope.
     *
     * @param scope session scope of the attribute names
     * @throws java.lang.IllegalStateException
     *          if this method is called on an
     *          invalidated session
     * @return			an <code>Enumeration</code> of
     * <code>String</code> objects specifying the
     * names of all the objects bound to
     * this session, or an empty <code>Enumeration</code>
     * if no attributes are available in the given scope.
     */

    public java.util.Enumeration getAttributeNames(int scope);

    /**
     * Returns the time when this session was created, measured in
     * milliseconds since midnight January 1, 1970 GMT.
     *
     * @throws java.lang.IllegalStateException
     *          if this method is called on an
     *          invalidated session
     * @return				a <code>long</code> specifying
     * when this session was created,
     * expressed in
     * milliseconds since 1/1/1970 GMT
     */

    public long getCreationTime();


    /**
     * Returns a string containing the unique identifier assigned to this session.
     *
     * @return				a string specifying the identifier
     * assigned to this session
     */

    public java.lang.String getId();


    /**
     * Returns the last time the client sent a request associated with this session,
     * as the number of milliseconds since midnight January 1, 1970 GMT.
     * <p/>
     * <p>Actions that your portlet takes, such as getting or setting
     * a value associated with the session, do not affect the access
     * time.
     *
     * @return				a <code>long</code>
     * representing the last time
     * the client sent a request associated
     * with this session, expressed in
     * milliseconds since 1/1/1970 GMT
     */

    public long getLastAccessedTime();


    /**
     * Returns the maximum time interval, in seconds, for which the portlet container
     * keeps this session open between client accesses. After this interval,
     * the portlet container invalidates the session.  The maximum time
     * interval can be set
     * with the <code>setMaxInactiveInterval</code> method.
     * A negative time indicates the session should never timeout.
     *
     * @return		an integer specifying the number of
     * seconds this session remains open
     * between client requests
     * @see		#setMaxInactiveInterval
     */

    public int getMaxInactiveInterval();


    /**
     * Invalidates this session (all scopes) and unbinds any objects bound to it.
     * <p/>
     * Invalidating the portlet session will result in invalidating the underlying
     * <code>HttpSession</code>
     *
     * @throws java.lang.IllegalStateException
     *          if this method is called on a
     *          session which has already been invalidated
     */

    public void invalidate();


    /**
     * Returns true if the client does not yet know about the session or
     * if the client chooses not to join the session.
     *
     * @return <code>true</code> if the
     *         server has created a session,
     *         but the client has not joined yet.
     * @throws java.lang.IllegalStateException
     *          if this method is called on a
     *          session which has already been invalidated
     */

    public boolean isNew();


    /**
     * Removes the object bound with the specified name under
     * the <code>PORTLET_SCOPE</code> from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     * @param name the name of the object to be
     *             removed from this session in the
     *             <code> PORTLET_SCOPE</code>.
     * @throws java.lang.IllegalStateException
     *          if this method is called on a
     *          session which has been invalidated
     * @throws java.lang.IllegalArgumentException
     *          if name is <code>null</code>.
     */

    public void removeAttribute(String name);


    /**
     * Removes the object bound with the specified name and the given scope from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     * @param name  the name of the object to be
     *              removed from this session
     * @param scope session scope of this attribute
     * @throws java.lang.IllegalStateException
     *          if this method is called on a
     *          session which has been invalidated
     * @throws java.lang.IllegalArgumentException
     *          if name is <code>null</code>.
     */

    public void removeAttribute(String name, int scope);


    /**
     * Binds an object to this session under the <code>PORTLET_SCOPE</code>, using the name specified.
     * If an object of the same name in this scope is already bound to the session,
     * that object is replaced.
     * <p/>
     * <p>After this method has been executed, and if the new object
     * implements <code>HttpSessionBindingListener</code>,
     * the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListeners</code> in the web
     * application.
     * <p>If an object was already bound to this session
     * that implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     * <p/>
     * <p>If the value is <code>null</code>, this has the same effect as calling
     * <code>removeAttribute()</code>.
     *
     * @param name  the name to which the object is bound under
     *              the <code>PORTLET_SCOPE</code>;
     *              this cannot be <code>null</code>.
     * @param value the object to be bound
     * @throws java.lang.IllegalStateException
     *          if this method is called on a
     *          session which has been invalidated
     * @throws java.lang.IllegalArgumentException
     *          if name is <code>null</code>.
     */

    public void setAttribute(java.lang.String name, java.lang.Object value);


    /**
     * Binds an object to this session in the given scope, using the name specified.
     * If an object of the same name in this scope is already bound to the session,
     * that object is replaced.
     * <p/>
     * <p>After this method has been executed, and if the new object
     * implements <code>HttpSessionBindingListener</code>,
     * the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListeners</code> in the web
     * application.
     * <p>If an object was already bound to this session
     * that implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     * <p/>
     * <p>If the value is <code>null</code>, this has the same effect as calling
     * <code>removeAttribute()</code>.
     *
     * @param name  the name to which the object is bound;
     *              this cannot be <code>null</code>.
     * @param value the object to be bound
     * @param scope session scope of this attribute
     * @throws java.lang.IllegalStateException
     *          if this method is called on a
     *          session which has been invalidated
     * @throws java.lang.IllegalArgumentException
     *          if name is <code>null</code>.
     */

    public void setAttribute(java.lang.String name, java.lang.Object value, int scope);


    /**
     * Specifies the time, in seconds, between client requests, before the
     * portlet container invalidates this session. A negative time
     * indicates the session should never timeout.
     *
     * @param interval An integer specifying the number
     *                 of seconds
     */

    public void setMaxInactiveInterval(int interval);


    /**
     * Returns the portlet application context associated with this session.
     *
     * @return the portlet application context
     */

    public PortletContext getPortletContext();

}

