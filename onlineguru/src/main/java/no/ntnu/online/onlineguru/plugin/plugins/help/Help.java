package no.ntnu.online.onlineguru.plugin.plugins.help;

import java.util.ArrayList;
import java.util.HashMap;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.WandRepository;
import org.apache.log4j.Logger;

/**
*
* @author melwil
*/

public class Help implements Plugin {
    static Logger logger = Logger.getLogger(Help.class);
	
	private WandRepository wandRepository;
	private ArrayList<String> triggers = new ArrayList<String>(); 
	private HashMap<String, String> publicHelp = new HashMap<String, String>();

	public String getDescription() {
		return "Provides information about OnlineGuru and it's commands";
	}

	public void incomingEvent(Event e) {
		if (e.getEventType() == EventType.PRIVMSG) {
			handleMessage(e);
		}
	}

	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.PRIVMSG);
	}

	public void addWand(WandRepository wandRepository) {
		this.wandRepository = wandRepository;
	}
	
	private void handleMessage(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		String message = pme.getMessage();
		String sender = pme.getSender();
		Network network = pme.getNetwork();
		
		if (message.split(" ")[0].equals("!help") || message.split(" ")[0].equals("!hjelp") || message.split(" ")[0].equals("??")) {

			String helptrigger = message.replaceAll("^[^\\s]+\\s?", "");

			// If there is no argument other than the help trigger, display all the basic triggers.
			if (helptrigger.isEmpty()) {
				
				wandRepository.sendNoticeToTarget(network, sender, "Here is a list of available triggers, use !help <trigger> for more info;");

				String output = "";
								
				for (String trigger : triggers) {
					output += trigger+" ";
					if (output.length() > 100) {
						output = output.trim();
						output = output.replaceAll("\\s+", ", ");
						wandRepository.sendNoticeToTarget(network, sender, output);
						output = "";
					}
				}
				if (!output.isEmpty()) {
					output = output.trim();
					output = output.replaceAll("\\s+", ", ");
					wandRepository.sendNoticeToTarget(network, sender, output);;
				}
			}
			// Otherwise, display help text for the supplied trigger
			else {
				// Find the help item
				if (publicHelp.containsKey(helptrigger)) {
					wandRepository.sendNoticeToTarget(network, sender, publicHelp.get(helptrigger));
				}
				else {
					wandRepository.sendNoticeToTarget(network, sender, "No help item for '" + helptrigger + "'");
				}
				
				// Send message to server
			}
		}
	}
	
	/*
	 * Public methods for Help
	 */
	public void addPublicTrigger(String helpTrigger) {
		if (triggers.contains(helpTrigger)) {
            logger.error(String.format("Another trigger already exists for %s", helpTrigger));
		}
		else {
			triggers.add(helpTrigger.toLowerCase());
		}
	}
	public void addPublicHelp(String helpTrigger, String helpText) {
		if (publicHelp.containsKey(helpTrigger)) {
            logger.error(String.format("Another help trigger already exists for %s", helpTrigger));
		}
		else {
			publicHelp.put(helpTrigger.toLowerCase(), helpText);
		}
	}
}
