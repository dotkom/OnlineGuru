package no.ntnu.online.onlineguru.plugin.plugins.regex;


import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.nickserv.FakeWand;
import no.ntnu.online.onlineguru.utils.history.History;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static no.fictive.irclib.HelperFactory.createPrivMsgEvent;
import static org.junit.Assert.*;


/**
 * @author Roy Sindre Norangshol
 */
public class RegexTest {
    private RegexPlugin plugin;
    private FakeWand fakeWand;
    private History history;
    private Channel channel;

    @Before
    public void setUp() {
               Network network = new Network();
        network.setServerAlias("freenode");
        ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
        ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
        channel = new Channel("#test");
        channels.put("#test", channel);

        fakeWand = new FakeWand(networks, channels);

        history = new History();
        plugin = new RegexPlugin(fakeWand, history);
    }

    @Test
    public void testSimpleRegexReplacement() {
        PrivMsgEvent event = createPrivMsgEvent("freenode", "Rockj", "#test", "Du er en superhelt");
        history.appendChannelHistory(channel, event);

        assertNull(plugin.handleMessage("s/", createPrivMsgEvent("freenode", "fictive", "#test", "s/superhelt/idiot/")));
        assertEquals(String.format("<%s> %s", event.getSender(), "Du er en idiot"), plugin.handleMessage("s/", createPrivMsgEvent("freenode", "Rockj", "#test", "s/superhelt/idiot/")));
        assertEquals(String.format("<%s> %s", event.getSender(), "Du er en idiot"), plugin.handleMessage("troll/", createPrivMsgEvent("freenode", "trall", "#test", "troll/superhelt/idiot/")));
    }
}
