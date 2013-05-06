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
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.flags.storage.NetworkFlags;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;
import no.ntnu.online.onlineguru.plugin.plugins.nickserv.NickServ;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Manages flags and superusers for the bot.
 *
 * @author Håvard Slettvold
 */
public class FlagsPlugin implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(FlagsPlugin.class);

    private Wand wand;
    private NickServ nickserv;

    private Map<Network, NetworkFlags> networks;
    private CommandHandler commandHandler;

    private static final String database_folder = "database/";
    private static final String flags_database_folder = database_folder + "flags/";
    private static final String flags_settings_folder = "settings/";
    private static final String flags_settings_file = flags_settings_folder + "flags.conf";

    private String root_username = null;

    public FlagsPlugin() {
        networks = new HashMap<Network, NetworkFlags>();
        commandHandler = new CommandHandler(this);

        verifySettings();
    }

    private void verifySettings() {
        SimpleIO.createFolder(flags_database_folder);

        try {
            Map<String, String> settings = SimpleIO.loadConfig(flags_settings_file);
            root_username = settings.get("root_username");

            if (root_username == null) {
                SimpleIO.appendLineToFile(flags_settings_file, "root_username=");
            }
            if (root_username == null) {
                logger.error("Flags configured incorrectly. Check flags.conf.");
            }
            if (root_username != null && root_username.isEmpty()) {
                logger.error("Flags root username not specified.");
            }

        } catch (IOException ioe) {
            logger.error("Failed to read settings file.", ioe.getCause());
        }
    }

    /*
     * Methods implemented from PluginWithDependencies
     */

    public String[] getDependencies() {
        return new String[]{"NickServ","Help",};
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof NickServ) {
            this.nickserv = (NickServ) plugin;
        }
        if (plugin instanceof Help) {
            Help help = (Help) plugin;
            help.addHelp(
                    "flags",
                    Flag.f,
                    "<botname> flags [channel] nick [flags] - Updates flags for a user. <botname> is only needed in channel.",
                    "[channel] argument can be skipped when used in channel. [flags] can be left blank to show a users current flags."
            );
            help.addHelp(
                    "su",
                    Flag.f,
                    "<botname> su(peruser) nick [add|rem(ove)] - Sets superuser status for a nick on a network. <botname> is only needed in channel."
            );
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
        networks.put(
                e.getNetwork(),
                new NetworkFlags(
                        e.getNetwork(),
                        flags_database_folder + e.getNetwork().getServerAlias() + ".db",
                        root_username
                )
        );
    }

    private void handleJoinEvent(JoinEvent e) {
        if (wand.isMe(e.getNetwork(), e.getNick())) {
            networks.get(e.getNetwork()).addChannel(e.getChannel());
        }
    }

    private void handlePrivMsgEvent(PrivMsgEvent e) {
        commandHandler.handleCommand(e);
    }

    /*
     * Internal helper methods
     */

    protected String serializeFlags(Set<Flag> flags) {
        String result = "";
        for (Flag f : flags) {
            if (f == Flag.ANYONE) {
                continue;
            }
            result += f;
        }
        return result;
    }

    protected Set<Flag> deserializeFlags(String flags) {
        Set<Flag> result = new HashSet<Flag>();
        if (flags != null) {
            for (char c : flags.toCharArray()) {
                try {
                    Flag f = Flag.valueOf("" + c);
                    result.add(f);
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid flag recorded: " + c);
                }
            }
        }
        return result;
    }

    protected Set<Flag> updateFlags(Set<Flag> currentFlags, String flagsDelta) {
        char action = '.';
        Flag currentFlag;
        Set<Flag> updatedFlags = new HashSet<Flag>();
        updatedFlags.addAll(currentFlags);

        for (Character c : flagsDelta.toCharArray()) {
            currentFlag = null;

            switch (c) {
                case '+':
                case '-':
                    action = c;
                    break;
                default:
                    try {
                        currentFlag = Flag.valueOf("" + c);
                    } catch (IllegalArgumentException e) {
                        logger.debug("Illegal flag '" + c + "'.");
                    }
            }

            if (currentFlag != null) {
                switch (action) {
                    case '-':
                        updatedFlags.remove(currentFlag);
                        break;
                    case '+':
                        updatedFlags.add(currentFlag);
                        break;
                }
            }

        }
        return updatedFlags;
    }

    protected boolean saveFlags(Network network, String channel, String nick, Set<Flag> flags) {
        String username = nickserv.getUsername(network, nick);

        return networks.get(network).
                saveFlags(channel, username, serializeFlags(flags));
    }

    protected boolean isUser(Network network, String username) {
        if (username == null) {
            return false;
        }
        return nickserv.getUsername(network, username) != null;
    }

    protected boolean isSuperuser(Network network, String nick) {
        String username = nickserv.getUsername(network, nick);

        return networks.get(network).isSuperuser(username);
    }

    protected boolean addSuperuser(Network network, String nick) {
        String username = nickserv.getUsername(network, nick);

        return networks.get(network).addSuperuser(username);
    }

    protected boolean removeSuperuser(Network network, String nick) {
        String username = nickserv.getUsername(network, nick);

        return networks.get(network).removeSuperuser(username);
    }

    /*
     * Public methods
     */

    /**
     * Fetches flags that a user has in a channel.
     *
     * Superusers will override and channel flags, essentially having all flags.
     *
     * @param network Network to check.
     * @param channel Channel to check.
     * @param nick Nick to check.
     * @return Set of flags belonging to Nick in Channel on Network.
     */
    public Set<Flag> getFlags(Network network, String channel, String nick) {
        String username = nickserv.getUsername(network, nick);

        Set<Flag> flags = new HashSet<Flag>();

        if (username == null) {
            flags.add(Flag.ANYONE);
        }
        else {
            if (channel == null) {
                flags = getFlags(network, username);
            }
            else {
                if (isSuperuser(network, username)) {
                    flags.addAll(EnumSet.allOf(Flag.class));
                }
                else {
                    String f = networks.get(network).getFlags(channel, nick);
                    flags.addAll(deserializeFlags(f));
                }
            }
        }
        return flags;
    }

    /**
     * Will get flags for a nick on a server. Typically a fallback if no channel can be obtained.
     *
     * @param network Network to check.
     * @param nick Nick to check.
     * @return Set of flags the nick has.
     */
    public Set<Flag> getFlags(Network network, String nick) {
        String username = nickserv.getUsername(network, nick);

        Set<Flag> flags = new HashSet<Flag>();

        if (username != null) {

            if (isSuperuser(network, username)) {
                flags.addAll(EnumSet.allOf(Flag.class));
            }
            else {
                flags.add(Flag.ANYONE);
            }
        }
        return flags;
    }

    /**
     * Shortcut to check if a nick has a certain flag.
     *
     * @param network Network to check.
     * @param channel Channel to check.
     * @param nick Nick to check.
     * @param flag Flag to check if Nick has.
     * @return boolean
     */
    public boolean hasFlag(Network network, String channel, String nick, Flag flag) {
        return getFlags(network, channel, nick).contains(flag);
    }

}
