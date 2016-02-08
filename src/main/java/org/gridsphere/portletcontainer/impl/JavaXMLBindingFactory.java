package org.gridsphere.portletcontainer.impl;


import org.gridsphere.services.core.persistence.PersistenceManagerXml;

import java.net.URL;

public class JavaXMLBindingFactory {

    /**
     * Returns an instance of a PersistenceManagerXML from a descriptor and mapping URL
     *
     * @param descriptorURL the descriptor location
     * @param mappingURL    the mapping location
     * @return an instance of PersistenceManagerXmlImpl
     */
    public static PersistenceManagerXml createPersistenceManagerXml(String descriptorURL, URL mappingURL) {
        return new PersistenceManagerXmlImpl(descriptorURL, mappingURL);
    }


}