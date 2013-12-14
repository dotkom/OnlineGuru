package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class Listeners {

    private Map<String, CallbackListener> callbackListeners;

    public Listeners() {
        this.callbackListeners = new HashMap<String, CallbackListener>();
    }

    public void put(String mailinglist, CallbackListener callbackListener) {
        callbackListeners.put(mailinglist, callbackListener);
    }

    public boolean containsKey(String mailinglist) {
        return callbackListeners.containsKey(mailinglist);
    }

    public CallbackListener get(String mailinglist) {
        return callbackListeners.get(mailinglist);
    }

}
