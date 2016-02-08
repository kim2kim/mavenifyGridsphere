/*
 * @author <a href="mailto:novotny@gridsphere.org">Jason Novotny</a>
 * @version $Id$
 */
package org.gridsphere.portlet.service.spi.impl;

import org.gridsphere.portlet.service.spi.PortletServiceConfig;
import org.gridsphere.portlet.service.spi.impl.descriptor.PortletServiceDefinition;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Properties;

/**
 * The <code>SportletServiceConfig</code> provides an implementation
 * of the <code>PortletServiceConfig</code> interface through which
 * portlet services access the configuration settings from the services
 * descriptor file.
 */
public class PortletServiceConfigImpl implements PortletServiceConfig {

    private Properties configProperties;
    private ServletContext servletContext;

    /**
     * Constructor disallows non-argument instantiation
     */
    private PortletServiceConfigImpl() {
    }

    /**
     * Constructs an instance of SportletServiceConfig using the supplied
     * service class, the configuration properties and the  servlet configuration
     *
     * @param def            the sportlet service definition
     * @param servletContext the <code>ServletConfig</code>
     */
    public PortletServiceConfigImpl(PortletServiceDefinition def,
                                 ServletContext servletContext) {
        this.configProperties = def.getConfigProperties();
        this.servletContext = servletContext;
    }

    /**
     * Returns the init parameter with the given name.
     *
     * @param name the name of the requested init parameter.
     * @return the init parameter
     */
    public String getInitParameter(String name) {
        return configProperties.getProperty(name);
    }

    /**
     * Returns the init parameter with the given name.
     *
     * @param name  the name of the requested init parameter.
     * @param value the value of the init parameter
     */
    public void setInitParameter(String name, String value) {
        configProperties.setProperty(name, value);
    }

    /**
     * Returns the init parameter with the given name. It returns the given default
     * value if the parameter is not found.
     *
     * @param name         the name of the requested init parameter.
     * @param defaultValue the default value to return.
     * @return the init parameter value if exists, otherwise defaultValue
     */
    public String getInitParameter(String name, String defaultValue) {
        return configProperties.getProperty(name, defaultValue);
    }

    /**
     * Returns an enumeration with the names of all init parameters provided in the portlet service configuration.
     *
     * @return an enumeration of the init parameters
     */
    public Enumeration getInitParameterNames() {
        return configProperties.keys();
    }

    /**
     * Returns the servlet context
     *
     * @return the servlet context
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

}
