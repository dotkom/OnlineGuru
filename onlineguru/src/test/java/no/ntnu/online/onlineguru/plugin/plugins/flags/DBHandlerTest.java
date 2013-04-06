package no.ntnu.online.onlineguru.plugin.plugins.flags;

import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.helpers.EventFactory;
import no.ntnu.online.onlineguru.plugin.plugins.flags.DBHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import static org.junit.Assert.*;

/**
 * @author Håvard Slettvold
 */
public class DBHandlerTest {

    DBHandler db;

    Network network;
    Channel channel;

    String user1 = "user1";
    String pass1 = "pass1";
    String user2 = "user2";
    String pass2 = "pass2";


    @Before
    public void setup() {
        db = new DBHandler();

        network = new Network();
        network.setServerAlias("thisIsATestNetworkAliasNameThing");
        channel = new Channel("#hurrdurr");

        File f = new File(db.getDBNetworkPath(network));
        assertFalse(f.exists());
    }

    /*
     * Yes, this test case is huge, but I want to remove the db-file after testing,
     * and Maven really isn't fond of TestSuites and TestClasses.
     * http://i.imgur.com/PruNGJ1.gif
     */
    @Test
    public void testDataBase() {
        assertNotNull(network);

        try {
            db.initiate(network);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Testing connection. The DB was created with no issues.
        Connection conn = db.connect(network);
        assertNotNull(conn);

        // Try to make a new superuser. Table exists and checks work.
        assertTrue(db.createSuperuser(network, user1, pass1));
        assertTrue(db.isSuperuser(network, user1));

        // Try do delete a superuser. The delete method works.
        assertTrue(db.removeSuperuser(network, user1));
        assertFalse(db.isSuperuser(network, user1));

        // Create channel.
        assertTrue(db.createChannel(EventFactory.createJoinEvent(network, channel.getChannelname(), user1)));
        assertTrue(db.userExistsInChannel(network, channel.getChannelname(), user1));

        // Update someones flags.
        assertTrue(db.setFlags(network, channel.getChannelname(), user1, "oO"));

        // Check if the flags are correctly retrieved.
        assertEquals("oO", db.getFlags(network, channel.getChannelname(), user1));

        db.disconnect();
    }

    @After
    public void cleanUp() {
        // Remove db file used for testing.
        File f = new File(db.getDBNetworkPath(network));
        if (f.exists()) {
            f.delete();
        }
    }
}
