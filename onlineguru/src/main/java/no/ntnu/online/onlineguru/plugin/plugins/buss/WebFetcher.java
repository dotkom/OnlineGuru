package no.ntnu.online.onlineguru.plugin.plugins.buss;

import java.io.IOException;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Sep 19, 2010
 * Time: 12:05:04 AM
 */
public interface WebFetcher {
    public String get(String url) throws IOException;
}
