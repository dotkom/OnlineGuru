package no.ntnu.online.onlineguru.plugin.plugins.flags.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class ChannelFlags {

    private Map<String, String> flags = new HashMap<String, String>();

    public ChannelFlags() {
        this.flags = new HashMap<String, String>();
    }

    public ChannelFlags(Map<String, String> flags) {
        this.flags = flags;
    }

    public boolean saveFlags(String username, String flags) {
        this.flags.put(username, flags);
        return true;
    }

    public String getFlags(String username) {
        return this.flags.get(username);
    }

}
