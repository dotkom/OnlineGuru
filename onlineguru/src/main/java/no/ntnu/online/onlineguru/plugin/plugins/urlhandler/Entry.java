package no.ntnu.online.onlineguru.plugin.plugins.urlhandler;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.ntnu.online.onlineguru.exceptions.IncompliantCallerException;
import no.ntnu.online.onlineguru.utils.DecodeHtmlEntities;
import no.ntnu.online.onlineguru.utils.URLShortener;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.urlreader.impl.HTMLRetriever;
import no.ntnu.online.onlineguru.utils.urlreader.model.Retriever;
import no.ntnu.online.onlineguru.utils.urlreader.model.URLReader;
import org.apache.log4j.Logger;

import no.ntnu.online.onlineguru.utils.Timer;

public class Entry implements URLReader {

	static Logger logger = Logger.getLogger(Entry.class);
	
	private Wand wand;
    private PrivMsgEvent pme;
    private String url, title = "", shortLink = "";
		
	private int TIMEOUT = 0;
	private Timer timer;
	private boolean fetchedURLTitle = false;
    private boolean fetchedShortLink = false;
	private boolean isShortAlready = false;
	
	public Entry(Wand wand, PrivMsgEvent pme, String url) {
		this.wand = wand;
        this.pme = pme;
        this.url = url;

        // Start url source fetcher.
        try {
		    new HTMLRetriever(this, url);
        } catch (IncompliantCallerException e) {
            logger.error(e.getMessage(), e.getCause());
        }

        // If longer than 42 chars (youtube url is exactly 42)
		if(url.length() > 42) {
            shortLink = URLShortener.bitlyfyLink(url);
            fetchedShortLink = true;
		}
		else {
			isShortAlready = true;
		}

		timer = new Timer(this, "update", 100, true);
		timer.start();
	}
	
	public void urlReaderCallback(Retriever retriever) {
        HTMLRetriever hr = (HTMLRetriever)retriever;
		title = parseTitle(hr.getInString());
		fetchedURLTitle = true;
	}
	
	public void urlReaderCallback(Retriever retriever, Object[] parameters) {
        // not used
	}
	
	public void update() {
		TIMEOUT += 100;
		if(!isShortAlready) {
			if((fetchedURLTitle && fetchedShortLink) || TIMEOUT >= 6000) {
				timer.stopTimer();
				sendURLInformation();			
			}
		}
		else {
			if(fetchedURLTitle || TIMEOUT >= 6000) {
				timer.stopTimer();
				sendURLInformation();
			}
		}
	}
	
	private void sendURLInformation() {
		
		String returnString = "";
		
		if(!title.isEmpty()) {
			returnString += title;
		}
		
		if(!shortLink.isEmpty()) {
			if(!title.isEmpty()) {
                returnString += " - ";
            }
			returnString += shortLink;
			
		}
		
		if(!returnString.isEmpty()) {
			wand.sendMessageToTarget(pme.getNetwork(), pme.getTarget(), returnString);
		}
	}
	
	private String parseTitle(String source) {
		
		String sourceToLower = source.toLowerCase();
		int titleStart = sourceToLower.indexOf("<title>");
		int titleEnd = sourceToLower.indexOf("</title>");
		
		if(titleStart != -1 && titleEnd != -1) {
			return "«" + DecodeHtmlEntities.stripHTMLEntities(source.substring(titleStart + 7, titleEnd)) + "»";
		}
		
		return "";
	}

}