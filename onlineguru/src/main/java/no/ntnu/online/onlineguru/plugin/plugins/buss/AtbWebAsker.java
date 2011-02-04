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
public class AtbWebAsker implements BusAsker {

    private static final String SCHEME = "http";
    private static final String ROOT_URL = "www.atb.no";
    private static final String ROOT_PATH = "/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=";

    private WebFetcher webFetcher;

    public AtbWebAsker(WebFetcher webFetcher) {
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

        return tryAskAtb(questionUrl);
    }

    private String tryAskAtb(String questionUrl) {
        try {
            String answer = webFetcher.get(questionUrl);
            answer = cleanAllDuplicateSpaces(answer);
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
            return "Klarte ikke å prate med bussorakelet, kanskje tjenesten er nede?";
        }
    }

    private String cleanAllDuplicateSpaces(String answer) {
        if (answer != null){
            return answer.replaceAll("\\s+", " ").trim();
        }
        return "";
    }

    private String urlEncode(String question) throws MalformedURLException, URISyntaxException {
            URI uri = new URI(SCHEME, ROOT_URL, ROOT_PATH + question, null);
            return uri.toString().replace("%3F", "?");
    }
}