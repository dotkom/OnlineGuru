package no.ntnu.online.onlineguru.plugin.plugins.karma;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.channel.Channel;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.help.HelpPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.karma.logging.KarmaLogger;
import no.ntnu.online.onlineguru.plugin.plugins.karma.model.NetworkList;
import no.ntnu.online.onlineguru.utils.JSONStorage;
import no.ntnu.online.onlineguru.utils.Wand;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */
public class KarmaPlugin implements PluginWithDependencies {

    private Wand wand;
    private FlagsPlugin flagsPlugin;

    private final String database_folder = "database/";
    private final String database_file = database_folder + "karma.json";
    private final String log_file = database_folder + "karma.log";

    private NetworkList networkList;
    private KarmaLogger karmaLogger;

    private final String karmaTrigger = "!karma";
    private Pattern karmaPattern = Pattern.compile("(?:^|\\s)(\\S+)(?:(\\+\\+|--)|([+-]=)(\\d+))(?:\\s|$)");

    private final Flag controlFlag = Flag.a;

    public KarmaPlugin() {
        networkList = (NetworkList) JSONStorage.load(database_file, NetworkList.class);
        if (networkList == null) {
            networkList = new NetworkList();
        }

        karmaLogger = new KarmaLogger(log_file);
    }

    @Override
    public String getDescription() {
        return "Each nick in a channel can have a karma associated with it to show the persons trust level.";
    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }

    @Override
    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG:
                PrivMsgEvent pme = (PrivMsgEvent) e;
                String reply = handlePrivmsg(pme);
                // Save
                save_data();
                // Send reply
                if (reply != null) {
                    wand.sendMessageToTarget(pme.getNetwork(), pme.getTarget(), "[karma] "+reply);
                }
                break;
        }
    }

    private boolean save_data() {
        return JSONStorage.save(database_file, networkList);
    }

    private String handlePrivmsg(PrivMsgEvent e) {
        String message = e.getMessage();
        // This plugin only works in channels
        if (!e.isChannelMessage()) { return null; }
        if (message.startsWith(karmaTrigger)) {
            message = message.substring(karmaTrigger.length());
            if (message.isEmpty()) {
                int karma = networkList.getKarma(e.getNetwork(), e.getChannel(), e.getSender());
                return e.getSender() +"'s karma is "+ karma +".";
            }
            else {
                message = message.trim();
                Channel chan = e.getNetwork().getChannel(e.getChannel());
                if (chan.isOnChannel(message)) {
                    int karma = networkList.getKarma(e.getNetwork(), e.getChannel(), message);
                    return message +"'s karma is "+ karma +".";
                }
            }
        }
        else {
            ResponseKarmaFind karma = findKarmaChange(e);

            if (karma != null && karma.amount != 0) {
                // Update the data
                int newKarma = networkList.changeKarma(e.getNetwork(), e.getChannel(), karma.target, karma.amount);

                if (karma.sender.equals(karma.target)) {
                    // Log as "self inflicted"
                    karmaLogger.appendLog(karma.sender, karma.sender, karma.amount);
                    return e.getSender() +"'s karma has been reduced to "+ newKarma +" for trying to cheat.";
                }
                else {
                    // Log as proper karma change
                    karmaLogger.appendLog(karma.sender, karma.target, karma.amount);
                    if (karma.amount > 1) return karma.target +"'s karma has been increased to "+ newKarma +" by "+ karma.sender +".";
                    if (karma.amount < -1) return karma.target +"'s karma has been decreased to "+ newKarma +" by "+ karma.sender +".";
                }
            }
        }
        return null;
    }

    protected ResponseKarmaFind findKarmaChange(PrivMsgEvent e) {
        Matcher matcher = karmaPattern.matcher(e.getMessage());

        if (matcher.find()) {
            String nick = matcher.group(1);
            if (nick != null) {
                if (nick.equals(e.getSender())) {
                    return new ResponseKarmaFind(nick, nick, -5);
                }
                Channel chan = e.getNetwork().getChannel(e.getChannel());
                if (chan.isOnChannel(nick)) {
                    if (matcher.group(2) != null) {
                        String karmaDirection = matcher.group(2);
                        if (karmaDirection.equals("++")) {
                            return new ResponseKarmaFind(e.getSender(), nick, 1);
                        }
                        else if (karmaDirection.equals("--")) {
                            return new ResponseKarmaFind(e.getSender(), nick, -1);
                        }
                    }
                    if (matcher.group(3) != null) {
                        Set<Flag> flags = flagsPlugin.getFlags(e.getNetwork(), e.getChannel(), e.getSender());
                        if (flags.contains(controlFlag)) {
                            String karmaDirection = matcher.group(3);
                            int amount = Integer.parseInt(matcher.group(4));
                            if (karmaDirection.equals("+=")) {
                                return new ResponseKarmaFind(e.getSender(), nick, amount);
                            }
                            else if (karmaDirection.equals("-=")) {
                                return new ResponseKarmaFind(e.getSender(), nick, -amount);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String[] getDependencies() {
        return new String[]{"FlagsPlugin", "HelpPlugin", };
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) this.flagsPlugin = (FlagsPlugin) plugin;
        if (plugin instanceof HelpPlugin) {
            HelpPlugin helpPlugin = (HelpPlugin) plugin;
            helpPlugin.addHelp("nick++ | nick--", Flag.ANYONE, "Increase or decrease karma for a nick.");
            helpPlugin.addHelp("!karma <nick>", Flag.ANYONE, "Check karma for <nick>, or yourself if <nick> is omitted.");
            helpPlugin.addHelp("nick+=<num> | nick-=<num>", controlFlag, "Increase or decrease karma for a nick by <num>    ");
        }
    }
}
