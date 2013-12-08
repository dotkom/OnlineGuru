package no.ntnu.online.onlineguru.plugin.plugins.karma;

import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.helpers.EventFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Håvard Slettvold
 */
public class KarmaPluginTest {

    private KarmaPlugin karmaPlugin;
    private Network network = mock(Network.class);
    private Channel channel = mock(Channel.class);

    @Before
    public void setup() {
        karmaPlugin = new KarmaPlugin();

        when(network.getChannel("#channel")).thenReturn(channel);
        when(channel.isOnChannel("nick")).thenReturn(true);
    }

    @Test
    public void testPunishment() {
        assertEquals(
                "nick's karma has been reduced to -5 for trying to cheat.",
                karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "nick++"))
        );
        assertEquals(
                "nick's karma is -5.",
                karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "!karma"))
        );
        assertEquals(
                "nick's karma is -5.",
                karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "!karma nick"))
        );
    }

    @Test
    public void testIncreasingKarma() {
        karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "nick++"));
        assertEquals(
                "nick's karma is 1.",
                karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "!karma nick"))
        );
        karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "nick++"));
        assertEquals(
                "nick's karma is 2.",
                karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "!karma nick"))
        );
    }

    @Test
    public void testDecreasingKarma() {
        karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "nick--"));
        assertEquals(
                "nick's karma is -1.",
                karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "!karma nick"))
        );
        karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "nick--"));
        assertEquals(
                "nick's karma is -2.",
                karmaPlugin.handlePrivmsg(EventFactory.createPrivMsgEvent(network, "other", "#channel", "!karma nick"))
        );
    }
}
