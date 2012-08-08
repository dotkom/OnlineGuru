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
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1= createPrivMsgEvent("freenode", "Rockj", "#test", "o_O");
        PrivMsgEvent event2 = createPrivMsgEvent("freenode", "Rockj", "#test", "Du er en superhelt");
        PrivMsgEvent event3 = createPrivMsgEvent("freenode", "Rockj", "#test", "s/superhelt/idiot/");

        history.appendChannelHistory(channel, event1);
        history.appendChannelHistory(channel, event2);
        history.appendChannelHistory(channel, event3);

        assertNull(plugin.handleMessage("s/", createPrivMsgEvent("freenode", "fictive", "#test", "s/superhelt/idiot/")));
        assertEquals(String.format("<%s> %s", event2.getSender(), "Du er en idiot"), plugin.handleMessage("s/", createPrivMsgEvent("freenode", "Rockj", "#test", "s/superhelt/idiot/")));
        assertEquals(String.format("<%s> %s", event2.getSender(), "Du er en idiot"), plugin.handleMessage("troll/", createPrivMsgEvent("freenode", "trall", "#test", "troll/superhelt/idiot/")));
        assertEquals(String.format("<%s> %s", event1.getSender(), ":-)"), plugin.handleMessage("s/", createPrivMsgEvent("freenode", "Rockj", "#test", "s/o_O/:-)/")));
    }

    @Test
    public void testAgainstMetaReplacementTrolling() {
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "KinkyPinkie", "#test", "Rockj: det funker ikke" ));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "Rockj", "#test", "troll/funker ikke/fungerer jo utmerket som faen jo!/"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "Fl0bB", "#test", "troll/fungerer/lawl/"));
        // 16:33:00   @onlineguru | <Rockj> troll/funker ikke/lawl jo utmerket som faen jo!/
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "Fl0bB", "#test", "s/troll/gawd så meta/"));
        // 16:33:29   @onlineguru | <Fl0bB> gawd så meta/fungerer/lawl/

        assertNull(plugin.handleMessage("troll/",createPrivMsgEvent("freenode", "Fl0bB", "#test", "troll/fungerer/lawl/")));
        assertNull(plugin.handleMessage("s/", createPrivMsgEvent("freenode", "Fl0bB", "#test", "s/troll/gawd så meta/")));
        assertNull(plugin.handleMessage("s/", createPrivMsgEvent("freenode", "Fl0bB", "#test", "s/gawd/troll/")));
    }
}
