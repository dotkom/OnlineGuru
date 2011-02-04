package no.ntnu.online.onlineguru.plugin.plugins.buss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Sep 18, 2010
 * Time: 11:56:15 PM
 */
public class BusTucWebAsker implements BusAsker {

    private static final String SCHEME = "http";
    private static final String ROOT_URL = "www.idi.ntnu.no";
    private static final String ROOT_PATH = "/~tagore/cgi-bin/busstuc/busq.cgi?quest=";

    private WebFetcher webFetcher;

    public BusTucWebAsker(WebFetcher webFetcher) {
        this.webFetcher = webFetcher;
    }

    public String ask(String question) {
        String questionUrl;
        try {
            questionUrl = urlEncode(question);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Spørsmålet ditt kunne ikke stilles. Sendte du med noen superrare tegn eller?";
        }

        return tryGetAndParseHtmlAnswer(questionUrl);
    }

    private String tryGetAndParseHtmlAnswer(String questionUrl) {
        try {
            String answerHtml = webFetcher.get(questionUrl);
            return getAnswerFromHtml(answerHtml);
        } catch (IOException e) {
            e.printStackTrace();
            return "Klarte ikke å prate med bussorakelet, kanskje tjenesten er nede?";
        }
    }

    private String urlEncode(String question) throws MalformedURLException, URISyntaxException {
            URI uri = new URI(SCHEME, ROOT_URL, ROOT_PATH + question, null);
            return uri.toString();
    }

    private String getAnswerFromHtml(String answerHtml) {
        if (answerHtml == null) {
            return "Fikk ikke et fornuftig svar tilbake... Kanskje busstuc er nede.";
        }

        int bodyStart = answerHtml.indexOf("<body>") + 6;
        int bodyEnd = answerHtml.indexOf("</body>") ;


        String answer = replaceBreaksWithIrcNewline(answerHtml.substring(bodyStart, bodyEnd).trim());
        return answer.trim();
    }

    private String replaceBreaksWithIrcNewline(String answerWithHtmlBreaks) {
        answerWithHtmlBreaks = answerWithHtmlBreaks.replace("\n", "");
        return answerWithHtmlBreaks.replace("<br>", "\r\n");
    }
}
