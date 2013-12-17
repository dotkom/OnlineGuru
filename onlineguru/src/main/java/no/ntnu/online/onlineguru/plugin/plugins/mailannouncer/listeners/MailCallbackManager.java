package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class MailCallbackManager {

    private Map<String, String> aliases;
    private Map<String, MailCallbackListener> mailCallbackListenerMap;

    public MailCallbackManager() {
        this.mailCallbackListenerMap = new HashMap<String, MailCallbackListener>();
    }

    public void put(String mailinglist, MailCallbackListener mailCallbackListener) {
        mailCallbackListenerMap.put(mailinglist, mailCallbackListener);
    }

    public boolean containsKey(String mailinglist) {
        return mailCallbackListenerMap.containsKey(mailinglist);
    }

    public MailCallbackListener get(String mailinglist) {
        return mailCallbackListenerMap.get(mailinglist);
    }

    public void addAlias(String mailinglist, String alias) {
        aliases.put(mailinglist, alias);
    }

    public String getAlias(String mailinglist) {
        return aliases.get(mailinglist);
    }

}
