package org.gridsphere.portletcontainer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridsphere.portletcontainer.PortletSessionListener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.*;

/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
public class PortletSessionManager implements HttpSessionListener {

    private static PortletSessionManager instance = new PortletSessionManager();
    private Log log = LogFactory.getLog(PortletSessionManager.class);

    private Hashtable<String, HttpSession> sessions = new Hashtable<String, HttpSession>();
    private Hashtable<String, List<PortletSessionListener>> sessionListeners =
            new Hashtable<String, List<PortletSessionListener>>();

    private PortletSessionManager() {

    }

    public static PortletSessionManager getInstance() {
        return instance;
    }

    public int getNumSessions() {
        return sessions.size();
    }

    public Collection<HttpSession> getSessions() {
        return sessions.values();
    }

    public Set<String> getSessionIds() {
        return sessions.keySet();
    }

    /**
     * Record the fact that a session has been created.
     *
     * @param event The session event
     */
    public void sessionCreated(HttpSessionEvent event) {
        log.debug("sessionCreated('" + event.getSession().getId() + "')");
        String id = event.getSession().getId();
        sessions.put(id, event.getSession());
        dumpSessions();
    }


    /**
     * Record the fact that a session has been destroyed.
     *
     * @param event The session event
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        log.debug("sessionDestroyed('" + event.getSession().getId() + "')");
        HttpSession httpSession = event.getSession();
        if (httpSession != null) {
            String id = event.getSession().getId();

            List<PortletSessionListener> listeners = sessionListeners.get(id);
            if (listeners != null) {
                Iterator<PortletSessionListener> it = listeners.iterator();
                while (it.hasNext()) {
                    PortletSessionListener sessionListener = (PortletSessionListener) it.next();

                    log.info("logging a session listener out: " + sessionListener.getClass());
                    sessionListener.logout(httpSession);

                }
                log.info("Removing session: " + httpSession.getId());
                sessions.remove(id);
                sessionListeners.remove(id);
            }
        } else {
            log.info("Not sure why sessionDestroyed listener provides null session id!");
        }
        dumpSessions();
    }

    public void addSessionListener(String sessionId, PortletSessionListener sessionListener) {
        log.debug("adding session listener for : " + sessionId + " " + sessionListener.getClass());
        HttpSession session = (HttpSession) sessions.get(sessionId);
        if (session != null) {
            List<PortletSessionListener> listeners = sessionListeners.get(sessionId);
            if (listeners == null) listeners = new ArrayList<PortletSessionListener>();
            listeners.add(sessionListener);
            System.err.println("adding session listener for : " + sessionId + " " + sessionListener.getClass());
            sessionListeners.put(sessionId, listeners);
        }
        dumpSessions();
    }

    public void dumpSessions() {
        log.debug("PortletSessionManager Session information:");
        log.debug("# current sessions: " + sessions.size());
        Set<String> keySet = sessions.keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext()) {
            log.debug("session #id: " + (String) it.next());
        }
    }

}
