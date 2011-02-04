package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import java.util.HashMap;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Nov 14, 2010
 * Time: 4:25:30 PM
 */
public interface AnnouncementRepository {
    public void save(HashMap<String, Announce> hashMap);
    public HashMap<String, Announce> load();
}
