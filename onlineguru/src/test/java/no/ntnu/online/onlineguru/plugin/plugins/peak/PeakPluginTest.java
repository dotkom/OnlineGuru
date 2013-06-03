package no.ntnu.online.onlineguru.plugin.plugins.peak;

import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.nick.Nick;
import no.ntnu.online.onlineguru.helpers.EventFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The scope of these tests are limited to actions that are manually executed.
 * Updating the peakPlugin based on users joining the channel can only ever be tested if
 * the bot is running since I can't/won't mock the entire connection etc.
 *
 * @author Håvard Slettvold
 */
public class PeakPluginTest {

    PeakPlugin peakPlugin;
    FakeWand fakeWand;
    Network network;


    String channelName = "#somerandomtestchannelnamethatnoonewilleverpick";
    Channel testChannel;

    Nick me = new Nick("jabba");
    Nick testNick1 = new Nick("foo");
    Nick testNick2 = new Nick("bar");

    @Before
    public void setup() {
        peakPlugin = new PeakPlugin();

        // Make the network
        network = new Network();
        network.setServerAlias("freenode");
        ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
        networks.put(network.getServerAlias(), network);
        // The network makes the channel
        network.addChannel(channelName);
        testChannel = network.getChannel(channelName);
        ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
        channels.put(channelName, testChannel);

        fakeWand = new FakeWand(networks, channels);
        fakeWand.setNick("jabba");

        peakPlugin.addWand(fakeWand);

        // Simulate a nick joining the channel
        testChannel.addNick(me);
        JoinEvent je = EventFactory.createJoinEvent(network, testChannel.getChannelname(), "jabba");
        peakPlugin.incomingEvent(je);
    }

    @Test
    public void testNewChannelCount() {
        // Channel was joined by 1 nick, that nick was me. Count should be 1 and no update occurs.
        assertFalse(peakPlugin.updatePeakForChannel(network, testChannel));
        assertEquals(1, peakPlugin.getCount(network, testChannel));
    }

    @Test
    public void testAfterAddingNicksManually() {
        testChannel.addNick(testNick1);
        assertTrue(peakPlugin.updatePeakForChannel(network, testChannel));
        assertFalse(peakPlugin.updatePeakForChannel(network, testChannel));

        testChannel.addNick(testNick2);
        assertTrue(peakPlugin.updatePeakForChannel(network, testChannel));
        assertFalse(peakPlugin.updatePeakForChannel(network, testChannel));
    }
}
