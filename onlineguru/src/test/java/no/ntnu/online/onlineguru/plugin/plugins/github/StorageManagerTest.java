package no.ntnu.online.onlineguru.plugin.plugins.github;

import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.CallbackListener;
import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.Listeners;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Håvard Slettvold
 */
public class StorageManagerTest {

    private String database_file = "database/github_storage_test.db";
    private StorageManager storageManager = new StorageManager(database_file);

    private Listeners listeners = new Listeners();

    @Test
    public void testSave() {

        CallbackListener cl1 = new CallbackListener();
        listeners.put("https://github.com/moo/moo", cl1);

        cl1.getOrCreateSubscription("testnetwork", "#testchannel1");
        cl1.getOrCreateSubscription("testnetwork", "#testchannel2");

        CallbackListener cl2 = new CallbackListener();
        listeners.put("https://github.com/test/test", cl2);

        cl2.getOrCreateSubscription("testnetwork", "#testchannel3");
        cl2.getOrCreateSubscription("testnetwork", "#testchannel4");

        String desiredResult = "{\"callbackListeners\":{\"https://github.com/moo/moo\":{\"announceSubscriptions\":[{\"network\":\"testnetwork\",\"channel\":\"#testchannel1\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false},{\"network\":\"testnetwork\",\"channel\":\"#testchannel2\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false}]},\"https://github.com/test/test\":{\"announceSubscriptions\":[{\"network\":\"testnetwork\",\"channel\":\"#testchannel3\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false},{\"network\":\"testnetwork\",\"channel\":\"#testchannel4\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false}]}}}";

        /* Better formatted;
            {
                "https://github.com/moo/moo": {
                    "subscribers": [{
                            "network": "testnetwork",
                            "channel": "#testchannel1",
                            "wantsIssues": false,
                            "wantsCommits": false,
                            "wantsPullRequests": false,
                            "wantsBranches": false
                        }, {
                            "network": "testnetwork",
                            "channel": "#testchannel2",
                            "wantsIssues": false,
                            "wantsCommits": false,
                            "wantsPullRequests": false,
                            "wantsBranches": false
                        }
                    ]
                },
                "https://github.com/test/test": {
                    "subscribers": [{
                            "network": "testnetwork",
                            "channel": "#testchannel3",
                            "wantsIssues": false,
                            "wantsCommits": false,
                            "wantsPullRequests": false,
                            "wantsBranches": false
                        }, {
                            "network": "testnetwork",
                            "channel": "#testchannel4",
                            "wantsIssues": false,
                            "wantsCommits": false,
                            "wantsPullRequests": false,
                            "wantsBranches": false
                        }
                    ]
                }
            }
        */

        assertEquals(desiredResult, storageManager.saveListeners(listeners));
    }

    @Test
    public void testLoad() {
        Listeners fromStorage = storageManager.loadListeners();


        File f = new File(database_file);
        f.delete();
    }

}
