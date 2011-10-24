package no.ntnu.online.onlineguru.plugin.plugins.git;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ;
import no.ntnu.online.onlineguru.plugin.plugins.git.github.jsonmodel.GitHubPayload;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.webserver.Webserver;
import no.ntnu.online.onlineguru.utils.xmlrpcserver.XmlRpcServer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:39
 * @todo fixme refactor this shitty plugin
 */
public class Git implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(Git.class);
    private final String DESCRIPTION = "Announces Redmine-Git changeset url";
    protected static final String DB_FOLDER = "database/";
    private static final String TRIGGER = "git";
    private final String[] dependencies = new String[]{"ChanServ"};
    private Wand wand;
    private ChanServ chanServ;

    private GitAnnounceImpl gitAnnounce;

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

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof ChanServ) {
            chanServ = (ChanServ) plugin;
        }
    }

    public String[] getDependencies() {
        return dependencies;
    }

    private void registerWithXmlRpcServer() {
        gitAnnounce = new GitAnnounceImpl(wand);
        XmlRpcServer xmlRpcServer = OnlineGuru.serviceLocator.getInstance(XmlRpcServer.class);
        Webserver webServer = OnlineGuru.serviceLocator.getInstance(Webserver.class);
        try {
            webServer.registerWebserverCallback("/plugins/git", gitAnnounce);
            xmlRpcServer.addHandler(GitAnnounce.class.getName(), gitAnnounce);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        // TODO: Allow chosen clients only.
    }

    private void handleMsg(PrivMsgEvent privMsgEvent) {
        if (privMsgEvent.isPrivateMessage()) {
            if (privMsgEvent.getMessage().toLowerCase().startsWith(TRIGGER) && chanServ.isNickLoggedIn(privMsgEvent.getSender())) {
                String[] message = privMsgEvent.getMessage().split("\\s+");
                if (message.length > 2) {
                    Boolean result = Boolean.FALSE;
                    if (isAddByRecipientKeyword(message)) {
                        addAnnounceByRecipient(privMsgEvent, message);
                    }
                } else if (isListKeyword(message[1])) {
                    listAnnounces(privMsgEvent);
                } else if (isDeleteByRecipient(message)) {
                    deleteByRecipient(privMsgEvent, message);
                }

            }
        }


    }

    private boolean isDeleteByRecipient(String[] message) {
        return "del".equalsIgnoreCase(message[2]) && message.length == 6;
    }

    private boolean isListKeyword(String s) {
        return "list".equalsIgnoreCase(s.trim());
    }

    private boolean isAddByRecipientKeyword(String[] message) {
        return "add".equalsIgnoreCase(message[1]) && message.length == 7;
    }

    private void listAnnounces(PrivMsgEvent privMsgEvent) {
        Iterator<IRCAnnounce> announces = gitAnnounce.getAnnounces();
        if (announces.hasNext()) {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), "Listing up git-redmine announces:");
            while (announces.hasNext()) {
                IRCAnnounce announce = announces.next();
                wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), announce.listIrcAnnounces());
            }
        } else {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), "No git-redmine announces added yet...");
        }
    }


    private void addAnnounceByRecipient(PrivMsgEvent privMsgEvent, String[] message) {
        Boolean result;
        result = addAnnounce(message[2], message[3], message[4], message[5], message[6]);
        if (result) {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Added git %s announce for %s to network %s on channel %s", message[2], message[3], message[4], message[5]));
        } else {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to add git %s announce for %s , does it already exist?", message[2], message[3]));
        }
    }

    private void deleteByRecipient(PrivMsgEvent privMsgEvent, String[] message) {
        Boolean result;
        result = delAnnounce(message[2], message[3], message[4], message[5]);
        if (result) {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Removed git %s announce for %s on network %s channel %s", message[2], message[3], message[4], message[5]));
        } else {
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to remove git %s announce for %s on network %s channel %s", message[2], message[3], message[4], message[5]));
        }
    }

    private Boolean addAnnounce(String repoType, String repository, String network, String channel, String verboseLevel) {
        VerboseLevel level;
        try {
            level = VerboseLevel.values()[Integer.parseInt(verboseLevel)];
        } catch (NumberFormatException nfe) {
            return Boolean.FALSE;
        } catch(ArrayIndexOutOfBoundsException aiofbe) {
            return Boolean.FALSE;
        }

        ConcurrentHashMap<String, List<ChannelAnnounce>> channelsToAnnounce = new ConcurrentHashMap<String, List<ChannelAnnounce>>();
        ArrayList<ChannelAnnounce> channels = new ArrayList<ChannelAnnounce>();
        channels.add(new ChannelAnnounce(channel, level));
        channelsToAnnounce.put(network, channels);

        GitPayload payload;
        if (repoType.equalsIgnoreCase("github")) {
            payload = new GitHubPayload(repository);
        } else {
            payload = new RedminePayload(repository);
        }

        return gitAnnounce.addAnnounce(new IRCAnnounce(payload, channelsToAnnounce));
    }

    private Boolean delAnnounce(String repoType, String repositoryIdentifier, String network, String channel) {
        return gitAnnounce.removeAnnounce(repositoryIdentifier, network, channel);
    }


}
