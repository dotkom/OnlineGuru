package no.ntnu.online.onlineguru.plugin.plugins.regex;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.helpers.EventFactory;
import no.ntnu.online.onlineguru.plugin.plugins.history.HistoryPlugin;
import no.ntnu.online.onlineguru.service.services.history.History;
import no.ntnu.online.onlineguru.utils.Wand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * @author Roy Sindre Norangshol
 * @author Håvard Slettvold
 */
public class RegexTest {
    private RegexPlugin regexPlugin;
    private HistoryPlugin mockHistoryPlugin = mock(HistoryPlugin.class);
    private Channel channel = new Channel("#test");
    private Network mockNetwork = mock(Network.class);
    private Wand mockWand = mock(Wand.class);

    private History history = new History();

    @Before
    public void setUp() {
        regexPlugin = new RegexPlugin();
        regexPlugin.addWand(mockWand);

        when(mockHistoryPlugin.getHistory()).thenReturn(history);
        regexPlugin.loadDependency(mockHistoryPlugin);
    }


    @Test
    public void testSimpleRegexReplacement() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "Rockj", "#test", "o_O");
        PrivMsgEvent event2 = EventFactory.createPrivMsgEvent(mockNetwork, "Rockj", "#test", "Du er en superhelt");
        PrivMsgEvent event3 = EventFactory.createPrivMsgEvent(mockNetwork, "Rockj", "#test", "s/superhelt/idiot/");

        history.appendChannelHistory(channel, event1);
        history.appendChannelHistory(channel, event2);
        history.appendChannelHistory(channel, event3);

        assertEquals(
                null, 
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "fictive", "#test", "s/superhelt/idiot/"))
        );
        assertEquals(String.format(
                "<%s> %s", event2.getSender(), "Du er en idiot"), 
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "Rockj", "#test", "s/superhelt/idiot/"))
        );
        assertEquals(String.format(
                "<%s> %s", event1.getSender(), ":-)"), 
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "Rockj", "#test", "s/o_O/:-)/"))
        );
    }

    @Test
    public void testAgainstMetaReplacementTrolling() {
        history.appendChannelHistory(channel, EventFactory.createPrivMsgEvent(mockNetwork, "KinkyPinkie", "#test", "Rockj: det funker ikke"));
        history.appendChannelHistory(channel, EventFactory.createPrivMsgEvent(mockNetwork, "Rockj", "#test", "troll/funker ikke/fungerer jo utmerket som faen jo!/"));
        history.appendChannelHistory(channel, EventFactory.createPrivMsgEvent(mockNetwork, "Fl0bB", "#test", "troll/fungerer/lawl/"));
        // 16:33:00   @onlineguru | <Rockj> troll/funker ikke/lawl jo utmerket som faen jo!/
        history.appendChannelHistory(channel, EventFactory.createPrivMsgEvent(mockNetwork, "Fl0bB", "#test", "s/troll/gawd så meta/"));
        // 16:33:29   @onlineguru | <Fl0bB> gawd så meta/fungerer/lawl/

        assertNull(regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "Fl0bB", "#test", "troll/fungerer/lawl/")));
        assertEquals("<Fl0bB> gawd så meta/fungerer/lawl/", regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "Fl0bB", "#test", "s/troll/gawd så meta/")));
        assertEquals(null, regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "Fl0bB", "#test", "s/gawd/troll/")));
    }

    @Test
    public void testEscapedSeparator() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "Dette Er eN Te/st fo/r escaped# separator.");
        PrivMsgEvent event2 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "Nå bare tes\\ter vi.");

        history.appendChannelHistory(channel, event1);
        history.appendChannelHistory(channel, event2);

        assertEquals(
                "<melwil> Dette Er eN <omg>r escaped# separator.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/Te\\/st fo\\//<omg>/"))
        );
        assertEquals(
                "<melwil> Dette Er eN Test fo/r escaped# separator.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/\\///"))
        );
    }

    @Test
    public void testIgnoreCaseFlag() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "Dette Er eN Test for ignore case flag.");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                "<melwil> Det der var eN Test for ignore case flag.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/Dette Er/Det der var/"))
        );
        assertEquals(
                "<melwil> Det der var eN Test for ignore case flag.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/Dette Er/Det der var/"))
        );
        assertEquals(
                null,
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/dette er/Det der var/"))
        );
        assertEquals(
                "<melwil> Det der var eN Test for ignore case flag.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/dette er/Det der var/i"))
        );
    }

    @Test
    public void testReplaceAllFlag() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "tester tester tester replace all.");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                "<melwil> virker tester tester replace all.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/tester/virker/"))
        );
        assertEquals(
                "<melwil> virker virker virker replace all.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/tester/virker/g"))
        );
    }

    @Test
    public void testNthReplace() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "tekst foran 1dog 2dog 3dog 4dog 5dog 6dog noe text blabla");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                "<melwil> tekst foran 1dog 2virker 3dog 4dog 5dog 6dog noe text blabla",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/dog/virker/2"))
        );
        assertEquals(
                "<melwil> tekst foran 1dog 2dog 3virker 4virker 5virker 6virker noe text blabla",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/dog/virker/3g"))
        );
    }

    @Test
    public void testTooLongReplacement() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "1234567890");
        PrivMsgEvent event2 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "12345678901");

        history.appendChannelHistory(channel, event1);

        // Should return a string of length 400
        assertEquals(
                "<melwil> 12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/./1234567890123456789012345678901234567890/g"))
        );

        history.appendChannelHistory(channel, event2);
        // Should not return a string of length > 400
        assertEquals(
                null,
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/./1234567890123456789012345678901234567890/g"))
        );

    }

    @Test
    public void testMatchingGroupsReplace() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "her er en enkel to test");

        history.appendChannelHistory(channel, event1);

        assertEquals(
                null,
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/en/'$1'/"))
        );
        assertEquals(
                "<melwil> her er 'en' enkel to test",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/(en)/'$1'/"))
        );
        assertEquals(
                "<melwil> her er 'en'' enkel to test",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/(en)|(to)/'$1'$2'/"))
        );
        assertEquals(
                "<melwil> her er 'en'' 'en''kel ''to' test",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/(en)|(to)/'$1'$2'/g"))
        );

    }

    @Test
    public void testVerboseFlag() {
        assertEquals(0, history.getLastChannelEvents(channel).size());
        PrivMsgEvent event1 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "her er en enkel to test");
        PrivMsgEvent event2 = EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "12345678901");

        history.appendChannelHistory(channel, event1);

        // Testing incompliable regex
        assertEquals(
                null,
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "hurr", "#test", "s/+//"))
        );
        assertEquals(
                "The Regular Expression pattern could not be compiled.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "hurr", "#test", "s/+//v"))
        );

        // Testing no matches found
        assertEquals(
                null,
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "hurr", "#test", "s/doesn't/exist/"))
        );
        assertEquals(
                "Found no match to your search.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "hurr", "#test", "s/doesn't/exist/v"))
        );

        // Testing illegal groups
        assertEquals(
                null,
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/en/'$1'/"))
        );
        assertEquals(
                "No group 1. Define the matching group.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/en/'$1'/v"))
        );

        history.appendChannelHistory(channel, event2);

        // Should not return a string of length > 400, but with verbose should provide feedback.
        assertEquals(
                null,
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/./1234567890123456789012345678901234567890/g"))
        );
        assertEquals(
                "ERROR: Replaced pattern was longer than 400 characters.",
                regexPlugin.handleSed(EventFactory.createPrivMsgEvent(mockNetwork, "melwil", "#test", "s/./1234567890123456789012345678901234567890/gv"))
        );

    }

}
