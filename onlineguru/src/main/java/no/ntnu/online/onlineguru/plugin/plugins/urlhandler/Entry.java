package no.ntnu.online.onlineguru.plugin.plugins.urlhandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Timer;
import no.ntnu.online.onlineguru.utils.urlreader.URLReader;
import no.ntnu.online.onlineguru.utils.urlreader.URLReaderUser;

public class Entry implements URLReaderUser {

	static Logger logger = Logger.getLogger(Entry.class);
	
	private Wand wand;
	private Network network;
	private String target;
	
	private String sourceURL = "";
	private String sourceTinyURL = "";
	
	private Pattern tinyUrlPattern = Pattern.compile("<br><small>\\[<a\\s+href=\"(.*?)\"");
	private static String tinyURLpart1 = "http://tinyurl.com/create.php?source=homepage&url=";
	private static String tinyURLpart2 = "&submit=Make+TinyURL!&alias=";
	
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
		
		if(url.length() > 42) { 
			String encodedURL = encodeURL(url);
			if(encodedURL != null) {
				new URLReader(this, encodedURL, new Object[] {"TinyURL"});
			}
		}
		else {
			isShortAlready = true;
		}
		
		timer = new Timer(this, "update", 100, true);
		timer.start();
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
		String tinyUrl = parseTinyURL(sourceTinyURL);
		
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
	
	private String parseTinyURL(String source) {
		
		Matcher matcher = tinyUrlPattern.matcher(source);
		if(matcher.find()) {
			return matcher.group(1);
		}
		return "";     //TODO bitly
	}
	
	private String encodeURL(String url) {
		try {
			URI uri = new URI(tinyURLpart1 + url + tinyURLpart2);
			return uri.toString();
		} catch (URISyntaxException e) {
			logger.error("Entry.encodeURL TinyURL", e.getCause());
		}
		return null;
	}
}