package no.ntnu.online.onlineguru.plugin.plugins.github.listeners;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class Listeners {

    private Map<String, GithubCallbackListener> githubCallbackListeners;

    public Listeners() {
        this.githubCallbackListeners = new HashMap<String, GithubCallbackListener>();
    }

    public void put(String repository, GithubCallbackListener githubCallbackListener) {
        this.githubCallbackListeners.put(repository, githubCallbackListener);
    }

    public boolean containsKey(String repository) {
        return githubCallbackListeners.containsKey(repository);
    }

    public GithubCallbackListener get(String repository) {
        return githubCallbackListeners.get(repository);
    }

}
