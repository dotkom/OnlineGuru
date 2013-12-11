package no.ntnu.online.onlineguru.plugin.plugins.seen;

import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.nick.Nick;
import no.ntnu.online.onlineguru.plugin.plugins.history.HistoryPlugin;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.service.services.history.History;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static no.fictive.irclib.HelperFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Roy Sindre Norangshol
 */
public class SeenTest {

    private History history = new History();
    private Nick rockj;
    private Nick fictive;
    private Nick melwil;
    private SeenPlugin seenPlugin;
    private Wand mockWand = mock(Wand.class);
    private HistoryPlugin mockHistoryPlugin = mock(HistoryPlugin.class);


    @Before
    public void setUp() {
        Network network = new Network();
        network.setServerAlias("freenode");
        ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
        ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
        channels.put("#test", new Channel("#test"));

        seenPlugin = new SeenPlugin();
        seenPlugin.addWand(mockWand);

        when(mockHistoryPlugin.getHistory()).thenReturn(history);
        seenPlugin.loadDependency(mockHistoryPlugin);

        rockj = new Nick("Rockj");
        fictive = new Nick("Fictive");
        melwil = new Nick("melwil");

        history.appendHistory(rockj, createJoinEvent("freenode", "#test", rockj.getNickname()));
        history.appendHistory(fictive, createJoinEvent("freenode", "#test", fictive.getNickname()));
        history.appendHistory(melwil, createJoinEvent("freenode", "#test", melwil.getNickname()));

        history.appendHistory(rockj, createPrivMsgEvent("freenode", rockj.getNickname(), "#test", "This is a trolling message, what's up?!"));
        history.nickChangeHistory(createNickEvent("freenode", fictive.getNickname(), "fictiveLulz"));
        fictive.setNickname("fictiveLulz");
        for (int i = 0; i < 3; i++) {
            history.appendHistory(melwil, createPrivMsgEvent("freenode", melwil.getNickname(), "#test", "Flood " + i + "!"));
        }


    }

    @Test
    public void testLastActionByNicks() {
        assertEquals("This is a trolling message, what's up?!", ((PrivMsgEvent) history.getLastEvents(rockj).get(0)).getMessage());
        assertEquals("fictiveLulz", ((NickEvent) history.getLastEvents(fictive).get(0)).getNewNick());
        assertEquals("Flood 2!", ((PrivMsgEvent) history.getLastEvents(melwil).get(0)).getMessage());
    }

    @Test
    public void testLastActionByNicksWithWand() {
        assertEquals("Rockj sent a message to #test and said: This is a trolling message, what's up?!", seenPlugin.handleSeenQuery(createPrivMsgEvent("freenode", melwil.getNickname(), "#test", "!seen Rockj")));
        assertEquals("fictiveLulz changed nick from Fictive", seenPlugin.handleSeenQuery(createPrivMsgEvent("freenode", melwil.getNickname(), "#test", "!seen fictiveLulz")));
        assertEquals("melwil sent a message to #test and said: Flood 2!", seenPlugin.handleSeenQuery(createPrivMsgEvent("freenode", fictive.getNickname(), "#test", "!seen melwil")));
    }

    @Test
    public void testLastMessageFromAnotherChannel() {
        assertEquals("Rockj was seen talking in #test", seenPlugin.handleSeenQuery(createPrivMsgEvent("freenode", melwil.getNickname(), "#someotherchannel", "!seen Rockj")));
    }

}
