package no.ntnu.online.onlineguru.plugin.plugins.np;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Håvard Slettvold
 */
public class NPPluginTest {

    NPPlugin np = new NPPlugin();

    @Test
    public void testCreateApikey() {
        String nick = "testnick";
        String apikey = np.createApikey(nick);

        assertEquals("1cf7cd3896b7f93f73e4f689d8fb780bdb90a0e7689a6a86d74e2ea41ab9e224", apikey);

    }
}
