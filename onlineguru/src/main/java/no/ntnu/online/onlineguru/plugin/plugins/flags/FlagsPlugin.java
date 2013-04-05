package no.ntnu.online.onlineguru.plugin.plugins.flags;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.ConnectEvent;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.handler.CommandHandler;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.flags.storage.DBHandler;
import no.ntnu.online.onlineguru.plugin.plugins.nickserv.NickServ;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Håvard Slettvold
 */
public class FlagsPlugin implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(FlagsPlugin.class);

    private Wand wand;
    private NickServ nickserv;

    private DBHandler db;
    private CommandHandler commandHandler;

    public FlagsPlugin() {
        db = new DBHandler();
        commandHandler = new CommandHandler();
    }

    /*
     * Methods implemented from PluginWithDependencies
     */

    public String[] getDependencies() {
        return new String[]{"NickServ", };
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof NickServ) {
            this.nickserv = (NickServ) plugin;
        }
    }

    public String getDescription() {
        return "Keeps track of flags used to restrict access to commands on OnlineGuru.";
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case CONNECT:
                handleConnectEvent((ConnectEvent) e);
                break;
            case JOIN:
                handleJoinEvent((JoinEvent) e);
                break;
            case PRIVMSG:
                handlePrivMsgEvent((PrivMsgEvent) e);
                break;
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.CONNECT);
        eventDistributor.addListener(this, EventType.JOIN);
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
        commandHandler.setWand(wand);
    }

    /*
     * Handle events
     */

    private void handleConnectEvent(ConnectEvent e) {
        try {
            db.initiate(e.getNetwork());
        } catch (IOException ioe) {
            logger.error("Failed to create database for network: " + e.getNetwork().getServerAlias(), ioe.getCause());
        }

    }

    private void handleJoinEvent(JoinEvent e) {
        db.createChannel(e);
    }

    private void handlePrivMsgEvent(PrivMsgEvent e) {
        commandHandler.handleCommand(e);
    }

    /*
     * Public methods
     */
    public Set<Flag> getFlags(Network network, String nick) {
        String username = nickserv.getUsername(network, nick);

        Set<Flag> flags = new HashSet<Flag>();

        if (username != null) {

            if (db.isSuperuser(network, username)) {
                for (Flag f : Flag.values()) {
                    flags.add(f);
                }
            }
            else {
                flags.add(Flag.ANYONE);
                // TODO
                // fetch users flags
            }
        }


        return flags;
    }

}
