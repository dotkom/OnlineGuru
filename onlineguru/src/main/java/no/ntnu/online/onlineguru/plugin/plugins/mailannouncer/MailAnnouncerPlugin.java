package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;


import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ;
import no.ntnu.online.onlineguru.service.services.xmlrpcserver.XmlRpcServer;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MailAnnouncerPlugin is a {@link no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies} in {@link no.ntnu.online.onlineguru.OnlineGuru}
 * @todo fixme refactor this shitty plugin
 *
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class MailAnnouncerPlugin implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(MailAnnouncerPlugin.class);
    private static final String DESCRIPTION = "MailAnnouncerPlugin announces incoming messages thru our XML RPC Server as Email messages to defined IRC channels";
    private static final String TRIGGER = "mail";
    private final String[] dependencies = new String[]{"ChanServ"};
    protected static final String DB_FOLDER = "database/";
    // TODO: Use or remove
    private final String DB_FILE_CLIENTS = DB_FOLDER + "mailannouncer-clients.db";
    private EmailImpl emailReceiver;
    private Wand wand;
    private ChanServ chanServ;


    public MailAnnouncerPlugin() {
        super();
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                handleMsg((PrivMsgEvent) e);
                break;
            }
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;

        registerWithXmlRpcServer();
    }

    private void registerWithXmlRpcServer() {
        emailReceiver = new EmailImpl(wand);

        new Thread() {

            @Override
            public void run() {
                XmlRpcServer xmlRpcServer = OnlineGuru.serviceLocator.getInstance(XmlRpcServer.class);
                try {
                    xmlRpcServer.addHandler(Email.class.getName(), emailReceiver);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            // TODO: Allow chosen clients only.
        }.start();
    }


    public void loadDependency(Plugin plugin) {
        if (plugin instanceof ChanServ) {
            chanServ = (ChanServ) plugin;
        }
    }

    public String[] getDependencies() {
        return dependencies;
    }

    private void handleMsg(PrivMsgEvent privMsgEvent) {
        if (privMsgEvent.isPrivateMessage()) {
            if (privMsgEvent.getMessage().toLowerCase().startsWith(TRIGGER) && chanServ.isNickLoggedIn(privMsgEvent.getSender())) {
                String[] message = privMsgEvent.getMessage().split("\\s+");
                if (message.length > 2) {
                    Boolean result = Boolean.FALSE;
                    if (isAddByRecipientKeyword(message)) {
                        addAnnounceByRecipient(privMsgEvent, message);
                    } else if (isAddByListIdKeyword(message)) {
                        addAnnounceByList(privMsgEvent, message);
                    } else if (isSetTagKeyword(message)) {
                        setTag(privMsgEvent, message, result);
                    }
                } else if (isListKeyword(message[1])) {
                    listAnnounces(privMsgEvent);
                }

            }
        }


    }

    private boolean isListKeyword(String s) {
        return "list".equalsIgnoreCase(s.trim());
    }

    private boolean isSetTagKeyword(String[] message) {
        return "tag".equalsIgnoreCase(message[1]) && message.length == 5;
    }

    private boolean isAddByListIdKeyword(String[] message) {
        return "addlistid".equalsIgnoreCase(message[1]) && message.length == 5;
    }

    private boolean isAddByRecipientKeyword(String[] message) {
        return "add".equalsIgnoreCase(message[1]) && message.length == 5;
    }

    private void listAnnounces(PrivMsgEvent privMsgEvent) {
        Iterator<Announce> announces = emailReceiver.getAnnounces();
        if (announces.hasNext()) {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), "Listing up mail announces:");
            while (announces.hasNext()) {
                Announce announce = announces.next();
                wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), announce.toString());
            }
        } else {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), "No mail announces added yet...");
        }
    }

    private void setTag(PrivMsgEvent privMsgEvent, String[] message, Boolean result) {
        if ("list".equalsIgnoreCase(message[2])) {
            result = emailReceiver.setAnnounceTag(LookupAnnounce.getLookup(null, message[3]), message[4]);
        }   else if ("email".equalsIgnoreCase(message[2])) {
            result = emailReceiver.setAnnounceTag(LookupAnnounce.getLookup(message[3], null), message[4]);
        }

        if (result) {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Modified entry %s to set announce tag %s" , message[3], message[4]));
        } else {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to modify entry %s to set announce tag %s" , message[3], message[4]));
        }
    }

    private void addAnnounceByList(PrivMsgEvent privMsgEvent, String[] message) {
        Boolean result;
        result = addListIdAnnounce(message[2], message[3], message[4]);
        if (result) {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Added mail announce for list-id %s to network %s on channel %s", message[2], message[3], message[4]));
        } else {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to add mail announce for list id %s , does it already exist?", message[2]));
        }
    }

    private void addAnnounceByRecipient(PrivMsgEvent privMsgEvent, String[] message) {
        Boolean result;
        result = addAnnounce(message[2], message[3], message[4]);
        if (result) {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Added mail announce for email %s to network %s on channel %s", message[2], message[3], message[4]));
        } else {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to add mail announce for email %s , does it already exist?", message[2]));
        }
    }

    private Boolean addAnnounce(String toEmail, String network, String channel) {
        return addAnnounce(toEmail, network, channel, null);
    }

    private Boolean addAnnounce(String toEmail, String network, String channel, String tag) {
        ConcurrentHashMap<String, List<String>> channelsToAnnounce = new ConcurrentHashMap<String, List<String>>();
        ArrayList<String> channels = new ArrayList<String>();
        channels.add(channel);
        channelsToAnnounce.put(network, channels);

        return emailReceiver.addAnnounce(new Announce(tag, toEmail, null, channelsToAnnounce));
    }

    private Boolean addListIdAnnounce(String listId, String network, String channel) {
        ConcurrentHashMap<String, List<String>> channelsToAnnounce = new ConcurrentHashMap<String, List<String>>();
        ArrayList<String> channels = new ArrayList<String>();
        channels.add(channel);
        channelsToAnnounce.put(network, channels);

        return emailReceiver.addAnnounce(new Announce(null, null, null, channelsToAnnounce, listId));
    }
}
