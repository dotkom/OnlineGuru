package no.ntnu.online.onlineguru.plugin.plugins.urlhandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rosaloves.bitlyj.Url;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Timer;
import no.ntnu.online.onlineguru.utils.urlreader.URLReader;
import no.ntnu.online.onlineguru.utils.urlreader.URLReaderUser;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

public class Entry implements URLReaderUser {

	static Logger logger = Logger.getLogger(Entry.class);
	
	private Wand wand;
	private Network network;
	private String target;
	
	private String sourceURL = "";
	private String sourceTinyURL = "";

    private final String settings_folder = "settings/";
    //using the same api-key for bit.ly as the lmgtfy plugin
    private final String settings_file = settings_folder + "lmgtfy.conf";
    private String bitlyUsername;
    private String bitlyApiKey;

	private int TIMEOUT = 0;
	private Timer timer;
	private boolean fetchedURLSource = false;
	private boolean fetchedTinyURLSource = false;
	private boolean isShortAlready = false;
	
	public Entry(Wand wand, Network network, String url, String target) {
		this.wand = wand;
		this.network = network;
		this.target = target;
		
		new URLReader(this, url);

        initiate();

		if(url.length() > 42) {
            bitlyfyLink(url);
		}
		else {
			isShortAlready = true;
		}
		
		timer = new Timer(this, "update", 100, true);
		timer.start();
	}

    private void initiate() {
        try {
            bitlyUsername = SimpleIO.loadConfig(settings_file).get("username");
            bitlyApiKey = SimpleIO.loadConfig(settings_file).get("apikey");
        } catch (IOException e) {
            logger.error(e);
        }
    }
	
	public void urlReaderCallback(URLReader urlReader) {
		sourceURL = urlReader.getInString();
		fetchedURLSource = true;
	}
	
	public void urlReaderCallback(URLReader urlReader, Object[] parameters) {
		sourceTinyURL = urlReader.getInString();
		fetchedTinyURLSource = true;
	}
	
	public void update() {
		TIMEOUT += 100;
		if(!isShortAlready) {
			if((fetchedURLSource && fetchedTinyURLSource) || TIMEOUT >= 6000) {
				timer.stopTimer();
				sendURLInformation();			
			}
		}
		else {
			if(fetchedURLSource || TIMEOUT >= 6000) {
				timer.stopTimer();
				sendURLInformation();
			}
		}
	}
	
	private void sendURLInformation() {
		
		String returnString = "";
		String title = parseTitle(sourceURL);
		String tinyUrl = bitlyfyLink(sourceTinyURL);
		
		if(!title.isEmpty()) {
			returnString += title;
		}
		
		if(!tinyUrl.isEmpty()) {
			if(!title.isEmpty()) returnString += " - ";
			returnString += tinyUrl;
			
		}
		
		if(!returnString.isEmpty()) {
			wand.sendMessageToTarget(network, target, returnString);
		}
	}
	
	private String parseTitle(String source) {
		
		String sourceToLower = source.toLowerCase();
		int titleStart = sourceToLower.indexOf("<title>");
		int titleEnd = sourceToLower.indexOf("</title>");
		
		if(titleStart != -1 && titleEnd != -1) {
			return "«" + source.substring(titleStart + 7, titleEnd) + "»";
		}
		
		return "";
	}

    public String bitlyfyLink(String link) {
        Url url = as(bitlyUsername, bitlyApiKey).call(shorten(link));
        return url.getShortUrl();
    }
}