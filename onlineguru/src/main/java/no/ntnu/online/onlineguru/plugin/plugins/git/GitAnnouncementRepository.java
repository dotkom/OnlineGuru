package no.ntnu.online.onlineguru.plugin.plugins.git;

import java.util.HashMap;

/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:47
 */
public interface GitAnnouncementRepository {
    public void save(HashMap<String, IRCAnnounce> hashMap);

    public HashMap<String, IRCAnnounce> load();
}

