<%@ page import="javax.portlet.PortletSession" %>
<%@ taglib uri="/portletUI" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:defineObjects/>

<ui:form>

    <ui:messagebox beanId="msg"/>

    <ui:frame>

        <ui:tablerow>
            <ui:tablecell width="200">
                <ui:text key="USERNAME"/>
            </ui:tablecell>
            <ui:tablecell>
                <ui:textfield size="15" beanId="userName"/>
            </ui:tablecell>
        </ui:tablerow>


        <ui:tablerow>
            <ui:tablecell width="200">
                <ui:text key="GIVENNAME"/>
            </ui:tablecell>
            <ui:tablecell>
                <ui:textfield size="15" beanId="firstName"/>
            </ui:tablecell>
        </ui:tablerow>

        <ui:tablerow>
            <ui:tablecell width="200">
                <ui:text key="FAMILYNAME"/>
            </ui:tablecell>
            <ui:tablecell>
                <ui:textfield size="15" beanId="lastName"/>
            </ui:tablecell>
        </ui:tablerow>

        <ui:tablerow>
            <ui:tablecell width="200">
                <ui:text key="EMAILADDRESS"/>
            </ui:tablecell>
            <ui:tablecell>
                <ui:textfield size="15" beanId="emailAddress"/>
            </ui:tablecell>
        </ui:tablerow>

        <ui:tablerow>
            <ui:tablecell width="200">
                <ui:text key="ORGANIZATION"/>
            </ui:tablecell>
            <ui:tablecell>
                <ui:textfield size="15" beanId="organization"/>
            </ui:tablecell>
        </ui:tablerow>

        <% if (request.getAttribute("savePass") != null) { %>

        <ui:tablerow>
            <ui:tablecell width="200">
                <ui:text key="PASSWORD"/>
            </ui:tablecell>
            <ui:tablecell>
                <ui:password size="15" beanId="password"/>
            </ui:tablecell>
        </ui:tablerow>

        <ui:tablerow>
            <ui:tablecell width="200">
                <ui:text key="CONFIRM_PASS"/>
            </ui:tablecell>
            <ui:tablecell>
                <ui:password size="15" beanId="confirmPassword"/>
            </ui:tablecell>
        </ui:tablerow>
        <% } %>

    </ui:frame>

    <%
        PortletSession ps = renderRequest.getPortletSession(false);
        String useCaptcha = (String) ps.getAttribute("usecaptcha");
        if (useCaptcha.equals(Boolean.TRUE.toString())) {
    %>

    <ui:image src="<%= request.getContextPath() + \"/Captcha.jpg\" %>"/>
    <ui:actionsubmit key="LOGIN_CANT_READ" action="cantRead"/>
    <p/>

    <ui:text key="CAPTCHA_TEXT"/>
    <p/>
    <ui:textfield size="10" beanId="captchaTF"/>
    <p/>
    <%
        }
    %>
    <ui:frame>
        <ui:tablerow>
            <ui:tablecell>
                <ui:actionsubmit action="doSaveAccount" key="SAVE"/>
            </ui:tablecell>
        </ui:tablerow>
    </ui:frame>

</ui:form>

