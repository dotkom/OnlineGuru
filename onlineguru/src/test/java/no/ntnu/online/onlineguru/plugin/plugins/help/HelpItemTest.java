package no.ntnu.online.onlineguru.plugin.plugins.help;

import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Håvard Slettvold
 */
public class HelpItemTest {

    HelpItem hi1 = new HelpItem("!test", Flag.ANYONE, new String[]{"this is a test",});
    HelpItem hi2 = new HelpItem("!test", Flag.O, new String[]{"this is a different test",});

    ArrayList<HelpItem> helpItemArrayList = new ArrayList<HelpItem>();

    @Test
    public void testEqualsHelpItem() {
        assertTrue(hi1.equals(hi2));
    }

    @Test
    public void testArrayListContainsHelpItem() {
        helpItemArrayList.add(hi1);

        assertTrue(helpItemArrayList.contains(hi2));
    }
}
