package no.ntnu.online.onlineguru.plugin.plugins.history;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.PartEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.nick.Nick;
import no.ntnu.online.onlineguru.utils.history.History;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static no.fictive.irclib.TestHelperFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Roy Sindre Norangshol
 */
public class HistoryTest {

    private History history;
    private Nick fictive;
    private Nick rockj   ;
    private Nick melwil;
    private Channel channel;

    @Before
    public void setUp() {
        history = new History();

        rockj = new Nick("Rockj");
        fictive = new Nick("Fictive");
        melwil = new Nick("melwil");

        history.appendHistory(rockj, createJoinEvent("freenode", "#test", rockj.getNickname()));
        history.appendHistory(fictive, createJoinEvent("freenode", "#test", fictive.getNickname()));
        history.appendHistory(melwil, createJoinEvent("freenode", "#test", melwil.getNickname()));

        history.appendHistory(rockj, createPrivMsgEvent("freenode", rockj.getNickname(), "#test", "This is a trolling message, what's up?!" ));
        history.nickChangeHistory(createNickEvent("freenode", fictive.getNickname(), "fictiveLulz"));
        fictive.setNickname("fictiveLulz");
        for (int i=0; i < 3; i++) {
            history.appendHistory(melwil, createPrivMsgEvent("freenode", melwil.getNickname(), "#test", "Flood "+i+"!"));
        }

        // Channel history here
        channel = new Channel("#test");

        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", rockj.getNickname(), channel.getChannelname(), "Message 1"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", rockj.getNickname(), channel.getChannelname(), "Message 2"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", rockj.getNickname(), channel.getChannelname(), "Message 3"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", rockj.getNickname(), channel.getChannelname(), "Message 4"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", fictive.getNickname(), channel.getChannelname(), "Message 5"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", rockj.getNickname(), channel.getChannelname(), "Message 6"));
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", rockj.getNickname(), channel.getChannelname(), "Message 7"));
    }

    @Test
    public void testLastActionByRockj() {
        Event event = history.getLastEvents(rockj).get(0);
        assertEquals((PrivMsgEvent)event, event);
        PrivMsgEvent privMsgEvent = (PrivMsgEvent)event;

        assertEquals("This is a trolling message, what's up?!", privMsgEvent.getMessage());
        assertEquals(2, history.getLastEvents(rockj).size());
    }

    @Test
    public void testIfHistoryStaysWithUsAfterNickChange() {
        Nick copyOfFictive = new Nick(fictive.getNickname());
        copyOfFictive.setNickname("fictive");

        assertEquals(0, history.getLastEvents(copyOfFictive).size());
        assertEquals(2, history.getLastEvents(fictive).size());
    }

    @Test
    public void testIfHistoryDoesntHoldMoreEventsThenMaxConstant() {
        assertEquals(4, history.getLastEvents(melwil).size());
        history.appendHistory(melwil, createPrivMsgEvent("freenode", melwil.getNickname(), "#test", "hoho now at max"));

        List<Event> melwilsHistoryAfter5Changes = history.getLastEvents(melwil);
        System.out.println(Arrays.toString(melwilsHistoryAfter5Changes.toArray()));
        assertEquals(History.MAX_EVENTS_IN_HISTORY_PER_NICK, melwilsHistoryAfter5Changes.size());
        assertEquals("hoho now at max", ((PrivMsgEvent)melwilsHistoryAfter5Changes.get(0)).getMessage());
        assertTrue(melwilsHistoryAfter5Changes.get(History.MAX_EVENTS_IN_HISTORY_PER_NICK-1) instanceof JoinEvent);
        assertEquals("#test", ((JoinEvent) melwilsHistoryAfter5Changes.get(History.MAX_EVENTS_IN_HISTORY_PER_NICK-1)).getChannel());

        history.appendHistory(melwil, createPartEvent("freenode", "#test", melwil.getNickname(), "troll"));
        assertEquals(History.MAX_EVENTS_IN_HISTORY_PER_NICK, history.getLastEvents(melwil).size());
        assertEquals("hoho now at max", ((PrivMsgEvent)history.getLastEvents(melwil).get(1)).getMessage());
        assertTrue(history.getLastEvents(melwil).get(0) instanceof PartEvent);
        assertEquals("#test", ((PartEvent) history.getLastEvents(melwil).get(0)).getChannel());
    }

    @Test
    public void testIfHistoryChannelMaxLimit() {
        assertEquals(7, history.getLastChannelEvents(channel).size());
        assertEquals("Message 1", history.getLastChannelEvents(channel).get(history.getLastChannelEvents(channel).size()-1).getMessage());

        for (int i=history.getLastChannelEvents(channel).size(); i < History.MAX_EVENTS_IN_CHANNEL_HISOTRY; i++) {
            history.appendChannelHistory(channel, createPrivMsgEvent("freenode", melwil.getNickname(), channel.getChannelname(), "Flood "+i));
        }

        assertEquals(History.MAX_EVENTS_IN_CHANNEL_HISOTRY, history.getLastChannelEvents(channel).size());
        assertEquals("Message 1", history.getLastChannelEvents(channel).get(history.getLastChannelEvents(channel).size()-1).getMessage());
        history.appendChannelHistory(channel, createPrivMsgEvent("freenode", melwil.getNickname(), channel.getChannelname(), "newLineAtTop"));
        assertEquals(History.MAX_EVENTS_IN_CHANNEL_HISOTRY, history.getLastChannelEvents(channel).size());
        assertEquals("Message 2", history.getLastChannelEvents(channel).get(history.getLastChannelEvents(channel).size()-1).getMessage());
        assertEquals("newLineAtTop", history.getLastChannelEvents(channel).get(0).getMessage());
    }
}
