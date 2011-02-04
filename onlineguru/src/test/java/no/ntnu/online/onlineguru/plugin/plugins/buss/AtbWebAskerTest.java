package no.ntnu.online.onlineguru.plugin.plugins.buss;

import no.ntnu.online.onlineguru.plugin.plugins.buss.AtbWebAsker;
import no.ntnu.online.onlineguru.plugin.plugins.buss.BusAsker;
import no.ntnu.online.onlineguru.plugin.plugins.buss.WebFetcher;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Sep 18, 2010
 * Time: 11:38:52 PM
 */
public class AtbWebAskerTest {
    private WebFetcher webFetcher;
    private BusAsker asker;
    private static final String ATB_ORACLE_BASE_URL = "http://www.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=";

    @Before
    public void setUp() throws Exception {
        webFetcher = mock(WebFetcher.class);
        asker = new AtbWebAsker(webFetcher);
    }

    @Test
    public void ShouldAskTheCorrectUrl() throws IOException {
        asker.ask("");

        verify(webFetcher).get(ATB_ORACLE_BASE_URL);
    }

    @Test
    public void shouldAppendSimpleWordCorrectly() throws IOException {
        asker.ask("Hello");

        verify(webFetcher).get(ATB_ORACLE_BASE_URL + "Hello");
    }

    @Test
    public void shouldCorrectlyAppendAdvancedQuestions() throws IOException {
        asker.ask("Hello World!");

        verify(webFetcher).get(ATB_ORACLE_BASE_URL + "Hello%20World!");
    }

    @Test
    public void shouldReplaceDoubleSpaceWithSingleSpace() throws IOException {
        when(webFetcher.get(ATB_ORACLE_BASE_URL+"Stub%201")).thenReturn(
                "  Hello  World  How Are  You Doing?");

        String answer = asker.ask("Stub 1");
        String expected = "Hello World How Are You Doing?";

        assertEquals(expected, answer);
    }

    @Test
    public void shouldReplaceMultipleSpaceWithSingleSpace() throws IOException {
        when(webFetcher.get(ATB_ORACLE_BASE_URL+"Stub%201")).thenReturn(
                "  Hello  World      How Are   You Doing?");

        String answer = asker.ask("Stub 1");
        String expected = "Hello World How Are You Doing?";

        assertEquals(expected, answer);
    }
}

