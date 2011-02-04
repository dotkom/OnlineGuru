package no.ntnu.online.onlineguru.plugin.plugins.autoop;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.WandRepository;

/**
 *
 * @author Roy Sindre Norangshol
 */
public class AutoOp implements Plugin, PluginWithDependencies {

    private final String DB_FOLDER = "database/";
    private final String DB_FILE = DB_FOLDER + "autoop.db";
    private final String DESCRIPTION_STRING = "Auto op people on channels";
    private final String TRIGGER = "!autoop";
    private WandRepository wandRepository = null;
    private ChanServ chanServ = null;
    private final String[] dependencies = new String[]{"ChanServ"};
    // Channel { Nick : ident@hostname }
    private HashMap<String, HashMap<String, String>> autoOpList = null;

    public AutoOp() {
        try {
            autoOpList = (HashMap<String, HashMap<String, String>>) SimpleIO.loadSerializedData(DB_FILE);
            if (autoOpList == null) {
                autoOpList = new HashMap<String, HashMap<String, String>>();
            }
        } catch (FileNotFoundException ex) {
            autoOpList = new HashMap<String, HashMap<String, String>>();
        }
    }

    public String getDescription() {
        return DESCRIPTION_STRING;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {

            case JOIN: {
                handleJoin((JoinEvent) e);
                break;
            }
            case PRIVMSG: {
                handleMsg((PrivMsgEvent) e);
                break;
            }
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.JOIN);
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(WandRepository wandRepository) {
        this.wandRepository = wandRepository;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof ChanServ) {
            chanServ = (ChanServ) plugin;
        }
    }

    private void handleMsg(PrivMsgEvent pme) {
    	if(pme.isPrivateMessage()) {
	        if (pme.getMessage().toLowerCase().startsWith(TRIGGER) && chanServ.isNickLoggedIn(pme.getSender())) {
	            String[] message = pme.getMessage().split("\\s+");
	            if (message.length > 2) {
	                if ("add".equalsIgnoreCase(message[1]) && message.length == 6) {
	                    addToAutoOpList(message[2], message[3], message[4], message[5]);
	                    wandRepository.sendMessageToTarget(pme.getNetwork(), pme.getSender(), String.format("Added autoop on %s for: %s!%s@%s", message[2], message[3], message[4], message[5]));
	                } else if ("del".equalsIgnoreCase(message[1]) && message.length == 4) {
	                    removeFromAutoOpList(message[2], message[3]);
	                    wandRepository.sendMessageToTarget(pme.getNetwork(), pme.getSender(), String.format("Removed %s autoop from %s", message[3], message[2]));
	                } else if ("list".equalsIgnoreCase(message[1]) && message.length == 3) {
	                    List<String> list = listAutoOpList(message[2]);
	                    for (String user : list) {
	                        wandRepository.sendMessageToTarget(pme.getNetwork(), pme.getSender(), user);
	                    }
	                }
	            }
	        }
    	}
    }

    private List<String> listAutoOpList(String channel) {
        ArrayList<String> messages = new ArrayList<String>();
        channel = channel.toLowerCase();
        if (autoOpList.containsKey(channel)) {
            messages.add(String.format("Autoop-liste for %s", channel));
            Iterator<Entry<String, String>> channelList = autoOpList.get(channel).entrySet().iterator();
            while (channelList.hasNext()) {
                Entry<String, String> user = channelList.next();
                messages.add(String.format("- %s -> %s", user.getKey(), user.getValue()));
            }
        } else {
            messages.add(String.format("Fant ikke noen autoop-liste for kanal %s", channel));
        }
        return messages;
    }

    private void addToAutoOpList(String channel, String nick, String ident, String hostname) {
        channel = channel.toLowerCase();
        HashMap<String, String> userToSave = new HashMap<String, String>();
        userToSave.put(nick.toLowerCase(), String.format("%s@%s", ident.toLowerCase(), hostname.toLowerCase()));

        if (!autoOpList.containsKey(channel)) {
            autoOpList.put(channel, userToSave);
        } else {
            autoOpList.get(channel).putAll(userToSave);
        }
        SimpleIO.saveSerializedData(DB_FILE, autoOpList);
    }

    private void removeFromAutoOpList(String channel, String nick) {
        channel = channel.toLowerCase();
        nick = nick.toLowerCase();
        if (autoOpList.containsKey(channel)) {
            autoOpList.get(channel).remove(nick);
            SimpleIO.saveSerializedData(DB_FILE, autoOpList);
        }
    }

    private void handleJoin(JoinEvent je) {
        if (autoOpList.containsKey(je.getChannel())) {
            if (wandRepository.amIOp(je.getNetwork(), je.getChannel())) {
                HashMap<String, String> verifyNick = autoOpList.get(je.getChannel());
                if (verifyNick.containsKey(je.getNick().toLowerCase())) {
                    String[] identHostname = verifyNick.get(je.getNick().toLowerCase()).split("@");
                    Arrays.toString(identHostname);
                    if (identHostname[0].equalsIgnoreCase(je.getIdent()) && identHostname[1].equalsIgnoreCase(je.getHostname())) {
                        wandRepository.op(je.getNetwork(), je.getNick(), je.getChannel());
                    }
                }
            }
        }
    }
}
