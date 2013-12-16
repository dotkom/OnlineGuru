package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.helpers.EventFactory;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Håvard Slettvold
 */
public class MailAnnouncerPluginTest {

    private MailAnnouncerPlugin mailAnnouncerPlugin;

    private Wand wand = mock(Wand.class);
    private Network network = mock(Network.class);
    private FlagsPlugin flagsPlugin = mock(FlagsPlugin.class);

    @Before
    public void setup() {
        mailAnnouncerPlugin = new MailAnnouncerPlugin();
        mailAnnouncerPlugin.loadDependency(flagsPlugin);

        mailAnnouncerPlugin.addWand(wand);

        when(network.getServerAlias()).thenReturn("testNetwork");
        when(flagsPlugin.getFlags(network, "#channel", "melwil")).thenReturn(new HashSet<Flag>(){{ add(Flag.a); add(Flag.A); }});
        when(wand.getNetworkByAlias("testNetwork")).thenReturn(network);
    }

    @Test
    public void testCreateSubscriptionInPublic() {
        assertEquals(
                "#channel is now subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist on"))
        );
        assertEquals(
                "#channel is already subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist on"))
        );
    }

    @Test
    public void testCreateSubscriptionInPrivate() {
        assertEquals(
                "#channel is now subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel on"))
        );
        assertEquals(
                "#channel is already subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel on"))
        );
        assertEquals(
                "You must specify a channel when using this command in private messages.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist on"))
        );
    }

    @Test
    public void testDeleteSubsciptionInPublic() {
        assertEquals(
                "#channel is not subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist off"))
        );
        mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist on"));
        assertEquals(
                "#channel is no longer subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist off"))
        );
    }

    @Test
    public void testDeleteSubsciptionInPrivate() {
        assertEquals(
                "#channel is not subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel off"))
        );
        mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel on"));
        assertEquals(
                "#channel is no longer subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel off"))
        );
    }

    @Test
    public void testInfoInPublic() {
        assertEquals(
                "#channel is not subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist"))
        );
        mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist on"));
        assertEquals(
                "#channel is subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!mail testlist"))
        );
    }

    @Test
    public void testInfoInPrivate() {
        assertEquals(
                "#channel is not subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel"))
        );
        mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel on"));
        assertEquals(
                "#channel is subscribed to 'testlist'.",
                mailAnnouncerPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!mail testlist #channel"))
        );
    }
}
