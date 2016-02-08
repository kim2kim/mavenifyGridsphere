package org.gridsphere.services.core.customization.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.gridsphere.portlet.service.PortletServiceUnavailableException;
import org.gridsphere.portlet.service.spi.PortletServiceConfig;
import org.gridsphere.portlet.service.spi.PortletServiceProvider;
import org.gridsphere.services.core.customization.SettingsService;
import org.gridsphere.servlets.GridSphereServlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsServiceImpl implements PortletServiceProvider, SettingsService {

    private Log log = LogFactory.getLog(GridSphereServlet.class);
    private String settingsPath = System.getProperty("user.home") + File.separator + ".gridsphere";
    private Perl5Util util = new Perl5Util();

    /**
     * Checks if the JNDI variable for storing gs settings data is set, if not the default is assumed
     *
     * @param config
     * @throws PortletServiceUnavailableException
     *
     */
    public void init(PortletServiceConfig config) throws PortletServiceUnavailableException {

        log.debug("In Settings");

        try {
            Context initCtx = new InitialContext();
            javax.naming.Context env = (Context) initCtx.lookup("java:comp/env");
            settingsPath = (String) env.lookup("gridspheresettingsdir");

            //GPF-480 feature: change ${ENV.SYS_VAR_NAME} to value of system environment variable with named "SYS_VAR_NAME"
            while (util.match("m/\\$\\{ENV\\.([^}]+)\\}/", settingsPath)) {
                MatchResult matchResult = util.getMatch();
                String systemEnvironmentVariableName = matchResult.group(1);
                //can cause NPE if system environment variable was not set
                settingsPath = settingsPath.replaceAll("\\$\\{ENV\\." + systemEnvironmentVariableName + "\\}", System.getenv(systemEnvironmentVariableName).replace('\\', '/'));
            }
            log.info("Got config settings from JNDI");
        } catch (NamingException e) {
            // it does not exist, which is ok as well, we just use the default
            settingsPath = System.getProperty("user.home") + File.separator + ".gridsphere";
        } catch (NullPointerException e) {
            // it exists, but some of system environment variable used in it's value was not set
            settingsPath = System.getProperty("user.home") + File.separator + ".gridsphere";
        }
        // check if the path exist, if not create it and copy the template files (from WEB-INF/CustomPortal) to it
        File path = new File(settingsPath);
        
        log.info("GridSphere Settings will be saved in: " + settingsPath);
        if (!path.exists()) {
        	log.info("GridSphere ConfigDir did not exist, created.");
            path.mkdirs();
        }else {
        	log.info("Path does not exist " + path.getAbsolutePath());
        }
        
        try {
        	log.info("About to copy files to directory");
            copyFiles(config.getServletContext().getRealPath("/WEB-INF/CustomPortal"), settingsPath);
            log.info("Copy files to directory");
        } catch (IOException e) {
            log.error("Could not copy files to defined destination: " + config.getServletContext().getRealPath("/WEB-INF/CustomPortal") + " to " + settingsPath);
        }
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getSettingsPath() {
        return settingsPath;
    }

    public String getRealSettingsPath(String path) {
        String result = settingsPath + File.separator + path;
        //result = result.replaceAll("/", File.separator);
        log.debug("My Settings Path where lucene data is stored " + result);
        File f = new File(result);
        log.debug("The directory of lucene repository and workspace " + f.getAbsolutePath());
        if (f.isDirectory() && !result.endsWith(File.separator)) result = result + File.separator;
        return result;
    }

    private void copyFiles(String strPath, String dstPath) throws IOException {
        File src = new File(strPath);
        File dest = new File(dstPath);

        if (src.isDirectory()) {
            //if(dest.exists()!=true)
            dest.mkdirs();
            String list[] = src.list();

            for (int i = 0; i < list.length; i++) {
                String dest1 = dest.getAbsolutePath() + File.separator + list[i];
                String src1 = src.getAbsolutePath() + File.separator + list[i];
                copyFiles(src1, dest1);
            }
        } else {
            FileInputStream fin = new FileInputStream(src);
            FileOutputStream fout = new FileOutputStream(dest);
            int c;
            while ((c = fin.read()) >= 0)
                fout.write(c);
            fin.close();
            fout.close();
        }
    }
}
