package no.ntnu.online.onlineguru.plugin.plugins.dict;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.WandRepository;

public class Dict implements Plugin {

    private static final String DESCRIPTION_STRING = "Returns dictionary lookup results from the Clue dictionary. Usage: !dict <word>, !dict <dict> <word>";
    private static final String PLUGIN_KEYWORD = "!dict";
    private WandRepository wandRepository;
    private DictService service;

    public Dict() {
        this.service = new DictService();
    }

    public String getDescription() {
        return DESCRIPTION_STRING;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
            	PrivMsgEvent pme = (PrivMsgEvent)e;
            	
            	if(pme.isPrivateMessage()) {
	                String message = pme.getMessage();
	
	                if (messageContainsQuestion(message) && message.startsWith(PLUGIN_KEYWORD)) {
	                    message = message.substring(PLUGIN_KEYWORD.length() + 1);
	                    String[] dict = message.split(" ");
	                    String result;
	                    if (dict.length > 1) {
	                        result = service.lookup(dict[0], message.substring(dict[0].length() + 1));
	                    } else {
	                        result = service.lookup(message);
	                    }
	
	                    for (String msg : result.split("\n")) {
	                        wandRepository.sendMessageToTarget(e.getNetwork(), pme.getTarget(), msg);
	                    }
	                }
            	}
            	break;
            }
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
}
