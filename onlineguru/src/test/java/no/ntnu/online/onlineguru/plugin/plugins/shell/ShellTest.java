package no.ntnu.online.onlineguru.plugin.plugins.shell;


import no.fictive.irclib.HelperFactory;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.FakeWand;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * todo please mock this :|
 */
public class ShellTest {
    private ShellPlugin plugin;
    private List<String> messageObserverMessages;
    private Network network;

    @Before
    public void setUp() {
        messageObserverMessages = new ArrayList<String>();
        MessageObserver messageObserver = new MessageObserver() {
            public void deliverMessage(Network network, String target, String message) {
                messageObserverMessages.add(message);
            }
        };
        network = new Network();
        network.setServerAlias("freenode");
        ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
        ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
        plugin = new ShellPlugin(new FakeWand(networks, channels), messageObserver);
    }

    /*
    @Test
    public void testUptime() {
        plugin.incomingEvent(HelperFactory.createPrivMsgEvent("freenode", "Rockj", ShellPlugin.ADMIN_CHANNEL, "!shell uptime"));
        try {
            Thread.sleep(10L);

            assertNotNull(messageObserverMessages);
            assertEquals(1, messageObserverMessages.size());
            assertTrue(messageObserverMessages.get(0).contains("load average"));
        } catch (InterruptedException e) {

        }
    }
    */
}
