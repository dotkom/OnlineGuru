package no.ntnu.online.onlineguru.plugin.plugins.buss;

import no.ntnu.online.onlineguru.plugin.plugins.buss.HttpWebFetcher;
import no.ntnu.online.onlineguru.plugin.plugins.buss.WebFetcher;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class HttpWebFetcherTest {
    private static final String ATB_ORACLE_BASE_URL = "http://www.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=";

    @Test
    public void assertCanFetchBasedOnSimpleQuestion() throws IOException {
        WebFetcher fetcher = new HttpWebFetcher();

        String answer = fetcher.get(ATB_ORACLE_BASE_URL + "LOL");
        String expected = "LAWL";

        assertEquals(expected, answer);
    }
}
