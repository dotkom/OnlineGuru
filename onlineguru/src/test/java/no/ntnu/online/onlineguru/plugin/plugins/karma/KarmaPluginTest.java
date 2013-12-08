package no.ntnu.online.onlineguru.plugin.plugins.karma;

import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.helpers.EventFactory;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Håvard Slettvold
 */
public class KarmaPluginTest {

    private KarmaPlugin karmaPlugin;
    private FlagsPlugin flagsPlugin = mock(FlagsPlugin.class);

    private Network network = mock(Network.class);
    private Channel channel = mock(Channel.class);

    @Before
    public void setup() {
        karmaPlugin = new KarmaPlugin();
        karmaPlugin.loadDependency(flagsPlugin);

        when(flagsPlugin.getFlags(network, "#channel", "nick")).thenReturn(EnumSet.allOf(Flag.class));
        when(network.getChannel("#channel")).thenReturn(channel);
        when(channel.isOnChannel("nick")).thenReturn(true);
        when(channel.isOnChannel("other")).thenReturn(true);
    }

    @Test
    public void testPunishment() {
        ResponseKarmaFind responseKarmaFind = karmaPlugin.findKarmaChange(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "nick++"));
        assertEquals(-5, responseKarmaFind.amount);
        assertEquals("nick", responseKarmaFind.sender);
        assertEquals("nick", responseKarmaFind.target);
    }

    @Test
    public void testIncreasingKarma() {
        ResponseKarmaFind responseKarmaFind = karmaPlugin.findKarmaChange(EventFactory.createPrivMsgEvent(network, "other", "#channel", "nick++"));
        assertEquals(1, responseKarmaFind.amount);
        assertEquals("other", responseKarmaFind.sender);
        assertEquals("nick", responseKarmaFind.target);
    }

    @Test
    public void testDecreasingKarma() {
        ResponseKarmaFind responseKarmaFind = karmaPlugin.findKarmaChange(EventFactory.createPrivMsgEvent(network, "other", "#channel", "nick--"));
        assertEquals(-1, responseKarmaFind.amount);
        assertEquals("other", responseKarmaFind.sender);
        assertEquals("nick", responseKarmaFind.target);
    }

    @Test
    public void testIncreaseMultiple() {
        ResponseKarmaFind responseKarmaFind = karmaPlugin.findKarmaChange(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "nick+=5"));
        assertEquals(-5, responseKarmaFind.amount);
        assertEquals("nick", responseKarmaFind.sender);
        assertEquals("nick", responseKarmaFind.target);

        responseKarmaFind = karmaPlugin.findKarmaChange(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "other+=5"));
        assertEquals(5, responseKarmaFind.amount);
        assertEquals("nick", responseKarmaFind.sender);
        assertEquals("other", responseKarmaFind.target);
    }

    @Test
    public void testDecreaseMultiple() {
        ResponseKarmaFind responseKarmaFind = karmaPlugin.findKarmaChange(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "nick-=10"));
        assertEquals(-5, responseKarmaFind.amount);
        assertEquals("nick", responseKarmaFind.sender);
        assertEquals("nick", responseKarmaFind.target);

        responseKarmaFind = karmaPlugin.findKarmaChange(EventFactory.createPrivMsgEvent(network, "nick", "#channel", "other-=5"));
        assertEquals(-5, responseKarmaFind.amount);
        assertEquals("nick", responseKarmaFind.sender);
        assertEquals("other", responseKarmaFind.target);
    }
}
