package no.ntnu.online.onlineguru.plugin.plugins.middag;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.ntnu.online.onlineguru.utils.urlreader.URLReader;
import no.ntnu.online.onlineguru.utils.urlreader.URLReaderUser;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class UpdateMenu implements URLReaderUser {

    static Logger logger = Logger.getLogger(UpdateMenu.class);
	private Middag middag;
	private int week = 0;
	private int year = 0;
	private String kantine;
	private PrivMsgEvent event;
	
	private Pattern pagePattern = Pattern.compile(
			"class=\"ukedag\">([^<>]+)<" +									// finn dag (group 1)
		    "|" +															// eller	
			"\"menycelle\">([^<>]*).*?\"priscelle\">([^<>]*).*?" + 			// finn Menyen (group 2-3)
	        "|" +															// eller
	        "(Beklager, ingen meny er laget for uke (\\d{1,2}), (\\d{4}))"	// erroren (group 4-6)
	);
	
	public UpdateMenu(Middag middag, String url, int week, int year, String kantine) {
		this.middag = middag;
		this.week = week;
		this.year = year;
		this.kantine = kantine;

		new URLReader(this, url);
	}
	
	public void setEvent(PrivMsgEvent e) {
		this.event = e;
	}
	
	public void urlReaderCallback(URLReader urlr) {
		String weeksMenu = urlr.getInString();  
		Matcher pageMatcher = pagePattern.matcher(weeksMenu);
		
		String day = "";
		String dayMenu = "";

        boolean found = false;	
		while (pageMatcher.find()) {
            /*
            Enable to show group contents.

            for (int i = 1; i <= pageMatcher.groupCount(); i++) {
        		System.out.print(i+" \""+pageMatcher.group(i)+"\" ");
        	}
        	System.out.print("\n");

        	*/
        	if (pageMatcher.group(1) != null) {
        		if (day.isEmpty()) {
        			logger.debug("Day was null, setting it for the first time.");
        			day = pageMatcher.group(1); 
        		}
        		else {
        			logger.debug("Day was already set, Adding day menu or resetting.");
        			if (dayMenu.equals("")) {
        				middag.setMenu(year, week, day, kantine, "Det er ikke satt noen meny for "+day.toLowerCase()+" i "+kantine.toLowerCase()+" i uke "+week+", "+year+".");
        			}
        			else {
        				middag.setMenu(year, week, day, kantine, dayMenu);
        				dayMenu = "";
        			}
        			day = pageMatcher.group(1);
        		}
        	}
        	else if (pageMatcher.group(4) != null) {
        		logger.debug("No menu set for week "+week+". Setting error weekmessage for '"+kantine+"'.");
        		middag.setWeekMenu(year, week, kantine,
        				"SiT har ikke laget noen meny for "+kantine.toLowerCase()+" i uke "+pageMatcher.group(5)+", "+pageMatcher.group(6)+".");
        	}
        	else {
        		logger.debug("Adding menu item for day: "+day+"! ");
        		
        		if (pageMatcher.group(2) != null) {
        			if (!dayMenu.equals("")) {
        				dayMenu += ", ";
        			}
        			dayMenu += pageMatcher.group(2).trim();
        			if (pageMatcher.group(3) != null) {
        				if (!pageMatcher.group(3).trim().equals("")) {
	        				dayMenu += " - " + pageMatcher.group(3).trim().replaceAll("\\,\\-", "") + " kr"; 
        				}
        			}
        		}
        	}
            found = true;
        }
		if(!found){
            logger.debug("No match found. "+week+"/"+year);
        }
		else {
        	// Sets the meny for friday, since it will not be caught in the while loop.
        	if (dayMenu.equals("")) {
				middag.setMenu(year, week, day, kantine, "Det er ikke satt noen meny for "+day.toLowerCase()+" i "+kantine.toLowerCase()+" i uke "+week+", "+year+".");
    		}
			else {
				middag.setMenu(year, week, day, kantine, dayMenu);
				dayMenu = "";
			}
        }
		
		if (event != null) {
			middag.incomingEvent(event);
		}
	}

	public void urlReaderCallback(URLReader urlReader,
			Object[] callbackParameters) {
		// TODO Auto-generated method stub

	}

	
}