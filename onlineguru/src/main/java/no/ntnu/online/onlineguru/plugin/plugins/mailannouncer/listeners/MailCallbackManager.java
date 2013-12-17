package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class MailCallbackManager {

    private Map<String, String> aliases;
    private Map<String, MailCallbackListener> mailCallbackListeners;

    public MailCallbackManager() {
        this.aliases = new HashMap<String, String>();
        this.mailCallbackListeners = new HashMap<String, MailCallbackListener>();
    }

    public void put(String mailinglist, MailCallbackListener mailCallbackListener) {
        mailCallbackListeners.put(mailinglist, mailCallbackListener);
    }

    public boolean containsKey(String mailinglist) {
        return mailCallbackListeners.containsKey(mailinglist);
    }

    public MailCallbackListener get(String mailinglist) {
        return mailCallbackListeners.get(mailinglist);
    }

    public void addAlias(String mailinglist, String alias) {
        aliases.put(mailinglist, alias);
    }

    public String getAlias(String mailinglist) {
        return aliases.get(mailinglist);
    }

    public String getOriginal(String alias) {
        for (Map.Entry<String, String> e : aliases.entrySet()) {
            if (alias.equals(e.getValue())) {
                return e.getKey();
            }
        }
        return null;
    }

}
