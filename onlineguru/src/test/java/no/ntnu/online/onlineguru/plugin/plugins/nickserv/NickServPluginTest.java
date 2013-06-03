package no.ntnu.online.onlineguru.plugin.plugins.nickserv;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.command.ConnectEvent;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.NumericEvent;
import no.fictive.irclib.event.container.command.QuitEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Wand;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * @author Håvard Slettvold
 */


public class NickServPluginTest {

    String myNick = "OnlineGuru";

    NickServPlugin ns;
    Network network;
    IRCEventPacket testPackage;


    public IRCEventPacket makePacket(String rawline) {
        return new IRCEventPacket(rawline);
    }

    @Test
    public void checkForValidAddingOfAuthedNick() {
        simulateSetUp();
        simulateAuthedJoin();

        assertTrue(ns.isAuthed(network, "Authed"));
        assertEquals(ns.getUsername(network, "Authed"), "Yes");

    }

    private void simulateSetUp() {
        // Setup a network and a wand
        network = new Network();
        network.setServerAlias("freenode");
        ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
        ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

        Wand fakeWand = new FakeWand(networks, channels);

        // Make NickServPlugin and a Network instance
        ns = new NickServPlugin();
        ns.addWand(fakeWand);

        // Simulate a connect to a server. This will make an AuthHandler.
        ns.incomingEvent(new ConnectEvent(network));
    }

    private void simulateAuthedJoin() {
        // Fake a packet for NUMERIC 354
        testPackage = makePacket(":server 354 test Authed Yes");
        ns.incomingEvent(new NumericEvent(testPackage, network));
    }

    @Test
    public void checkForValidAddingOfNonAuthedUsers() {
        simulateSetUp();
        simulateNonAuthedJoin();

        assertFalse(ns.isAuthed(network, "NonAuthed"));
        assertNull(ns.getUsername(network, "NonAuthed"));
    }

    private void simulateNonAuthedJoin() {
        testPackage = makePacket(":server 354 test NonAuthed 0");
        ns.incomingEvent(new NumericEvent(testPackage, network));
    }

    @Test
    public void checkNickChanges() {
        simulateSetUp();
        simulateAuthedJoin();
        simulateNickChange();

        assertEquals(ns.getUsername(network, "NewNick"), "Yes");
    }

    private void simulateNickChange() {
        // Fake a packet for nick change
        testPackage = makePacket(":Authed!ident@hostname.com NICK :NewNick");
        ns.incomingEvent(new NickEvent(testPackage, network));
    }

    @Test
    public void checkQuit() {
        simulateSetUp();
        simulateAuthedJoin();
        simulateQuit();

        assertNull(ns.getUsername(network, "Authed"));
        assertFalse(ns.isAuthed(network, "Authed"));
    }

    private void simulateQuit() {
        // Fake a packet for quit
        testPackage = makePacket(":Authed!ident@hostname.com QUIT :Some reason");
        ns.incomingEvent(new QuitEvent(testPackage, network));
    }
}
