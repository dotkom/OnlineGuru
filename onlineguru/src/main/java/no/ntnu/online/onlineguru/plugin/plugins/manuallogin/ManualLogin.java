package no.ntnu.online.onlineguru.plugin.plugins.manuallogin;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.ConnectEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;
import no.ntnu.online.onlineguru.plugin.plugins.nickserv.NickServ;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ManualLogin implements PluginWithDependencies {


    private static Logger logger;
    private Map<Network, Map<String, String>> networkLogins;
    private NickServ nickServ;
    private FlagsPlugin flagsPlugin;
    private Wand wand;

    public ManualLogin() {
        logger = Logger.getLogger(this.getClass());
        networkLogins = new HashMap<Network, Map<String, String>>();
        Settings.LoadSettings();
    }

    public String getDescription() {
        return "[manuallogin] This plugin helps the bot recognise users on networks without nick identification";
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG:
                handlePrivMsg((PrivMsgEvent)e);
                break;
            case CONNECT:
                handleConnect((ConnectEvent)e);
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
        eventDistributor.addListener(this, EventType.CONNECT);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

    public String[] getDependencies() {

        return new String[] { "NickServ", "Help" , "FlagsPlugin" };
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof NickServ)
            nickServ = (NickServ)plugin;
        if (plugin instanceof Help)
            injectHelp((Help)plugin);
        if (plugin instanceof FlagsPlugin)
            flagsPlugin = (FlagsPlugin)plugin;
    }

    private void handleConnect(ConnectEvent e) {
        networkLogins.put(e.getNetwork(), Storage.loadNetworkLoginDatabase(e.getNetwork()));
    }

    private void injectHelp(Help help) {

        help.addHelp("register", Flag.A, "register [nick] [password] - Register a user with the bot.");
        help.addHelp("reloadlogin", Flag.A, "reloadlogin - Reload the login database.");
        help.addHelp("login", Flag.ANYONE, "login [nick] [password] - Log in to the bot.");
    }

    private void handlePrivMsg(PrivMsgEvent e) {

        String command = e.getMessage().trim().split("\\s+")[0];

        if(command.equals(Settings.registerKeyword))
            handleRegisterEvent(e);

        else if (command.equals(Settings.loginKeyword))
            handleLoginEvent(e);

        else if (command.equals(Settings.reloadLoginKeyword))
            handleUpdateLogin(e);
    }

    private void handleRegisterEvent(PrivMsgEvent e) {

        String[] tokens = e.getMessage().substring(Settings.registerKeyword.length()).trim().split("\\s+");
        Network network = e.getNetwork();
        String sender   = e.getSender();

        //A user must be in a common channel with the bot in order to register manually
        if(network.getNick(sender) == null) {
            wand.sendMessageToTarget(e.getNetwork(), e.getSender(), "You must be in a channel with me to be able to register.");
            return;
        }


        //If the syntax is not correct and the user is authenticated and superuser, send a message to help the user out
        if (tokens.length != 2 && nickServ.isAuthed(network, sender) && flagsPlugin.isSuperuser(network, sender)) {
            wand.sendMessageToTarget(network, sender, "Wrong syntax. See !help register.");
            return;
        }


        //A user must be authed and be superuser in order to register users
        if (nickServ.isAuthed(network, sender) && flagsPlugin.isSuperuser(network, sender)) {
            String username = tokens[0];
            String password = tokens[1];

            networkLogins.get(network).put(username, password);
            Storage.saveNetworkLoginDatabase(network, networkLogins.get(network));
            wand.sendMessageToTarget(network, sender, String.format("User with username %s was added to network %s.",
                                                                    username, network));
        }
        else {
            wand.sendMessageToTarget(network, sender, "You do not have the rights to register users.");
        }
    }

    private void handleLoginEvent(PrivMsgEvent e) {

        String[] tokens = e.getMessage().substring(Settings.loginKeyword.length()).trim().split("\\s+");
        Network network = e.getNetwork();
        String sender = e.getSender();

        //A user must be in a common channel with the bot in order to login manually
        if (network.getNick(sender) == null) {
            wand.sendMessageToTarget(e.getNetwork(), e.getSender(), "You must be in a channel with me to be able to login.");
            return;
        }


        //If the syntax was incorrect, send a message to help the user out
        if (tokens.length != 2) {
            wand.sendMessageToTarget(network, sender, "Wrong syntax. See !help login.");
            return;
        }

        String username = tokens[0];
        String password = tokens[1];

        if(networkLogins.get(network).containsKey(username) && networkLogins.get(network).get(username).equals(password)) {
            nickServ.fakeNickServAuthentication(network, sender, sender);
            wand.sendMessageToTarget(network, sender, "Login successful.");
        }
        else
            wand.sendMessageToTarget(network, sender, "Login failed.");
    }

    private void handleUpdateLogin(PrivMsgEvent e) {

        Network network = e.getNetwork();
        String sender = e.getSender();

        if(nickServ.isAuthed(network, sender) && flagsPlugin.isSuperuser(network, sender)) {
            networkLogins.put(network, Storage.loadNetworkLoginDatabase(network));
            wand.sendMessageToTarget(network, sender, String.format("Database successfully reloaded for network %s",
                                                                    network.getServerAlias()));
        }
        else {
            wand.sendMessageToTarget(network, sender, "You do not have the rights to perform that action.");
        }
    }
}
