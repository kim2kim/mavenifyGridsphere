package org.gridsphere.services.core.filter.impl.descriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridsphere.portletcontainer.impl.JavaXMLBindingFactory;
import org.gridsphere.services.core.filter.PortalFilter;
import org.gridsphere.services.core.filter.PortalFilterConfig;
import org.gridsphere.services.core.filter.impl.PortalFilterConfigImpl;
import org.gridsphere.services.core.persistence.PersistenceManagerException;
import org.gridsphere.services.core.persistence.PersistenceManagerXml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
public class PortalFilterDescriptor {

    private Log log = LogFactory.getLog(PortalFilterDescriptor.class);
    private List<PortalFilter> portalFilters = new ArrayList<PortalFilter>();
    private URL FILTER_MAPPING_PATH = getClass().getResource("/config/mapping/portal-filters-mapping.xml");

    /**
     * Constructor disallows non-argument instantiation
     */
    private PortalFilterDescriptor() {
    }

    public PortalFilterDescriptor(String descriptorFile) throws PersistenceManagerException {
        PersistenceManagerXml pmXML = JavaXMLBindingFactory.createPersistenceManagerXml(descriptorFile, FILTER_MAPPING_PATH);
        PortalFilterCollection portalFilterCollection = (PortalFilterCollection) pmXML.load();
        List<PortalFilterDefinition> portalFilterList = portalFilterCollection.getPortalFilterList();
        for (PortalFilterDefinition def : portalFilterList) {
            String filterImpl = def.getImplementation();
            try {
                PortalFilter filterClass = (PortalFilter) Class.forName(filterImpl).newInstance();
                PortalFilterConfig filterConfig = new PortalFilterConfigImpl(def.getConfigProperties());
                filterClass.init(filterConfig);
                portalFilters.add(filterClass);
            } catch (ClassNotFoundException e) {
                log.error("Unable to find filter class: " + filterImpl, e);
            } catch (InstantiationException e) {
                log.error("Unable to instantiate filter class: " + filterImpl, e);
            } catch (IllegalAccessException e) {
                log.error("Illegal access on filter class: " + filterImpl, e);
            }
        }
    }

    public List<PortalFilter> getPortalFilters() {
        return portalFilters;
    }

}
