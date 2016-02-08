package org.gridsphere.services.core.rss.impl;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import org.gridsphere.portlet.service.PortletServiceUnavailableException;
import org.gridsphere.portlet.service.spi.PortletServiceConfig;
import org.gridsphere.portlet.service.spi.PortletServiceProvider;
import org.gridsphere.services.core.rss.RssService;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the RssService.
 * $Id$
 */
public class RssServiceImpl implements RssService, PortletServiceProvider {

    private long CACHE_TIME = 20 * 60 * 1000; // in minutes
    private Map<String, SyndFeed> cachedStore = new HashMap<String, SyndFeed>();
    private Map<String, Long> cachedTime = new HashMap<String, Long>();

    public SyndFeed getFeed(String url) throws FeedException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        long diff = CACHE_TIME + 1;
        // check if the url is in the cachedStore
        if (cachedStore.containsKey(url)) {
            Long cachedTime = (Long) this.cachedTime.get(url);
            diff = System.currentTimeMillis() - cachedTime.longValue();
        }


        if (diff > CACHE_TIME) {
            try {
                URL rssUrl = new URL(url);
                URLConnection c = rssUrl.openConnection();
                c.setConnectTimeout(2000);
                c.setReadTimeout(2000);
                c.connect();
                InputSource src = new InputSource(c.getInputStream());
                feed = input.build(src);
            } catch (IOException e) {
                throw new FeedException("Invalid URL.");
            }
            cachedStore.put(url, feed);
            cachedTime.put(url, new Long(System.currentTimeMillis()));
        }
        return (SyndFeed) cachedStore.get(url);
    }

    public void init(PortletServiceConfig config) throws PortletServiceUnavailableException {
        Long cacheTime = new Long(config.getInitParameter("cache_time"));
        CACHE_TIME = cacheTime.longValue() * 60 * 1000;
    }

    public void destroy() {
    }
}
