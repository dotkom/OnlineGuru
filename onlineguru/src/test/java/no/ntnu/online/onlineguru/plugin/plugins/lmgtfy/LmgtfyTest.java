package no.ntnu.online.onlineguru.plugin.plugins.lmgtfy;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * Author: Nina Margrethe Smørsgård
 * GitHub: https://github.com/NinaMargrethe/
 * Date: 10/18/11
 */
public class LmgtfyTest {

    List<String> badSearchTerms;
    private LmgtfyPlugin plugin;
    private static final String BASE = "http://lmgtfy.com/?q=";

    @Before
    public void setupSearchTerms() {

        plugin = new LmgtfyPlugin();

        badSearchTerms = new ArrayList<String>(Arrays.asList(
                "spaceAfterTerm ",
                " spaceBeforeTerm",
                " spaceBeforeAndAfterTerm ",
                " "));
    }

    @Test
    public void generateLmgtfyLinkTest() {
        String link = plugin.generateLmgtfyLink(badSearchTerms);
        //System.out.println(String.format("->%s<-", link));

        assertEquals(link.substring(0, BASE.length()), BASE);

        assertFalse("link contains empty term", link.charAt(link.length() - 1) == '+');

        assertFalse(link.contains(" "));

        String terms = link.substring(BASE.length(), link.length());
        assertEquals(3, terms.split("\\+").length);
    }
}
