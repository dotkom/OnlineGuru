package no.ntnu.online.onlineguru.plugin.plugins.buss;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Sep 19, 2010
 * Time: 1:36:35 AM
 *
 * TODO:
 * This class is so generic it should be moved out of buss integration.
 * It does hoever not handle bad HTML such as from tagore's busstuc.
 *
 * Currently the buss implementation is using Atb which returns clean data,
 * so I haven't gotten around to fixing this class yet.
 */
public class HttpWebFetcher implements WebFetcher {
    private static final String END_OF_INPUT = "\\Z";

    public String get(String url) throws IOException {
        String content;
        URLConnection connection =  new URL(url).openConnection();
        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter(END_OF_INPUT);
        content = scanner.next();

        return content;
    }
}
