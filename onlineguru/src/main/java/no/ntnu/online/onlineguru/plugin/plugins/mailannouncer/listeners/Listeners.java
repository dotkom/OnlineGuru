package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.CallbackListener;

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

    public void put(String repository, CallbackListener callbackListener) {
        callbackListeners.put(repository, callbackListener);
    }

    public boolean containsKey(String repository) {
        return callbackListeners.containsKey(repository);
    }

    public CallbackListener get(String repository) {
        return callbackListeners.get(repository);
    }

}
