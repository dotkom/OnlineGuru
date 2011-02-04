package no.ntnu.online.onlineguru.plugin.plugins.buss;

import no.ntnu.online.onlineguru.plugin.plugins.buss.BusAsker;
import no.ntnu.online.onlineguru.plugin.plugins.buss.BusTucWebAsker;
import no.ntnu.online.onlineguru.plugin.plugins.buss.WebFetcher;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Sep 18, 2010
 * Time: 11:38:52 PM
 */
public class BussTucWebAskerTest {
    private WebFetcher webFetcher;
    private BusAsker asker;
    private static final String BUSSTUC_BASE_URL = "http://www.idi.ntnu.no/~tagore/cgi-bin/busstuc/busq.cgi%3Fquest=";

    @Before
    public void setUp() throws Exception {
        webFetcher = mock(WebFetcher.class);
        asker = new BusTucWebAsker(webFetcher);
    }

    @Test
    public void ShouldAskTheCorrectUrl() throws IOException {
        asker.ask("");

        verify(webFetcher).get(BUSSTUC_BASE_URL);
    }

    @Test
    public void shouldAppendSimpleWordCorrectly() throws IOException {
        asker.ask("Hello");

        verify(webFetcher).get(BUSSTUC_BASE_URL + "Hello");
    }

    @Test
    public void shouldCorrectlyAppendAdvancedQuestions() throws IOException {
        asker.ask("Hello World!");

        verify(webFetcher).get(BUSSTUC_BASE_URL + "Hello%20World!");
    }

    @Test
    public void shouldCorrectlyExtractSingleLineAnswerFromHtml() throws IOException {
        when(webFetcher.get(BUSSTUC_BASE_URL+"Stub%201")).thenReturn(
                "<html> \n" +
                "<head> \n" +
                "<title>Answer from BusTUC</title> \n" +
                "</head> \n" +
                "<body> \n" +
                "Test</body>\n" +
                "</html> ");
        
        String answer = asker.ask("Stub 1");

        assertEquals("Test", answer);
    }

    @Test
    public void shouldStripNewLines() throws IOException {
        when(webFetcher.get(BUSSTUC_BASE_URL+"Stub%202")).thenReturn(
                "<html> \n" +
                "<head> \n" +
                "<title>Answer from BusTUC</title> \n" +
                "</head> \n" +
                "<body> \n" +
                "Hello \nWorld" +
                "</body> \n" +
                "</html>");

        String answer = asker.ask("Stub 2");

        String correctAnswer = "Hello World";
        assertEquals(correctAnswer, answer);
    }

    @Test
    public void shouldReplaceBreaksWithIrcNewlines() throws IOException {
        when(webFetcher.get(BUSSTUC_BASE_URL+"Stub%202")).thenReturn(
                "<html> \n" +
                "<head> \n" +
                "<title>Answer from BusTUC</title> \n" +
                "</head> \n" +
                "<body> \n" +
                "Hello <br> \n" +
                "World <br> \n" +
                "</body> \n" +
                "</html>");

        String answer = asker.ask("Stub 2");
        String correctAnswer = "Hello \r\n World";

        assertEquals(correctAnswer, answer);
    }

    @Test
    public void shouldCorrectlyExtractMultiLineAnswerFromHtml() throws IOException {
        when(webFetcher.get(BUSSTUC_BASE_URL+"Stub%203")).thenReturn(
                "<html> \n" +
                "<head> \n" +
                "<title>Answer from BusTUC</title> \n" +
                "</head> \n" +
                "<body> \n" +
                "18. Sep. 2010 er en  lørdag .<br>" +
                "Jeg antar du mener avganger fra ikveld.<br>" +
                "Jeg kan ikke finne flere ruteforbindelser på lørdag kl.  0045 .<br>" +
                "</body> \n" +
                "</html> ");

        String answer = asker.ask("Stub 3");
        String correctAnswer =
                "18. Sep. 2010 er en  lørdag .\r\n" +
                "Jeg antar du mener avganger fra ikveld.\r\n" +
                "Jeg kan ikke finne flere ruteforbindelser på lørdag kl.  0045 .";

        assertEquals(correctAnswer, answer);
    }
}

