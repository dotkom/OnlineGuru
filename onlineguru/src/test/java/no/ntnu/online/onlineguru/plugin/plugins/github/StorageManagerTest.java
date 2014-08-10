package no.ntnu.online.onlineguru.plugin.plugins.github;

import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.GithubCallbackListener;
import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.GithubCallbackListeners;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Håvard Slettvold
 */
public class StorageManagerTest {

    private String database_file = "database/github_storage_test.db";
    private StorageManager storageManager = new StorageManager(database_file);

    private GithubCallbackListeners githubCallbackListeners = new GithubCallbackListeners();

    @Test
    public void testSave() {

        GithubCallbackListener cl1 = new GithubCallbackListener();
        githubCallbackListeners.put("https://github.com/moo/moo", cl1);

        cl1.getOrCreateSubscription("testnetwork", "#testchannel1");
        cl1.getOrCreateSubscription("testnetwork", "#testchannel2");

        GithubCallbackListener cl2 = new GithubCallbackListener();
        githubCallbackListeners.put("https://github.com/test/test", cl2);

        cl2.getOrCreateSubscription("testnetwork", "#testchannel3");
        cl2.getOrCreateSubscription("testnetwork", "#testchannel4");

        String desiredResult = "{\"githubCallbackListeners\":{\"https://github.com/moo/moo\":{\"announceSubscriptions\":[{\"network\":\"testnetwork\",\"channel\":\"#testchannel1\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false},{\"network\":\"testnetwork\",\"channel\":\"#testchannel2\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false}],\"wantedActions\":[\"reopened\",\"closed\",\"opened\"]},\"https://github.com/test/test\":{\"announceSubscriptions\":[{\"network\":\"testnetwork\",\"channel\":\"#testchannel3\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false},{\"network\":\"testnetwork\",\"channel\":\"#testchannel4\",\"wantsIssues\":false,\"wantsCommits\":false,\"wantsPullRequests\":false,\"wantsBranches\":false}],\"wantedActions\":[\"reopened\",\"closed\",\"opened\"]}}}";

        /* Better formatted;
            {
               "githubCallbackListeners":{
                  "https://github.com/moo/moo":{
                     "announceSubscriptions":[
                        {
                           "network":"testnetwork",
                           "channel":"#testchannel1",
                           "wantsIssues":false,
                           "wantsCommits":false,
                           "wantsPullRequests":false,
                           "wantsBranches":false
                        },
                        {
                           "network":"testnetwork",
                           "channel":"#testchannel2",
                           "wantsIssues":false,
                           "wantsCommits":false,
                           "wantsPullRequests":false,
                           "wantsBranches":false
                        }
                     ],
                     "wantedActions":[
                        "closed",
                        "opened"
                     ]
                  },
                  "https://github.com/test/test":{
                     "announceSubscriptions":[
                        {
                           "network":"testnetwork",
                           "channel":"#testchannel3",
                           "wantsIssues":false,
                           "wantsCommits":false,
                           "wantsPullRequests":false,
                           "wantsBranches":false
                        },
                        {
                           "network":"testnetwork",
                           "channel":"#testchannel4",
                           "wantsIssues":false,
                           "wantsCommits":false,
                           "wantsPullRequests":false,
                           "wantsBranches":false
                        }
                     ],
                     "wantedActions":[
                        "closed",
                        "opened",
                        "reopened"
                     ]
                  }
               }
            }
        */

        assertEquals(desiredResult, storageManager.saveListeners(githubCallbackListeners));
    }

    @Test
    public void testLoad() {
        GithubCallbackListeners fromStorage = storageManager.loadListeners();


        File f = new File(database_file);
        f.delete();
    }

}
