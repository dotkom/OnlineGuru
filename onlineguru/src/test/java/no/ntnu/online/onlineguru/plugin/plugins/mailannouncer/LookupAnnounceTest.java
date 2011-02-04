package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * LookupAnnounce Tester.
 *
 * @author Roy Sindre Norangshol
 * @since <pre>11/14/2010</pre>
 * @version 1.0
 */
public class LookupAnnounceTest {

    @Test
    public void testGetLookup() throws Exception {
        String listId = "<dotkom.online.ntnu.no>";
        String email = "dotkom-svn@online.ntnu.no";

        assertEquals(listId, LookupAnnounce.getLookup("doesnt@matter.no", listId));
        assertEquals(listId, LookupAnnounce.getLookup(new String(listId), listId));
        assertEquals(listId, LookupAnnounce.getLookup(listId, listId));
        assertEquals(listId, LookupAnnounce.getLookup("", listId));
        assertEquals(email, LookupAnnounce.getLookup(email, null));
        assertEquals(email, LookupAnnounce.getLookup(email, ""));
    }

}
