package no.ntnu.online.onlineguru.plugin.plugins.middag;

import java.util.HashMap;
import java.util.regex.*;

import no.ntnu.online.onlineguru.utils.WandRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;

/**
 *
 * @author melwil
 */

public class Middag implements PluginWithDependencies {
	 
	private WandRepository wandRepository;
	private Help help;

	private DateTimeFormatter day = DateTimeFormat.forPattern("e");
	private DateTimeFormatter week = DateTimeFormat.forPattern("w");
	private DateTimeFormatter year = DateTimeFormat.forPattern("y");
	
	private String[] strDays = { "", "mandag", "tirsdag", "onsdag", "torsdag", "fredag", "lørdag", "søndag", };
	private int currentWeek = Integer.parseInt(week.print(new DateTime()));
	private int currentYear = Integer.parseInt(year.print(new DateTime()));
	private HashMap<Integer, Year> years = new HashMap<Integer, Year>();
	
	Pattern pagePattern = Pattern.compile(
			"class=\"ukedag\">([^<>]+)<" +									// finn dag (group 1)
		    "|" +															// eller	
			"\"menycelle\">([^<>]*).*?\"priscelle\">([^<>]*).*?" + 			// finn Menyen (group 2-3)
	        "|" +															// eller
	        "(Beklager, ingen meny er laget for uke (\\d{1,2}), (\\d{4}))"	// erroren (group 4-6)
	);
	Pattern triggerPattern = Pattern.compile("^!middag(?: (\\w+)(?: (\\d{1,2})(?: (\\d{4}))?)?)?$");
 
    public Middag() {
    	updateMenu(currentYear, currentWeek);
    	getOrCreateYear(currentYear).makeWeek(currentWeek);
    }
    
    public Year getOrCreateYear(int year) {
    	Year y = years.get(year);
    	if (y == null) {
    		y = new Year();
    		years.put(year, y);
    		return y;
    	}
    	else {
    		return y;
    	}    	
    }
    
	public void setMenu(int year, int week, String day, String kantine, String menu) {
		getOrCreateYear(year).setMenu(week, day, kantine, menu);
	}
	
	public void setWeekMenu(int year, int week, String kantine, String menu) {
		
	}
    
    /*
     * Metoder som arves fra PluginWithDependencies
     */

	public String getDescription() {
		return "This Plugin fetches the dinner-menues from Realfagskantina and Hangaren at NTNU Gløshaugen.";
	}

	public void incomingEvent(Event e) {
		switch (e.getEventType()) {
			case PRIVMSG:
				handlePrivMsgEvent((PrivMsgEvent)e);
				break;
		}
	}

	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.PRIVMSG);
	}

	public void addWand(WandRepository wandRepository) {
		this.wandRepository = wandRepository;
	}

	public String[] getDependencies() {
		return new String[] {"Help", };
	}

	public void loadDependency(Plugin plugin) {
		if (plugin instanceof Help) {
			this.help = (Help)plugin;
			help.addPublicTrigger("!middag");
			help.addPublicHelp("!middag", "!middag [day] - Display dinner menu for the cantinas Hangaren and Realfag. Optional argument [day] displays the menu for that day.");
		}
	}

	/*
	 * Triggerprosessering
	 */
	
	private void handlePrivMsgEvent(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		Matcher triggerMatcher = triggerPattern.matcher(pme.getMessage());
		
		if (triggerMatcher.find()) {
			String day = triggerMatcher.group(1) == null ? getStrDayOfWeek() : triggerMatcher.group(1).toLowerCase();
	    	
	    	int week = getWeek();
	    	int year = getYear();
	    	
	    	// Trengs for senere støtte om frem/tilbake i tid, trenger bare flere menyer for å teste.
//	    	int week = triggerMatcher.group(2) == null ? currentWeek : Integer.parseInt(triggerMatcher.group(2));
//	    	int year = triggerMatcher.group(3) == null ? currentYear : Integer.parseInt(triggerMatcher.group(3));
	    	
	    	Year y = years.get(year);
			if (y != null) {
				if (y.hasWeek(week)) {
					wandRepository.sendMessageToTarget(pme.getNetwork(), pme.getTarget(), "Hangaren: "+y.getMenu(week, day, "HANGAREN"));
					wandRepository.sendMessageToTarget(pme.getNetwork(), pme.getTarget(), "Realfag: "+y.getMenu(week, day, "REALFAG"));
				}
				else {
					updateMenu(year, week, day, pme);
				}
			}
			else {
				updateMenu(year, week, day, pme);
			}
		}
	}
		
	private String getStrDayOfWeek() {
		return strDays[Integer.parseInt(day.print(new DateTime()))];
	}
	
	private int getWeek() {
		return Integer.parseInt(week.print(new DateTime()));
	}
        
	private int getYear() {
		return Integer.parseInt(year.print(new DateTime()));
	}
	
	private void updateMenu(int year, int week) {
		updateMenu(year, week, "", null);
	}
	
	private void updateMenu(int year, int week, String day, PrivMsgEvent pme) {
		new UpdateMenu(this, "http://www.sit.no/content.ap?thisId=36444&visuke="+week+"&visaar="+year, week, year, "HANGAREN").setEvent(pme);
		new UpdateMenu(this, "http://www.sit.no/content.ap?thisId=36447&visuke="+week+"&visaar="+year, week, year, "REALFAG").setEvent(pme);
	}
	
}