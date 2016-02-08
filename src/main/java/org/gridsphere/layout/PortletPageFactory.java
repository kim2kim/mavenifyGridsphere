package org.gridsphere.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridsphere.portlet.impl.SportletProperties;
import org.gridsphere.portlet.service.spi.PortletServiceFactory;
import org.gridsphere.portletcontainer.GridSphereEvent;
import org.gridsphere.portletcontainer.PortletSessionListener;
import org.gridsphere.portletcontainer.impl.PortletSessionManager;
import org.gridsphere.services.core.customization.SettingsService;
import org.gridsphere.services.core.portal.PortalConfigService;
import org.gridsphere.services.core.user.User;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
public class PortletPageFactory implements PortletSessionListener {

    private Log log = LogFactory.getLog(PortletPageFactory.class);

    public static final String TCK_PAGE = "TCK";
    //public static final String SETUP_PAGE = "SetupLayout";
    public static final String ERROR_PAGE = "error";
    public static final String GUEST_PAGE = "guest";

    // TODO these need refactoring later on
    public static final String TEMPLATE_PAGE = "TemplateLayout";
    public static final String USER_PAGE = "loggedin";

    private static String USER_LAYOUT_DIR = null;

    public static final String DEFAULT_THEME = "default";

    private static PortletPageFactory instance = null;
    private PortletSessionManager sessionManager = PortletSessionManager.getInstance();
    private PortalConfigService portalConfigService = null;

    protected URL LAYOUT_MAPPING_PATH = getClass().getResource("/config/mapping/layout-mapping.xml");

    // Store user layouts in a hash
    private static Map<String, PortletPage> userLayouts = new HashMap<String, PortletPage>();

    // a hash of hashes to contain all the users layouts
    private static Map<String, Map<String, PortletPage>> layouts = new HashMap<String, Map<String, PortletPage>>();

    // a hash of loaded master layouts used to make copies
    private static Map<String, PortletPage> masterLayouts = new HashMap<String, PortletPage>();
    private static Set<String> editableLayoutIds = new HashSet<String>();


    private ServletContext context;

    private PortletPageFactory() {

    }

    public void init(ServletContext ctx) {

        this.context = ctx;

        SettingsService settingsService = (SettingsService) PortletServiceFactory.createPortletService(SettingsService.class, true);

        USER_LAYOUT_DIR = settingsService.getRealSettingsPath("layouts/users");
        String layoutsDirPath = settingsService.getRealSettingsPath("layouts");

        File layoutsDir = new File(layoutsDirPath);
        File[] layoutFiles = layoutsDir.listFiles();
        PortletPage page = null;
        for (int i = 0; i < layoutFiles.length; i++) {
            File layoutFile = layoutFiles[i];
            String layoutFileName = layoutFile.getName();
            if (layoutFileName.endsWith(".xml")) {
                String layoutId = layoutFileName.substring(0, layoutFileName.indexOf(".xml"));
                try {
                    page = PortletLayoutDescriptor.loadPortletPage(layoutFile.getAbsolutePath(), LAYOUT_MAPPING_PATH);
                    page.setLayoutDescriptor(layoutFile.getAbsolutePath());
                    if (page.getEditable()) editableLayoutIds.add(layoutId);
                    masterLayouts.put(layoutId, page);
                } catch (Exception e) {
                    log.error("Unable to load portlet page: " + layoutFileName, e);
                }
            }
        }

        String newuserLayoutPath = settingsService.getRealSettingsPath("layouts/users/");

        File userdir = new File(newuserLayoutPath);
        if (!userdir.exists()) {
            userdir.mkdir();
        }

        // test page creation times
        /*
        PortletPage copy = null;
        PortletPage guest = masterLayouts.get(GUEST_PAGE);
        long startTime = System.currentTimeMillis();
        try {
            for (int i = 0; i < 1000; i++) {
                copy = (PortletPage) deepCopy2(guest);
            }
            long endTime = System.currentTimeMillis();
            System.err.println("Serialize copy 1000 pages in = " + (endTime - startTime) + " (ms) ");
            for (int i = 0; i < 1000; i++) {
                copy = (PortletPage) deepCopy(guest);
            }
            startTime = System.currentTimeMillis();
            System.err.println("Clone copy 1000 pages in = " + (startTime - endTime) + " (ms) ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        portalConfigService = (PortalConfigService) PortletServiceFactory.createPortletService(PortalConfigService.class, true);
    }

    public static synchronized PortletPageFactory getInstance() {
        if (instance == null) {
            instance = new PortletPageFactory();
        }
        return instance;
    }

    public void login(HttpServletRequest request) {

    }

    public void logout(HttpSession session) {
        log.debug("in logout PortletPageFactory");
        String sessionId = session.getId();

        Map usersLayouts = (Map) layouts.get(sessionId);
        if (usersLayouts != null) {
            Iterator it = usersLayouts.keySet().iterator();
            while (it.hasNext()) {
                String layoutId = (String) it.next();
                log.debug("Removing " + layoutId + " container for:" + sessionId);
                it.remove();
            }
            layouts.remove(sessionId);
        }
        if (userLayouts.containsKey(sessionId)) {
            log.debug("Removing user  container for:" + sessionId);
            userLayouts.remove(sessionId);
        }

    }

    public Set<String> getEditableLayoutIds() {
        return editableLayoutIds;
    }

    public Set<String> getLayoutIds() {
        return masterLayouts.keySet();
    }

    public PortletTabbedPane getUserTabbedPane(PortletRequest req) {

        String sessionId = req.getPortletSession(true).getId();

        String userLayout = USER_LAYOUT_DIR + File.separator + req.getUserPrincipal().getName();

        if (userLayouts.containsKey(sessionId)) {
            PortletPage page = (PortletPage) userLayouts.get(USER_PAGE);
            PortletTabbedPane pane = new PortletTabbedPane();
            pane.setLayoutDescriptor(userLayout);
            PortletComponent comp = (PortletComponent) page.getPortletComponent();
            PortletTabbedPane existPane = (PortletTabbedPane) comp;
            List<PortletTab> tabs = existPane.getPortletTabs();
            for (PortletTab tab : tabs) {
                if (tab.getCanModify()) {
                    pane.addTab(tab);
                }
            }
            return (!pane.getPortletTabs().isEmpty() ? pane : null);
        }

        File f = new File(userLayout);
        PortletTabbedPane pane = null;


        if (f.exists()) {
            try {
                pane = PortletLayoutDescriptor.loadPortletTabs(userLayout, LAYOUT_MAPPING_PATH);
                pane.setLayoutDescriptor(userLayout);
                log.debug("Adding user tab to layout");
            } catch (Exception e) {
                log.error("Unable to make a clone of the templatePage", e);
                return null;
            }
        } else {
            return null;
        }

        // create tmp page
        PortletPage tmpPage = new PortletPage();
        try {
            //tmpPage.setLayoutDescriptor(userLayout + ".tmp");
            PortletTabbedPane tmpPane = (PortletTabbedPane) deepCopy((PortletComponent) pane);//changed by Valia: deepCopy for PortletTabbedPane throws exception...
            tmpPage.setPortletComponent(tmpPane);
            this.setPageTheme(tmpPage, req);
            tmpPage.init(req, new ArrayList<ComponentIdentifier>());
            tmpPane.save();
            return tmpPane;
        } catch (Exception e) {
            log.error("Unable to save user pane!", e);
        }

        return null;

    }

    public void setPageTheme(PortletPage page, PortletRequest req) {
        String theme = null;
        User user = (User) req.getAttribute(SportletProperties.PORTLET_USER);
        if (user != null) theme = (String) user.getAttribute(User.THEME);
        if (theme == null) {
            theme = portalConfigService.getProperty(PortalConfigService.DEFAULT_THEME);
        }
        req.getPortletSession().setAttribute(SportletProperties.LAYOUT_THEME, theme, PortletSession.APPLICATION_SCOPE);
    }

    public PortletPage createPortletPageCopy(String layoutId) {
        // get the master copy of the page
        PortletPage masterPage = (PortletPage) masterLayouts.get(layoutId);
        PortletPage copy = null;
        // there are two cases where a master may not be there, TCK case and logged in  user
        try {
            copy = (PortletPage) deepCopy(masterPage);
        } catch (Exception e) {
            log.error("Failed to make a copy of the master page: " + layoutId);
            if (layoutId.equals(ERROR_PAGE))
            	return null;
            return createErrorPage();
        }
        return copy;
    }

    public void savePortletPageMaster(PortletPage page) {
        String layoutDesc = page.getLayoutDescriptor();
        String layoutId = layoutDesc.substring(layoutDesc.lastIndexOf(File.separator) + 1, layoutDesc.lastIndexOf(".xml"));
        log.debug("saving layout: " + layoutId);

        try {
            PortletLayoutDescriptor.saveLayoutComponent(page, layoutDesc, LAYOUT_MAPPING_PATH);
            masterLayouts.put(layoutId, page);
        } catch (Exception e) {
            log.error("Unable to save layout descriptor: " + layoutDesc, e);
        }

        // remove any active layouts with this layoutId
        for (Map<String, PortletPage> map : layouts.values()) {
            Map userLayouts = (Map) map;
            userLayouts.remove(layoutId);
            log.debug("removing a layout: " + layoutId);
        }
    }


    /**
     * This is the primary entrance to retrieving a PortletPage
     *
     * @param event the gridsphere event
     * @return a portlet page
     */
    public PortletPage getPortletPage(GridSphereEvent event) {
        // first check for layout id in request parameter
        PortletRequest req = event.getRenderRequest();
        String layoutId = (String) req.getAttribute(SportletProperties.LAYOUT_PAGE);
        //System.err.println("layoutId==" + layoutId);
        if (layoutId == null) {
            if (req.getUserPrincipal() == null) {
                // if no reference to a layout exists, return a guest layout
                //System.err.println("guest page");
                layoutId = GUEST_PAGE;
            } else {
                //System.err.println("user page");
                layoutId = USER_PAGE;
            }
            if (event.getLayoutID() != null) {
                layoutId = event.getLayoutID();
                // make sure if user has logged in and URL specifies guest page, then user gets directed to user page
                if (layoutId.equals(GUEST_PAGE) && (req.getUserPrincipal() != null)) layoutId = USER_PAGE;
            }

            req.setAttribute(SportletProperties.LAYOUT_PAGE, layoutId);
        }
        return getPortletPageFromHash(req, layoutId);
    }

    /**
     * Added by Valia Tsagkalidou: We need this method so as to know where to put user's custom layout from a portlet.
     *
     * @return the path where users' layout are stored
     */
    public String getUserLayoutPath() {
        return USER_LAYOUT_DIR;
    }

    /**
     * This returns the page from the hashtable or creates a new one if necessary
     *
     * @param req      the portlet request
     * @param layoutId the layout id
     * @return the page
     */
    protected PortletPage getPortletPageFromHash(PortletRequest req, String layoutId) {
        PortletSession session = req.getPortletSession();
        PortletPage page = null;
        Map<String, PortletPage> usersLayouts = (Map<String, PortletPage>) layouts.get(session.getId());
        if (usersLayouts == null) {
            usersLayouts = new HashMap<String, PortletPage>();
            layouts.put(session.getId(), usersLayouts);
        }

        // now check for existing layout in hash
        page = (PortletPage) usersLayouts.get(layoutId);
        // only if no page exists, create a new one and place in hash
        if (page == null) {
            page = createPortletPage(req, layoutId);
            String role = page.getRequiredRole();
            if (!role.equals("") && !req.isUserInRole(role)) {
                // use existing page
                log.debug("User does not have proper permissions for layout=" + layoutId + "!!");
                if (req.getUserPrincipal() == null) {
                    // if no reference to a layout exists, return a guest layout
                    layoutId = GUEST_PAGE;
                } else {
                    layoutId = USER_PAGE;
                }
                page = (PortletPage) usersLayouts.get(layoutId);
                if (page == null) page = createPortletPage(req, layoutId);
                req.setAttribute(SportletProperties.LAYOUT_PAGE, layoutId);
            }
            usersLayouts.put(layoutId, page);
            log.debug("Creating new page " + layoutId + " placing in session " + session.getId());
            sessionManager.addSessionListener(session.getId(), this);
        }
        setPageTheme(page, req);
        return page;
    }

    public PortletPage createPortletPage(PortletRequest req, String layoutId) {
        // get the master copy of the page

        PortletPage masterPage = (PortletPage) masterLayouts.get(layoutId);
        PortletPage copy = null;
        // there are two cases where a master may not be there, TCK case and logged in  user
        if (masterPage == null) {
            log.info("master page is null " + layoutId);
            if (layoutId.equals(TCK_PAGE)) {
                copy = createTCKPage(req);
            } else {
                if (req.getUserPrincipal() == null) {
                    // if no reference to a layout exists, return a guest layout
                    //changed by Valia: previously it returned
                    copy = getPortletPageFromHash(req, GUEST_PAGE);
                } else {
                    //changed by Valia: previously it returned
                    copy = getPortletPageFromHash(req, USER_PAGE);
                }
            }
        } else {
            try {
                copy = (PortletPage) deepCopy(masterPage);
                log.info("Creating deep copy of page " + layoutId);
            } catch (Exception e) {

                log.error("Failed to make a copy of the master page: " + layoutId, e);
                return createErrorPage();
            }
        }

        /* added by Valia Tsagkalidou */
        PortletComponent generalPane = copy.getPortletComponent();
        if (req.getUserPrincipal() != null && generalPane instanceof PortletTabbedPane) {
            PortletTabbedPane pane = (PortletTabbedPane) generalPane;
            // place user tabs after group tabs
            PortletTabbedPane userPane = getUserTabbedPane(req);
            if (userPane != null) {
                List userTabs = userPane.getPortletTabs();
                for (int i = 0; i < userTabs.size(); i++) {
                    PortletTab subTab = (PortletTab) userTabs.get(i);
                    log.debug("adding user tab: " + subTab.getTitle("en"));
                    try {
                        pane.addTab((PortletTab) deepCopy(subTab));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            copy.setPortletComponent(pane);
        }
        /* End of addition */

        setPageTheme(copy, req);
        copy.init(req, new ArrayList<ComponentIdentifier>());
        return copy;
    }

    public void removePortletPage(PortletRequest req) {
        PortletSession session = req.getPortletSession();

        String id = session.getId();
        if (layouts.containsKey(id)) {
            layouts.remove(id);
        }
        //log.debug("removed user layout: " + userLayout);
    }

    /*
    public synchronized PortletPage shallowCopy(PortletPage page) throws CloneNotSupportedException {
        return (PortletPage) page.clone();
    }
    */

    public synchronized PortletTabbedPane deepCopy(PortletTabbedPane pane) throws CloneNotSupportedException {
        return (PortletTabbedPane) deepCopy(pane);
    }

    public synchronized Object deepCopy(Object oldObj) throws Exception {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(oldObj);   // C
            oos.flush();               // D
            ByteArrayInputStream bin =
                    new ByteArrayInputStream(bos.toByteArray()); // E
            ois = new ObjectInputStream(bin);                  // F
            // return the new object
            return ois.readObject(); // G
        } catch (Exception e) {
            throw e;
        } finally {
            if (oos != null) oos.close();
            if (ois != null) ois.close();
        }
    }


    public void logStatistics() {
        /*
        log.debug("\n\nnumber of guest layouts: " + guests.size());
        Iterator it = guests.keySet().iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            log.debug("guest has session: " + id);
        }
        log.debug("number of user layouts: " + userLayouts.size());
        it = userLayouts.keySet().iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            log.debug("user has session: " + id);
        }
        */
    }

    // TODO
    public PortletPage createErrorPage() {
        return createPortletPageCopy(ERROR_PAGE);
    }

    public PortletPage createTCKPage(PortletRequest req) {
        String[] portletNames = req.getParameterValues("portletName");
        PortletPage page = null;
        // Sun TCK test uses Jakarta Commons-HttpClient/2.0beta1

        // if (event.getClient().getUserAgent().indexOf("HttpClient") > 0) {
        if (portletNames != null) {
            log.info("Creating TCK LAYOUT!");
            String pageName = req.getParameter("pageName");
            page = new PortletPage();
            PortletTableLayout tableLayout = new PortletTableLayout();
            StringTokenizer tokenizer;
            for (int i = 0; i < portletNames.length; i++) {
                tokenizer = new StringTokenizer(portletNames[i], "/");
                String appName = tokenizer.nextToken();
                String portletName = tokenizer.nextToken();
                //String portletClass = registry.getPortletClassName(appName, portletName);
                //if (portletClass == null) {
                //    log.error("Unable to find portlet class for " + portletName);
                //}
                if (pageName == null) {
                    pageName = "TCK_testpage_" + portletName;
                }
                PortletFrame frame = new PortletFrame();
                PortletTitleBar tb = new PortletTitleBar();
                //tb.setPortletClass(portletClass);
                tb.setPortletClass(appName + "#" + portletName);
                frame.setPortletTitleBar(tb);
                //frame.setPortletClass(portletClass);
                frame.setPortletClass(appName + "#" + portletName);
                tableLayout.addPortletComponent(frame);
            }

            PortletTab tab = new PortletTab();
            tab.setTitle("en", pageName);
            tab.setPortletComponent(tableLayout);
            PortletTabbedPane pane = new PortletTabbedPane();
            pane.addTab(tab);
            page.setPortletComponent(pane);
            page.setLayoutDescriptor("/tmp/test.xml");
            try {
                page.save(context);
                this.setPageTheme(page, req);
                page.init(req, new ArrayList<ComponentIdentifier>());
            } catch (IOException e) {
                log.error("Unable to save TCK page to /tmp/test.xml", e);
            }
        }
        //  }
        return page;
    }

    // TODO
    public void renameRole(PortletRequest req, String oldRole, String newRole) {
        List<PortletPage> pages = new ArrayList<PortletPage>();
        pages.add(masterLayouts.get("GuestUserLayout"));
        pages.add(masterLayouts.get("LoggedInUserLayout"));
        try {
            for (PortletPage p : pages) {
                PortletPage page = (PortletPage) deepCopy(p);
                page.init(req, new ArrayList<ComponentIdentifier>());
                List<ComponentIdentifier> compList = page.getComponentIdentifierList();
                boolean resetLayout = false;
                for (ComponentIdentifier compId : compList) {
                    PortletComponent comp = compId.getPortletComponent();
                    String reqRole = comp.getRequiredRole();
                    if (reqRole.equalsIgnoreCase(oldRole)) {
                        resetLayout = true;
                        comp.setRequiredRole(newRole);
                    }
                }
                if (resetLayout) savePortletPageMaster(page);
            }
        } catch (Exception e) {
            log.error("Unable to load/save descriptor", e);
        }

    }

}
