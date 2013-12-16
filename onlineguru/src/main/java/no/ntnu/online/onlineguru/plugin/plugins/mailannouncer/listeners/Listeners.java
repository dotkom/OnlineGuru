package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class Listeners {

    private Map<String, MailCallbackListener> mailCallbackListenerMap;

    public Listeners() {
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

}
