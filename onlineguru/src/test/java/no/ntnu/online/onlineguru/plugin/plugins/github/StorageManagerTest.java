package no.ntnu.online.onlineguru.plugin.plugins.github;

import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.CallbackListener;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class StorageManagerTest {

    private String database_file = "database/githubtest.db";
    private StorageManager storageManager = new StorageManager(database_file);

    private Map<String, CallbackListener> callbackListeners = new HashMap<String, CallbackListener>();

    @Test
    public void testSave() {
        CallbackListener cl1 = new CallbackListener();
        callbackListeners.put("https://github.com/moo/moo", new CallbackListener());

        cl1.getOrCreateSubscription("testnetwork", "#testchannel1");
        cl1.getOrCreateSubscription("testnetwork", "#testchannel2");

        callbackListeners.put("https://github.com/test/test", new CallbackListener());

        cl1.getOrCreateSubscription("testnetwork", "#testchannel3");
        cl1.getOrCreateSubscription("testnetwork", "#testchannel4");


        System.out.println(storageManager.saveListeners(callbackListeners));
    }

    @Test
    public void testLoad() {
        System.out.println("durr");
    }

}
