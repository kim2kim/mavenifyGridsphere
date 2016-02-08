package org.gridsphere.servlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridsphere.layout.PortletLayoutEngine;
import org.gridsphere.portlet.impl.PortletContextImpl;
import org.gridsphere.portlet.impl.SportletProperties;
import org.gridsphere.portlet.service.spi.PortletServiceFactory;
import org.gridsphere.portletcontainer.GridSphereEvent;
import org.gridsphere.portletcontainer.impl.GridSphereEventImpl;
import org.gridsphere.services.core.customization.SettingsService;
import org.gridsphere.services.core.persistence.PersistenceManagerRdbms;
import org.gridsphere.services.core.persistence.PersistenceManagerService;
import org.gridsphere.services.core.persistence.impl.CreateDatabase;
import org.gridsphere.services.core.portal.PortalConfigService;
import org.gridsphere.services.core.security.password.PasswordEditor;
import org.gridsphere.services.core.security.password.PasswordManagerService;
import org.gridsphere.services.core.security.role.PortletRole;
import org.gridsphere.services.core.security.role.RoleManagerService;
import org.gridsphere.services.core.user.User;
import org.gridsphere.services.core.user.UserManagerService;
import org.gridsphere.services.core.setup.PortletsSetupModuleService;
import org.gridsphere.services.core.setup.modules.impl.descriptor.PortletsSetupModuleStateDescriptor;
import org.hibernate.StaleObjectStateException;

import javax.portlet.PortletContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
public class SetupServlet extends HttpServlet {

    private Log log = LogFactory.getLog(SetupServlet.class);
    private PortletLayoutEngine layoutEngine = PortletLayoutEngine.getInstance();
    private RoleManagerService roleService = null;
    private UserManagerService userManagerService = null;
    private PasswordManagerService passwordService = null;
    private PortalConfigService portalConfigService = null;
    private PortletsSetupModuleService portletsSetupModuleService = null;
    private SettingsService settingsService = null;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        settingsService = (SettingsService) PortletServiceFactory.createPortletService(SettingsService.class, true);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PortletContext ctx = new PortletContextImpl(getServletContext());
        GridSphereEventImpl event = new GridSphereEventImpl(ctx, req, res);

        if (req.getAttribute("setup") == null) {
            redirect(event);
            return;
        }

        String error = (String) req.getSession(true).getAttribute("error");
        if (error != null) {
            req.setAttribute("error", error);
            req.getSession().removeAttribute("error");
        }
        // check current GS release and the DB meta file
        String release = SportletProperties.getInstance().getProperty("gridsphere.release");
        int idx = release.lastIndexOf(" ");
        String gsversion = release.substring(idx + 1);
        //System.err.println("gsversion=" + gsversion);

        String dbpath = settingsService.getRealSettingsPath("database");

        File dbdir = new File(dbpath);
        String[] filenames = dbdir.list();
        String currentVersion = null;
        for (int i = 0; i < filenames.length; i++) {
            if (filenames[i].startsWith("GS")) currentVersion = filenames[i];
        }

        setupRoles(event);

        File thisdbfile = new File(dbpath + File.separator + "GS_" + gsversion);

        // if meta file exists, redirect to the portal unless admin needs to be created
        if (thisdbfile.exists()) {
            roleService = (RoleManagerService) PortletServiceFactory.createPortletService(RoleManagerService.class, true);
            userManagerService = (UserManagerService) PortletServiceFactory.createPortletService(UserManagerService.class, true);
            passwordService = (PasswordManagerService) PortletServiceFactory.createPortletService(PasswordManagerService.class, true);
            portalConfigService = (PortalConfigService) PortletServiceFactory.createPortletService(PortalConfigService.class, true);
            portletsSetupModuleService = (PortletsSetupModuleService) PortletServiceFactory.createPortletService(PortletsSetupModuleService.class, true);

            PersistenceManagerService pms = null;

            pms = (PersistenceManagerService) PortletServiceFactory.createPortletService(PersistenceManagerService.class, true);
            List admins = null;
            PersistenceManagerRdbms pm = null;
            try {
                log.info("Starting a database transaction");

                pm = pms.createGridSphereRdbms();
                pm.beginTransaction();

                admins = roleService.getUsersInRole(PortletRole.ADMIN);


                log.info("Committing the database transaction");

                pm.endTransaction();
            } catch (StaleObjectStateException staleEx) {
                log.error("This interceptor does not implement optimistic concurrency control!");
                log.error("Your application will not work until you add compensation actions!");

            } catch (Throwable ex) {
                ex.printStackTrace();
                pm.endTransaction();
                try {
                    pm.rollbackTransaction();
                } catch (Throwable rbEx) {
                    log.error("Could not rollback transaction after exception!", rbEx);
                }
            }


            if (admins.isEmpty()) {
                req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupAdmin");
            } else if (!portletsSetupModuleService.isPreInitSetupDone()) {
                try {
                    PortletsSetupModuleStateDescriptor portletsSetupModuleStateDescriptor = portletsSetupModuleService.getModuleStateDescriptor(req);
                    setPreInitSetupAttributes(req, portletsSetupModuleStateDescriptor);
                } catch (IllegalStateException e) {
                    portletsSetupModuleService.skipPreInitSetup();
                    log.error("Could not process setup module", e);
                } catch (NullPointerException e){
                    log.error("Could not process setup module", e);
                    throw new IllegalStateException("NullPointerException in portlets setup module (it might be caused by wrong value of portlet-name in the setup module descriptor).",e);
                }
            } else if (!portletsSetupModuleService.isPostInitSetupDone()) {
                try {
                    PortletsSetupModuleStateDescriptor portletsSetupModuleStateDescriptor = portletsSetupModuleService.getModuleStateDescriptor(req);
                    setPostInitSetupAttributes(req, portletsSetupModuleStateDescriptor);
                } catch (IllegalStateException e) {
                    portletsSetupModuleService.skipPostInitSetup();
                    log.error("Could not process setup module", e);
                } catch (NullPointerException e){
                    log.error("Could not process setup module", e);
                    throw new IllegalStateException("NullPointerException in portlets setup module (it might be caused by wrong value of portlet-name in the setup module descriptor).",e);
                }
            } else {
                redirect(event);
                return;
            }

        } else {

            // do a databse update since an old version exists
            if (currentVersion != null) {
                req.setAttribute(SportletProperties.LAYOUT_PAGE, "UpdateDatabase");
            } else {
                req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupDatabase");
            }
        }

        layoutEngine.actionPerformed(event);

        layoutEngine.service(event);

    }


    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        PortletContext ctx = new PortletContextImpl(getServletContext());
        GridSphereEventImpl event = new GridSphereEventImpl(ctx, req, res);

        req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupDatabase");
        try {
            String installType = req.getParameter("install");
            if (installType != null) {
                if (installType.equals("default")) {
                    createDefaultDatabase();
                    makeDatabase();
                    createDatabaseFile();
                    req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupAdmin");
                }
                if (installType.equals("custom")) {
                    createExternalDatabase(event);
                    makeDatabase();
                    createDatabaseFile();
                    req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupAdmin");
                }
                if (installType.equals("update")) {
                    req.setAttribute(SportletProperties.LAYOUT_PAGE, "UpdateDatabase");
                    updateDatabase();
                    removeOldDatabaseFile();
                    createDatabaseFile();
                }
                if (installType.equals("admin")) {
                    req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupAdmin");
                    createAdmin(event);
                }
                if (installType.equals("portlet")) {
                    String setupType = req.getParameter(SportletProperties.PORTLET_SETUP_TYPE);
                    String setupOperation = getSetupPortletOperation(req);
                    if (setupType.equals(SportletProperties.PORTLET_SETUP_TYPE_PRE)) {
                        if (setupOperation.toLowerCase().equals("skip_module")) {
                            portletsSetupModuleService.skipModule();
                        } else if (setupOperation.toLowerCase().equals("skip_pre_or_post_setup")) {
                            portletsSetupModuleService.skipPreInitSetup();
                        } else {
                            try {
                                portletsSetupModuleService.invokePreInit(req);
                            } catch (IllegalStateException e) {
                                portletsSetupModuleService.skipModule();
                                log.error("Could not process setup module", e);
                            }
                        }
                    } else if (setupType.equals(SportletProperties.PORTLET_SETUP_TYPE_POST)) {
                        if (setupOperation.toLowerCase().equals("skip_module")) {
                            portletsSetupModuleService.skipModule();
                        } else if (setupOperation.toLowerCase().equals("skip_pre_or_post_setup")) {
                            portletsSetupModuleService.skipPostInitSetup();
                        } else {
                            try {
                                portletsSetupModuleService.invokePostInit(req);
                            } catch (IllegalStateException e) {
                                portletsSetupModuleService.skipModule();
                                log.error("Could not process setup module", e);
                            }
                        }
                    }
                }
            }

        } catch (IllegalArgumentException e) {
            req.getSession(true).setAttribute("error", getLocalizedText(e.getMessage(),req));
        }
        redirect(event);
    }

    protected String getLocalizedText(String key, HttpServletRequest request) {
        Locale locale = request.getLocale();
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Portlet", locale);
            return bundle.getString(key);
        } catch (Exception e) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle("Portlet", Locale.ENGLISH);
                return bundle.getString(key);
            } catch (Exception ex) {
                return key;
            }
        }
    }

    private String getSetupPortletOperation(HttpServletRequest request){
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            if(parameterName.startsWith(SportletProperties.PORTLET_SETUP_OPERATION+"="))
                return parameterName.substring((SportletProperties.PORTLET_SETUP_OPERATION+"=").length()).toLowerCase();
        }
        return "";
    }

    private void setPreInitSetupAttributes(HttpServletRequest req, PortletsSetupModuleStateDescriptor portletsSetupModuleStateDescriptor) {
        req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupPortlet");
        req.setAttribute(SportletProperties.PORTLET_SETUP_TYPE, SportletProperties.PORTLET_SETUP_TYPE_PRE);

        req.setAttribute(SportletProperties.PORTLET_SETUP_PAGE_INCLUDE, portletsSetupModuleStateDescriptor.getJspFile());
        req.setAttribute(SportletProperties.PORTLET_SETUP_PAGE_CONTEXT, portletsSetupModuleStateDescriptor.getContext());
        req.setAttribute(SportletProperties.PORTLET_SETUP_TITLE, portletsSetupModuleStateDescriptor.getTitle());
        req.setAttribute(SportletProperties.PORTLET_SETUP_DESCRIPTION, portletsSetupModuleStateDescriptor.getDescription());
        req.setAttribute(SportletProperties.PORTLET_SETUP_MODULE_NUMBER, portletsSetupModuleStateDescriptor.getModuleNumber());
        req.setAttribute(SportletProperties.PORTLET_SETUP_NUMBER_OF_MODULES, portletsSetupModuleStateDescriptor.getNumberOfModules());
        Map<String, Object> attributes = portletsSetupModuleStateDescriptor.getAttributes();
        if (null != attributes) {
            Iterator<String> attributeKeyIterator = attributes.keySet().iterator();
            while (attributeKeyIterator.hasNext()) {
                String attributeKey = attributeKeyIterator.next();
                req.setAttribute(attributeKey, attributes.get(attributeKey));
            }
        }
    }

    private void setPostInitSetupAttributes(HttpServletRequest req, PortletsSetupModuleStateDescriptor portletsSetupModuleStateDescriptor) {
        req.setAttribute(SportletProperties.LAYOUT_PAGE, "SetupPortlet");
        req.setAttribute(SportletProperties.PORTLET_SETUP_TYPE, SportletProperties.PORTLET_SETUP_TYPE_POST);

        req.setAttribute(SportletProperties.PORTLET_SETUP_PAGE_INCLUDE, portletsSetupModuleStateDescriptor.getJspFile());
        req.setAttribute(SportletProperties.PORTLET_SETUP_PAGE_CONTEXT, portletsSetupModuleStateDescriptor.getContext());
        req.setAttribute(SportletProperties.PORTLET_SETUP_TITLE, portletsSetupModuleStateDescriptor.getTitle());
        req.setAttribute(SportletProperties.PORTLET_SETUP_DESCRIPTION, portletsSetupModuleStateDescriptor.getDescription());
        req.setAttribute(SportletProperties.PORTLET_SETUP_MODULE_NUMBER, portletsSetupModuleStateDescriptor.getModuleNumber());
        req.setAttribute(SportletProperties.PORTLET_SETUP_NUMBER_OF_MODULES, portletsSetupModuleStateDescriptor.getNumberOfModules());
        Map<String, Object> attributes = portletsSetupModuleStateDescriptor.getAttributes();
        if (null != attributes) {
            Iterator<String> attributeKeyIterator = attributes.keySet().iterator();
            while (attributeKeyIterator.hasNext()) {
                String attributeKey = attributeKeyIterator.next();
                req.setAttribute(attributeKey, attributes.get(attributeKey));
            }
        }
    }

    private void createDefaultDatabase() {

        // read in the original from the WEB-INF dir
        InputStream hibInputStream = getServletContext().getResourceAsStream("/WEB-INF/CustomPortal/database/hibernate.properties");

        // write it to the new settings location
        String hibPath = settingsService.getRealSettingsPath("database/hibernate.properties");
        // String hibPath = settingsService.getSettingsPath()+File.separator+"database"+File.separator+"hibernate.properties";
        //String hibPath = getServletContext().getRealPath("/WEB-INF/CustomPortal/database/hibernate.properties");
        try {
            FileOutputStream hibOut = new FileOutputStream(hibPath);
            Properties hibProps = new Properties();
            String connURL = "jdbc:hsqldb:" + settingsService.getRealSettingsPath("database/gridsphere");
            log.debug("using connURL= " + connURL);
            hibProps.load(hibInputStream);
            hibProps.setProperty("hibernate.connection.url", connURL);
            hibProps.store(hibOut, "Hibernate Properties");
        } catch (IOException e) {
            log.error("Unable to load/save hibernate.properties", e);
            throw new IllegalArgumentException("SETUP_ERROR_UNABLE_TO_SAVE_HIBERNATE");
        }

    }

    private void createExternalDatabase(GridSphereEvent event) {
        HttpServletRequest req = event.getHttpServletRequest();
        // get the original path as default
        InputStream hibInputStream = getServletContext().getResourceAsStream("/WEB-INF/CustomPortal/database/hibernate.properties");
        String hibPath = settingsService.getRealSettingsPath("database/hibernate.properties");

        String dbtype = req.getParameter("dbtype");

        String connURL = req.getParameter("databaseURL");
        if ((connURL == null) || (connURL.equals("")))
            throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_DB_URL");
        String dialect = req.getParameter("dialect");
        if ((dialect == null) || (dialect.equals("")))
            throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_HIBERNATE_DIALECT");
        String driverClass = req.getParameter("driverClass");
        if ((driverClass == null) || (driverClass.equals("")))
            throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_DB_DRIVER_CLASS");

        String name = req.getParameter("username");
        if (name == null) name = "";
        String pass = req.getParameter("password");
        if (pass == null) pass = "";

        System.err.println("dbtype=" + dbtype);


        try {
            FileOutputStream hibOut = new FileOutputStream(hibPath);
            Properties hibProps = new Properties();

            System.err.println("driver class=" + driverClass);
            System.err.println("conn url=" + connURL);
            hibProps.load(hibInputStream);
            hibProps.setProperty("hibernate.dialect", dialect);
            hibProps.setProperty("hibernate.connection.username", name);
            hibProps.setProperty("hibernate.connection.password", pass);
            hibProps.setProperty("hibernate.connection.url", connURL);
            hibProps.setProperty("hibernate.connection.driver_class", driverClass);
            hibProps.store(hibOut, "Hibernate Properties");
            hibOut.close();
            hibInputStream.close();
        } catch (IOException e) {
            log.error("Unable to load/save hibernate.properties", e);
            throw new IllegalArgumentException("SETUP_ERROR_UNABLE_TO_SAVE_HIBERNATE");
        }
    }

    private void makeDatabase() {
        CreateDatabase dbtask = new CreateDatabase();
        dbtask.setAction("CREATE");
        // todo fix the dir
        // dbtask.setConfigDir(getServletContext().getRealPath(""));
        dbtask.setPersistenceMappingDir(getServletContext().getRealPath("WEB-INF/persistence"));
        try {
            dbtask.execute();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void updateDatabase() {
        CreateDatabase dbtask = new CreateDatabase();
        dbtask.setAction("UPDATE");
        // todo fix the dir 
        //dbtask.setConfigDir(getServletContext().getRealPath(""));
        dbtask.setPersistenceMappingDir(getServletContext().getRealPath("WEB-INF/persistence"));
        try {
            dbtask.execute();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void createDatabaseFile() {

        String release = SportletProperties.getInstance().getProperty("gridsphere.release");
        int idx = release.lastIndexOf(" ");
        String gsversion = release.substring(idx + 1);
        String dbpath = settingsService.getRealSettingsPath("database/GS_" + gsversion);
        try {
            File dbfile = new File(dbpath);
            dbfile.createNewFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to create file: " + dbpath, e);
        }
    }


    private void setupRoles(GridSphereEvent event) {

        // Retrieve user if there is one
        HttpServletRequest req = event.getHttpServletRequest();

        List roles = new ArrayList();
        roles.add("setup");

        // set user, role and groups in request

        req.setAttribute(SportletProperties.PORTLET_ROLE, roles);
    }

    private void removeOldDatabaseFile() {
        String dbpath = settingsService.getRealSettingsPath("database");
        File dbdir = new File(dbpath);
        String[] filenames = dbdir.list();
        String currentVersion = null;
        for (int i = 0; i < filenames.length; i++) {
            if (filenames[i].startsWith("GS")) currentVersion = filenames[i];
        }
        if (currentVersion != null) {
            File f = new File(currentVersion);
            f.delete();
        }
    }

    private void createAdmin(GridSphereEvent event) {
        HttpServletRequest req = event.getHttpServletRequest();
        String username = req.getParameter("username");
        String firstname = req.getParameter("firstname");
        String lastname = req.getParameter("lastname");
        String email = req.getParameter("email");
        String org = req.getParameter("organization");
        String passwd = req.getParameter("password");
        String passwd2 = req.getParameter("password2");

        if (username.equals("")) throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_USERNAME");
        if (firstname.equals("")) throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_FIRST_NAME");
        if (lastname.equals("")) throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_LAST_NAME");
        if (email.equals("")) throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_EMAIL");
        if (!email.contains("@") || (!email.contains(".")))
            throw new IllegalArgumentException("SETUP_ERROR_INVALID_EMAIL");
        if (!passwd.equals(passwd2)) throw new IllegalArgumentException("SETUP_ERROR_PASSWORD_MISMATCH");
        if (passwd.equals("")) throw new IllegalArgumentException("SETUP_ERROR_NO_VALUE_FOR_PASSWORD");


        PersistenceManagerService pms = (PersistenceManagerService) PortletServiceFactory.createPortletService(PersistenceManagerService.class, true);
        PersistenceManagerRdbms pm = pms.createGridSphereRdbms();

        try {
            log.debug("Starting a database transaction");
            pm.beginTransaction();


            User accountRequest = this.userManagerService.createUser();
            accountRequest.setUserName(username);
            accountRequest.setFirstName(firstname);
            accountRequest.setLastName(lastname);
            accountRequest.setFullName(lastname + ", " + firstname);
            accountRequest.setEmailAddress(email);
            accountRequest.setOrganization(org);
            PasswordEditor editor = passwordService.editPassword(accountRequest);
            editor.setValue(passwd);
            log.debug("Saving the admin account in the DB");
            portalConfigService.setProperty(PortalConfigService.PORTAL_ADMIN_EMAIL, accountRequest.getEmailAddress());
            passwordService.savePassword(editor);
            userManagerService.saveUser(accountRequest);

            roleService.addUserToRole(accountRequest, PortletRole.ADMIN);
            roleService.addUserToRole(accountRequest, PortletRole.USER);

            // Commit and cleanup
            log.debug("Committing the database transaction");
            pm.endTransaction();
        } catch (StaleObjectStateException staleEx) {
            log.error("This interceptor does not implement optimistic concurrency control!");
            log.error("Your application will not work until you add compensation actions!");
            // Rollback, close everything, possibly compensate for any permanent changes
            // during the conversation, and finally restart business conversation. Maybe
            // give the user of the application a chance to merge some of his work with
            // fresh data... what you do here depends on your applications design.
            throw staleEx;
        } catch (Throwable ex) {
            // Rollback only
            ex.printStackTrace();
            try {
                pm.rollbackTransaction();
            } catch (Throwable rbEx) {
                log.error("Could not rollback transaction after exception!", rbEx);
            }


        }
    }

    private void redirect(GridSphereEvent event) {
        HttpServletRequest req = event.getHttpServletRequest();
        HttpServletResponse res = event.getHttpServletResponse();
        PortalConfigService configService = (PortalConfigService) PortletServiceFactory.createPortletService(PortalConfigService.class, true);
        StringBuffer s = new StringBuffer();
        String port;
        if (req.isSecure()) {
            s.append("https://");
            port = configService.getProperty("gridsphere.port.https");
        } else {
            s.append("http://");
            port = configService.getProperty("gridsphere.port.http");
        }
        s.append(req.getServerName());
        s.append(":");
        s.append((!port.equals("")) ? port : String.valueOf(req.getServerPort()));
        String contextPath = "/" + configService.getProperty("gridsphere.deploy");
        String servletPath = "/" + configService.getProperty("gridsphere.context");
        if (contextPath.equals("/")) contextPath = "";
        String url = contextPath + servletPath;
        url = s.append(url).toString();
        try {
            res.sendRedirect(url.toString());
        } catch (IOException e) {
            log.error("Unable to redirect!", e);
        }
        log.debug("redirecting to " + url);
    }

}
