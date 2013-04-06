package no.ntnu.online.onlineguru.plugin.plugins.flags;

import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.flags.DBHandler;
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

    DBHandler db = new DBHandler();

    Network network;
    Channel channel;


    @Before
    public void setup() {
        network = new Network();
        network.setServerAlias("thisIsATestNetworkAliasNameThing");

        File f = new File(db.getDBNetworkPath(network));
        assertFalse(f.exists());
    }

    @Test
    public void testDataBase() {
        assertNotNull(network);

        try {
            db.initiate(network);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Testing connection. If successful, the DB was created with no issues.
        Connection conn = db.connect(network);
        assertNotNull(conn);




        db.disconnect();
    }

    @Test
    public void cleanUp() {
        // Remove db file used for testing.
        File f = new File(db.getDBNetworkPath(network));
        if (f.exists()) {
            f.delete();
        }
    }
}
