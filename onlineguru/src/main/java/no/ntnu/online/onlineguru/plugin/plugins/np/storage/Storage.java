package no.ntnu.online.onlineguru.plugin.plugins.np.storage;

import no.ntnu.online.onlineguru.plugin.plugins.np.model.Alias;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.Scrobble;
import no.ntnu.online.onlineguru.utils.JSONStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class Storage {

    public static String databaseFolder = "database/";
    public static String databaseFile = databaseFolder + "scrobble.json";

    private Map<String, Scrobble> scrobbles = new HashMap<String, Scrobble>();
    private Map<String, Alias> aliases = new HashMap<String, Alias>();

    /*
     * Methods for scrobbling
     */

    public boolean putScrobble(String nick, Scrobble scrobble) {
        scrobbles.put(nick, scrobble);
        return save();
    }

    public boolean hasScrobble(String nick) {
        return scrobbles.containsKey(nick);
    }

    public Scrobble getScrobble(String nick) {
        return scrobbles.get(nick);
    }

    /*
     * Methods for aliases
     */

    public boolean putAlias(String nick, Alias alias) {
        aliases.put(nick, alias);
        return save();
    }

    public boolean hasAlias(String nick) {
        return aliases.containsKey(nick);
    }

    public Alias getAlias(String nick) {
        return aliases.get(nick);
    }

    public Alias getAliasByApikey(String apikey) {
        for (Alias a : aliases.values()) {
            if (a.getApikey().equals(apikey)) return a;
        }
        return null;
    }

    public boolean removeAlias(String nick) {
        aliases.remove(nick);
        return save();
    }

    /*
     * Method to save the data
     */

    private boolean save() {
        return JSONStorage.save(databaseFile, this);
    }

}
