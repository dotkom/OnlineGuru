package no.ntnu.online.onlineguru.plugin.plugins.regex;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.nickserv.FakeWand;
import no.ntnu.online.onlineguru.utils.history.History;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.fictive.irclib.HelperFactory.createPrivMsgEvent;
import static org.junit.Assert.*;


/**
 * @author Roy Sindre Norangshol
 * @author Håvard Slettvold
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

        assertEquals("[sed] Found no match to your search.", plugin.handleSed(createPrivMsgEvent("freenode", "fictive", "#test", "s/superhelt/idiot/")));
        assertEquals(String.format("[sed] <%s> %s", event2.getSender(), "Du er en idiot"), plugin.handleSed(createPrivMsgEvent("freenode", "Rockj", "#test", "s/superhelt/idiot/")));
        assertEquals(String.format("[sed] <%s> %s", event1.getSender(), ":-)"), plugin.handleSed(createPrivMsgEvent("freenode", "Rockj", "#test", "s/o_O/:-)/")));
    }

    @Test
    public void testAgainstMetaReplacementTrolling() {
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "KinkyPinkie", "#test", "Rockj: det funker ikke" ));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "Rockj", "#test", "troll/funker ikke/fungerer jo utmerket som faen jo!/"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "Fl0bB", "#test", "troll/fungerer/lawl/"));
        // 16:33:00   @onlineguru | <Rockj> troll/funker ikke/lawl jo utmerket som faen jo!/
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", "Fl0bB", "#test", "s/troll/gawd så meta/"));
        // 16:33:29   @onlineguru | <Fl0bB> gawd så meta/fungerer/lawl/

        assertNull(plugin.handleSed(createPrivMsgEvent("freenode", "Fl0bB", "#test", "troll/fungerer/lawl/")));
        assertEquals("[sed] <Fl0bB> gawd så meta/fungerer/lawl/", plugin.handleSed(createPrivMsgEvent("freenode", "Fl0bB", "#test", "s/troll/gawd så meta/")));
        assertEquals("[sed] Found no match to your search.", plugin.handleSed(createPrivMsgEvent("freenode", "Fl0bB", "#test", "s/gawd/troll/")));
    }

    @Test
    public void testEscapedSeparator() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1= createPrivMsgEvent("freenode", "melwil", "#test", "Dette Er eN Te/st fo/r ignore case flag.");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                "[sed] <melwil> Dette Er eN <omg>r ignore case flag.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/Te\\/st fo\\//<omg>/"))
        );
        assertEquals(
                "[sed] <melwil> Dette Er eN Test fo/r ignore case flag.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/\\///"))
        );
    }

    @Test
    public void testIgnoreCaseFlag() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1= createPrivMsgEvent("freenode", "melwil", "#test", "Dette Er eN Test for ignore case flag.");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                "[sed] <melwil> Det der var eN Test for ignore case flag.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/Dette Er/Det der var/"))
        );
        assertEquals(
                "[sed] <melwil> Det der var eN Test for ignore case flag.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/Dette Er/Det der var/"))
        );
        assertEquals(
                "[sed] Found no match to your search.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/dette er/Det der var/"))
        );
        assertEquals(
                "[sed] <melwil> Det der var eN Test for ignore case flag.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/dette er/Det der var/i"))
        );
    }

    @Test
    public void testReplaceAllFlag() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1= createPrivMsgEvent("freenode", "melwil", "#test", "tester tester tester replace all.");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                "[sed] <melwil> virker tester tester replace all.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/tester/virker/"))
        );
        assertEquals(
                "[sed] <melwil> virker virker virker replace all.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/tester/virker/g"))
        );
    }

    @Test
    public void testNthReplace() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1= createPrivMsgEvent("freenode", "melwil", "#test", "tekst foran 1dog 2dog 3dog 4dog 5dog 6dog noe text blabla");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                "[sed] <melwil> tekst foran 1dog 2virker 3dog 4dog 5dog 6dog noe text blabla",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/dog/virker/2"))
        );
        assertEquals(
                "[sed] <melwil> tekst foran 1dog 2dog 3virker 4virker 5virker 6virker noe text blabla",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/dog/virker/3g"))
        );
    }

    @Test
    public void testTooLongReplacement() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1= createPrivMsgEvent("freenode", "melwil", "#test", "1234567890");
        PrivMsgEvent event2= createPrivMsgEvent("freenode", "melwil", "#test", "12345678901");

        history.appendChannelHistory(channel, event1);

        // Should return a string of length 400
        assertEquals(
                "[sed] <melwil> 12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890",
                        plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/./1234567890123456789012345678901234567890/g"))
        );

        history.appendChannelHistory(channel, event2);
        // Should not return a string of length > 400
        assertEquals(
                "[sed] ERROR: Replaced pattern was longer than 400 characters.",
                plugin.handleSed(createPrivMsgEvent("freenode", "melwil", "#test", "s/./1234567890123456789012345678901234567890/g"))
        );

    }

}
