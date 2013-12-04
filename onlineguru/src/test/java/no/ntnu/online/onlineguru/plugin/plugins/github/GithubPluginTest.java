package no.ntnu.online.onlineguru.plugin.plugins.github;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.helpers.EventFactory;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Håvard Slettvold
 */
public class GithubPluginTest {

    private GithubPlugin githubPlugin;
    private Network network = new Network();
    private Wand wand = mock(Wand.class);
    private FlagsPlugin flagsPlugin = mock(FlagsPlugin.class);

    @Before
    public void setup() {
        githubPlugin = new GithubPlugin();
        githubPlugin.loadDependency(flagsPlugin);

        network.setServerAlias("testNetwork");
        githubPlugin.addWand(wand);

        when(flagsPlugin.getFlags(network, "#channel", "melwil")).thenReturn(new HashSet<Flag>(){{ add(Flag.a); add(Flag.A); }});
        when(wand.getNetworkByAlias("testNetwork")).thenReturn(network);
    }

    @Test
    public void testInPrivateWithoutSpecifyingChanenl() {
        // Test any operation trigger in private without channel
        assertEquals(
                "You must specify a channel when using this command in private messages.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github tes/test branches on"))
        );
        assertEquals(
                "You must specify a channel when using this command in private messages.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github tes/test"))
        );

    }

    @Test
    public void testInPrivateWithChanenl() {
        assertEquals("Announcing of branch creating and deletion for test/test in #channel turned on.", githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github https://github.com/test/test #channel branches on")));
        assertEquals("Subscriptions for #channel: branches", githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github https://github.com/test/test #channel")));
    }

    @Test
    public void testBranchName() {
        // Not a valid branch name, should fail.
        assertEquals("Unrecognized syntax. See !help !github for correct syntax.", githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "somenick", "!github test branches on")));



    }

    @Test
    public void testOperations() {
        // After turning all on, everything should return nothing updated
        assertEquals(
                "All annoucement triggers have been turned on for test/test in #channel.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel all on"))
        );
        assertEquals(
                "No subscriptions updated.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel branches on"))
        );
        assertEquals(
                "No subscriptions updated.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel commits on"))
        );
        assertEquals(
                "No subscriptions updated.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel issues on"))
        );
        assertEquals(
                "No subscriptions updated.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel pullrequests on"))
        );
        // Turn all off an check that they all return turned on
        assertEquals(
                "All annoucement triggers have been turned off for test/test in #channel.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel all off"))
        );
        assertEquals(
                "Announcing of branch creating and deletion for test/test in #channel turned on.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel branches on"))
        );
        assertEquals(
                "Announcing of commits for test/test in #channel turned on.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel commits on"))
        );
        assertEquals(
                "Announcing of issue activity for test/test in #channel turned on.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel issues on"))
        );
        assertEquals(
                "Announcing of pull request activity for test/test in #channel turned on.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel pullrequests on"))
        );
        assertEquals(
                "Invalid operation 'notvalidoperation'.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel notvalidoperation on"))
        );
    }

    @Test
    public void testInformation() {
        assertEquals(
                "Subscriptions for #channel: none",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel"))
        );
        // Adding issues subscription
        assertEquals(
                "Announcing of issue activity for test/test in #channel turned on.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel issues on"))
        );
        // Info should now show issues
        assertEquals(
                "Subscriptions for #channel: issues",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel"))
        );
        // Adding branches subscription
        assertEquals(
                "Announcing of branch creating and deletion for test/test in #channel turned on.",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "notinachannel", "!github test/test #channel branches on"))
        );
        // Info should now show branches and issues
        assertEquals(
                "Subscriptions for #channel: branches, issues",
                githubPlugin.handleCommand(EventFactory.createPrivMsgEvent(network, "melwil", "#channel", "!github test/test"))
        );
    }
}
