<%@ page import="org.gridsphere.portlet.impl.SportletProperties" %>
<%@ taglib uri="/portletUI" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:defineObjects/>

<ui:actionlink cssStyle="font-size: 10px; text-decoration: underline;" action="<%= SportletProperties.LOGOUT %>" key="LOGOUT"/>


