package no.ntnu.online.onlineguru.utils;

import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.Url;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.expand;
import static com.rosaloves.bitlyj.Bitly.shorten;

/**
 * @author Håvard Slettvold
 */
public class URLShortener {

    private static final String settings_folder = "settings/";
    private static final String settings_file = settings_folder + "bitly.conf";

    private static String bitlyUsername;
    private static String bitlyApiKey;
    static Logger logger = Logger.getLogger(URLShortener.class);

    public static String bitlyfyLink(String link) {
        initiate();
        Url url;

        try {
            url = as(bitlyUsername, bitlyApiKey).call(shorten(link));
            return url.getShortUrl();
        } catch (BitlyException be) {
            logger.error("Failed to shorten URL.", be.getCause());
        }

        return "";
    }

    private static void initiate() {
        try {
            Map<String, String> settings = SimpleIO.loadConfig(settings_file);
            bitlyUsername = settings.get("username");
            bitlyApiKey = settings.get("apikey");
        } catch (IOException e) {
            logger.error("Config load failed.", e.getCause());
        }
    }

}
