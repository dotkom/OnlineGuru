package no.ntnu.online.onlineguru.plugin.plugins.np.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class ScrobbleStorage {

    public static String database_folder = "database/";
    public static String database_file = database_folder + "scrobble.json";
    private Map<String, Scrobble> scrobbles = new HashMap<String, Scrobble>();

    public void put(String nick, Scrobble scrobble) {
        scrobbles.put(nick, scrobble);
    }

    public boolean containsKey(String nick) {
        return scrobbles.containsKey(nick);
    }

    public Scrobble get(String nick) {
        return scrobbles.get(nick);
    }

}
