package no.ntnu.online.onlineguru.plugin.plugins.buss;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;
import no.ntnu.online.onlineguru.utils.WandRepository;

/**
 * User: Dag Olav Prestegarden <dagolav@prestegarden.com>
 * Date: Sep 18, 2010
 * Time: 11:39:41 PM
 */
public class Bus implements PluginWithDependencies {
    private static final String DESCRIPTION_STRING = "Returns buss results from ATB's Bus Oracle service. Usage: !buss <question>";
    private static final String PLUGIN_KEYWORD = "!buss";

    private WandRepository wandRepository;
    private Help help;
    private BusAsker busAsker;

    public Bus(BusAsker asker) {
        this.busAsker = asker;
    }

    public Bus() {
        this(new AtbWebAsker(new HttpWebFetcher()));
    }

    public String getDescription() {
        return DESCRIPTION_STRING;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
        case PRIVMSG:
			handleMessageEvent((PrivMsgEvent)e);
			break;
		}
    }

    private void handleMessageEvent(Event e) {
    	PrivMsgEvent pme = (PrivMsgEvent)e;
        String target = pme.getTarget();
		String message = pme.getMessage();

        handleMessage(e, target, message);
    }

    private void handleMessage(Event e, String target, String message) {
        if (message.startsWith(PLUGIN_KEYWORD) && messageContainsQuestion(message)) {
            message = message.substring(PLUGIN_KEYWORD.length() + 1);
            wandRepository.sendMessageToTarget(e.getNetwork(), target, busAsker.ask(message));
        }
    }

    private boolean messageContainsQuestion(String message) {
        return message.length() > (PLUGIN_KEYWORD.length() + 1);
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
			help.addPublicTrigger("!buss");
			help.addPublicHelp("!buss", "!buss <question> - Asks the AtB bus oracle for directions based on your question.");
		}
	}
	
}
