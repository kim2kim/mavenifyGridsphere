/*
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @author <a href="mailto:oliver@wehrens.de">Oliver Wehrens</a>
 * @version $Id$
 */
package org.gridsphere.services.core.user.impl;

import org.gridsphere.services.core.user.User;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * The <code>UserImpl</code> implements the <code>User</code> interface
 * and is an abstract view on the user-specific data.
 * Apart from a set of pre-defined, fixed set of attributes,
 * the interface gives access to user profile data.
 */
public class UserImpl implements User {

    private String oid = null;
    // store used to maintain user attributes
    private Map<String, String> attributes = new HashMap<String, String>();

    private String UserID = "";
    private String LastName = "";
    private String FullName = "";
    private String FirstName = "";
    private String EmailAddress = "";
    private String Organization = "";
    private Long LastLoginTime = null;
    private Integer numLogins = 0;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * Returns the internal unique user id.
     *
     * @return the internal unique id
     */
    public String getID() {
        return getOid();
    }

    /**
     * Sets the internal unique user id.
     *
     * @param id the internal unique id
     */
    public void setID(String id) {
        setOid(id);
    }

    /**
     * Returns the user id of the user, or null if the user id is not available.
     *
     * @return the user id
     */
    public String getUserID() {
        return UserID;
    }

    /**
     * Sets the user id of the user, or null if the user id is not available.
     *
     * @param userID the user id
     */
    public void setUserID(String userID) {
        this.UserID = userID;
    }

    /**
     * This is alias for the getUserID method, which for all intensive
     * purposes represents the name required for this user to login.
     *
     * @return String the user id
     */
    public String getUserName() {
        return this.UserID;
    }

    /**
     * This is an alias for the setUserID method, which for all intensive
     * purposes represents the name required for this user to login.
     *
     * @param name the user id
     */
    public void setUserName(String name) {
        this.UserID = name;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name
     */
    public String getLastName() {
        return LastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    /**
     * Returns the full name of the user, or null if the full name is not available.
     * The full name contains given names, family names and possibly a title or suffix.
     * Therefore, the full name may be different from the concatenation of given and family name.
     *
     * @return the full name
     */
    public String getFullName() {
        return FullName;
    }

    /**
     * Sets the full name of the user, or null if the full name is not available.
     * The full name contains given names, family names and possibly a title or suffix.
     * Therefore, the full name may be different from the concatenation of given and family name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    /**
     * Returns the first name of the user
     *
     * @return the first name
     */
    public String getFirstName() {
        return FirstName;
    }

    /**
     * Sets the first) name of the user, or  if the given name is not available.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    /**
     * Returns the given e-mail of the user or null if none is available.
     *
     * @return the email address
     */
    public String getEmailAddress() {
        return EmailAddress;
    }

    /**
     * Sets the given e-mail of the user.
     *
     * @param emailAddress the email address
     */
    public void setEmailAddress(String emailAddress) {
        this.EmailAddress = emailAddress;
    }

    /**
     * Gets the organization the user belongs to
     *
     * @return organization the organization
     */
    public String getOrganization() {
        return Organization;
    }

    /**
     * Sets the organization the user belongs to
     *
     * @param organization the organization name
     */
    public void setOrganization(String organization) {
        this.Organization = organization;
    }

    /**
     * Returns the point of time that this user was last logged in, or
     * <code>null</code> if this information is not available. The time is
     * returned in number of milliseconds since January 1, 1970 GMT.
     *
     * @return the last login time
     */
    public Long getLastLoginTime() {
        return LastLoginTime;
    }

    /**
     * Sets the point of time that this user was last logged in, or
     * <code>null</code> if this information is not available.
     * The time is returned in number of milliseconds since January 1, 1970 GMT.
     *
     * @param lastLoginTime the last login time
     */
    public void setLastLoginTime(Long lastLoginTime) {
        this.LastLoginTime = lastLoginTime;
    }


    public Map getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }


    /**
     * Returns the value of the attribute with the given name,s
     * or null if no attribute with the given name exists.
     *
     * @param name the attribute name
     * @return the attribute value
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Sets the value of the attribute with the given name,
     *
     * @param name  the attribute name
     * @param value the attribute value
     */
    public void setAttribute(String name, String value) {
        attributes.put(name, value);
    }

    /**
     * Returns an enumeration of names of all attributes available to this request.
     * This method returns an empty enumeration if the request has no attributes available to it.
     *
     * @return an enumeration of attribute names
     */
    public Enumeration getAttributeNames() {
        return new Hashtable<String, String>(attributes).keys();
    }

    /**
     * Returns an enumeration of names of all attributes available to this request.
     * This method returns an empty enumeration if the request has no attributes available to it.
     *
     * @return an enumeration of attribute names
     */
    public Enumeration getAttributeValues() {
        return new Hashtable<String, String>(attributes).elements();
    }

    /**
     * Returns the number of login occurences for this user
     *
     * @return the number of login occurences
     */
    public Integer getNumLogins() {
        return numLogins;
    }

    /**
     * Sets the number of logins occurences for this user
     *
     * @param numLogins the last login time
     */
    public void setNumLogins(Integer numLogins) {
        this.numLogins = numLogins;
    }

    /**
     * Returns a string representaation of the User
     *
     * @return User information represented as a String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Last Name: ").append(LastName).append("\n");
        sb.append("Full Name: ").append(FullName).append("\n");
        sb.append("First Name: ").append(FirstName).append("\n");
        sb.append("Email Address: ").append(EmailAddress).append("\n");
        sb.append("Id: ").append(getOid()).append("\n");
        sb.append("UserID: ").append(UserID).append("\n");
        sb.append("LastLoginTime: ").append(LastLoginTime).append("\n");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        boolean b = true;
        if ((obj != null) && (obj.getClass().equals(this.getClass()))) {
            b = (((UserImpl) obj).EmailAddress == this.EmailAddress);
            b &= (((UserImpl) obj).LastName == this.LastName);
            b &= (((UserImpl) obj).FirstName == this.FirstName);
            b &= (((UserImpl) obj).FullName == this.FullName);
            b &= (((UserImpl) obj).FullName == this.FullName);
            b &= (((UserImpl) obj).UserID == this.UserID);
            b &= (((UserImpl) obj).LastLoginTime == this.LastLoginTime);
            b &= (((UserImpl) obj).Organization == this.Organization);
        }
        return b;
    }

    public int hashCode() {
        return UserID.hashCode();
    }

}
