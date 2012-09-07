package no.ntnu.online.onlineguru.plugin.plugins.help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

/**
* @author melwil
*/

public class Help implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(Help.class);
	
	private Wand wand;
    private FlagsPlugin flags;

    private Map<String, Flag> triggers = new HashMap<String, Flag>();

	private ArrayList<String> trigs = new ArrayList<String>();
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

	public void addWand(Wand wand) {
		this.wand = wand;
	}

    @Override
    public String[] getDependencies() {
        return new String[]{"Flags", };  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) {
            this.flags = (FlagsPlugin) plugin;
        }
    }

	private void handleMessage(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		String message = pme.getMessage();
		String sender = pme.getSender();
		Network network = pme.getNetwork();

		if (message.split(" ")[0].equals("!help") || message.split(" ")[0].equals("!hjelp") || message.split(" ")[0].equals("??")) {

			String helptrigger = message.replaceAll("^[^\\s]+\\s?", "");

			// If there is no argument other than the help trigger, display all the basic trigs.
			if (helptrigger.isEmpty()) {

				wand.sendMessageToTarget(network, sender, "Here is a list of available trigs, use !help <trigger> for more info;");

				String output = "";

				for (String trigger : trigs) {
                    if (!output.isEmpty()) {
                        output += ", ";
                    }
					output += trigger;
					if (output.length() > 100) {
						wand.sendMessageToTarget(network, sender, output);
						output = "";
					}
				}
				if (!output.isEmpty()) {
					wand.sendMessageToTarget(network, sender, output);;
				}
			}
			// Otherwise, display help text for the supplied trigger
			else {
				// Find the help item
				if (publicHelp.containsKey(helptrigger)) {
					wand.sendMessageToTarget(network, sender, publicHelp.get(helptrigger));
				}
				else {
					wand.sendMessageToTarget(network, sender, "No help item for '" + helptrigger + "'");
				}

				// Send message to server
			}
		}
	}

	/*
	 * Public methods for Help
	 */
    public void addTrigger(String helpTrigger, Flag flag) {
        if (triggers.containsKey(helpTrigger)) {
            logger.error(String.format("Another trigger already exists for %s", helpTrigger));
        }
        else {
            triggers.put(helpTrigger, flag);
        }
    }


	public void addPublicTrigger(String helpTrigger) {
		if (trigs.contains(helpTrigger)) {
            logger.error(String.format("Another trigger already exists for %s", helpTrigger));
		}
		else {
			trigs.add(helpTrigger.toLowerCase());
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
