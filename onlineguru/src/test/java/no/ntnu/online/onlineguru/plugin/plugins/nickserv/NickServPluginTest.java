package no.ntnu.online.onlineguru.plugin.plugins.nickserv;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.command.ConnectEvent;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.NumericEvent;
import no.fictive.irclib.event.container.command.QuitEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Wand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Håvard Slettvold
 */


public class NickServPluginTest {

    String myNick = "OnlineGuru";

    NickServPlugin nickServ;
    IRCEventPacket testPackage;

    Network mockNetwork;
    Wand mockWand;

    public IRCEventPacket makePacket(String rawline) {
        return new IRCEventPacket(rawline);
    }

    @Before
    public void setup() {
        mockNetwork = mock(Network.class);
        mockWand = mock(Wand.class);

        // Make NickServPlugin and a Network instance
        nickServ = new NickServPlugin();
        nickServ.addWand(mockWand);

        // Simulate a connect to a server. This will make an AuthHandler.
        nickServ.incomingEvent(new ConnectEvent(mockNetwork));
    }

    @Test
    public void checkForValidAddingOfAuthedNick() {
        simulateAuthedJoin();

        assertTrue(nickServ.isAuthed(mockNetwork, "Authed"));
        assertEquals(nickServ.getUsername(mockNetwork, "Authed"), "Yes");

    }

    private void simulateAuthedJoin() {
        // Fake a packet for NUMERIC 354
        testPackage = makePacket(":server 354 test Authed Yes");
        nickServ.incomingEvent(new NumericEvent(testPackage, mockNetwork));
    }

    @Test
    public void checkForValidAddingOfNonAuthedUsers() {
        simulateNonAuthedJoin();

        assertFalse(nickServ.isAuthed(mockNetwork, "NonAuthed"));
        assertNull(nickServ.getUsername(mockNetwork, "NonAuthed"));
    }

    private void simulateNonAuthedJoin() {
        testPackage = makePacket(":server 354 test NonAuthed 0");
        nickServ.incomingEvent(new NumericEvent(testPackage, mockNetwork));
    }

    @Test
    public void checkNickChanges() {
        simulateAuthedJoin();
        simulateNickChange();

        assertEquals(nickServ.getUsername(mockNetwork, "NewNick"), "Yes");
    }

    private void simulateNickChange() {
        // Fake a packet for nick change
        testPackage = makePacket(":Authed!ident@hostname.com NICK :NewNick");
        nickServ.incomingEvent(new NickEvent(testPackage, mockNetwork));
    }

    @Test
    public void checkQuit() {
        simulateAuthedJoin();
        simulateQuit();

        assertNull(nickServ.getUsername(mockNetwork, "Authed"));
        assertFalse(nickServ.isAuthed(mockNetwork, "Authed"));
    }

    private void simulateQuit() {
        // Fake a packet for quit
        testPackage = makePacket(":Authed!ident@hostname.com QUIT :Some reason");
        nickServ.incomingEvent(new QuitEvent(testPackage, mockNetwork));
    }

}
