package no.ntnu.online.onlineguru.plugin.plugins.rssannounce;

import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.WandRepository;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Roy Sindre Norangshol
 * http://www.roysindre.no
 * <p/>
 * Date: 11/30/10
 * Time: 4:35 PM
 */
public class RssChecker implements Runnable {
    static Logger logger = Logger.getLogger(RssChecker.class);
    private Thread thread;
    private DateTime lastUpdated;
    private String lastTitleEntry;
    private Abdera abdera;
    private final String urlToPull = "http://wiki.java.no/createrssfeed.action?types=blogpost&sort=modified&showContent=true&showDiff=true&spaces=javabin&labelString=tirsdagsm%f8te&rssType=atom&maxResults=10&timeSpan=5&publicFeed=true&title=Tirsdagsm%f8te";
    private Parser parser;
    private WandRepository wand;
    private int refreshEveryMinute = 15;
    private final String DB = RssPlugin.DB_FOLDER + "lastupdated.db";


    public RssChecker() {
        try {
            String configLastUpdated = SimpleIO.readFileAsString(DB);
            if (configLastUpdated != null && !configLastUpdated.equalsIgnoreCase("")) {
                lastUpdated = new DateTime(configLastUpdated);
            } else {
                lastUpdated = new DateTime("2000-11-30T14:19:42Z");
            }
        } catch (IOException e) {
            lastUpdated = new DateTime("2000-11-30T14:19:42Z");
        }


        this.wand = wand;
        this.abdera = new Abdera();
        thread = new Thread(this, "RssChecker");
        thread.start();
    }

    public RssChecker(WandRepository wand) {
        try {
            String configLastUpdated = SimpleIO.readFileAsString(DB);
            if (configLastUpdated != null && !configLastUpdated.equalsIgnoreCase("")) {
                lastUpdated = new DateTime(configLastUpdated);
            } else {
                lastUpdated = new DateTime("2000-11-30T14:19:42Z");
            }
        } catch (IOException e) {
            lastUpdated = new DateTime("2000-11-30T14:19:42Z");
        }


        this.wand = wand;
        this.abdera = new Abdera();
        thread = new Thread(this, "RssChecker");
        thread.start();
    }


    public void run() {

        try {
            while (true) {
                // 1 Second * Seconds * Minutes
                Thread.sleep(1000 * 60 * refreshEveryMinute);
                checkRss();

            }
        } catch (InterruptedException e) {
            // Ignore
        }

    }

    public void checkRss() {
        try {
            URL url = null;
            //logger.debug("Checking for new RSS updates");
            parser = abdera.getParser();
            url = new URL(urlToPull);
            Document<Feed> doc = parser.parse(url.openStream());
            Feed feed = doc.getRoot();
            //logger.debug(feed.getTitle());
            DateTime newestUpdatedTime = lastUpdated;
            for (Entry entry : feed.getEntries()) {
                //logger.debug(entry.getTitle());
                DateTime entryLastUpdated = new DateTime(entry.getUpdatedElement().getString());
                //logger.debug(String.format("lastUpdated: %s , entryLastUpdated: %s", lastUpdated.toString(), entryLastUpdated.toString()));
                //logger.debug(String.format("lastUpdated: %s , entryLastUpdated: %s", lastUpdated.toInstant(), entryLastUpdated.toInstant()));
                if (lastUpdated.isBefore(entryLastUpdated.toInstant())) {
                    if (newestUpdatedTime.isBefore(entryLastUpdated.toInstant())) {
                        newestUpdatedTime = entryLastUpdated;
                    }
                    String toAnnounce = String.format("[RSS] %s (%s) %s", entry.getTitle(), entry.getAuthor().getName(), entry.getLinks().get(0).getHref().toASCIIString());
                    logger.debug(toAnnounce);
                    publishToIRC(toAnnounce);
                }

            }
            if (!lastUpdated.isEqual(newestUpdatedTime)) {
                lastUpdated = newestUpdatedTime;
                SimpleIO.writeToFile(DB, lastUpdated.toString());
                logger.debug("Updated lastUpdated timestamp for RSS Plugin");
            }
        } catch (MalformedURLException e) {
            logger.error(e.getCause());
        } catch (IOException e) {
            logger.error(e.getCause());
        }
    }

    /**
     * TODO Implement to save which networks and channels it should do announces to for which feeds. Hardcoded for now.
     *
     * @param message
     */

    private void publishToIRC(String message) {
        wand.sendMessageToTarget(wand.getNetworkByAlias("efnet"), "#java.no", message);
    }

    public static void main(String[] args) {
        new RssChecker();
    }
}
