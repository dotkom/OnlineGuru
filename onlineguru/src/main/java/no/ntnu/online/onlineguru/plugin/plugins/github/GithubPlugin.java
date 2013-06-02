package no.ntnu.online.onlineguru.plugin.plugins.github;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.AnnounceSubscription;
import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.CallbackListener;
import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.Listeners;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;
import no.ntnu.online.onlineguru.service.services.webserver.Webserver;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Original idea and implementation by Roy Sindre Norangshol
 *
 * @author Håvard Slettvold
 */
public class GithubPlugin implements PluginWithDependencies {

    static Logger logger = Logger.getLogger(GithubPlugin.class);

    private Wand wand;
    private FlagsPlugin flagsPlugin;

    private final String database_folder = "database/";
    private final String database_file = database_folder + "github.db";

    private final Flag editorFlag = Flag.A;
    private final Flag peekingFlag = Flag.a;

    private GithubCallback githubCallback;
    private StorageManager storageManager;
    private Listeners listeners;

    private Pattern commandPattern = Pattern.compile(
            "!github" +                                                                           // Trigger
            " (?:https://github\\.com/)?([\\w.-]+/[\\w.-]+)" +                                    // Repository, group 1
            "(?: (#\\S+))?" +                                                                     // channel, group 2
            "(" +                                                                                 // Operation group 3
                " (?:(b(?:ranches)?)|(c(?:ommits)?)|(?:(i(?:ssues)?)|(p(?:ull)?r(?:equests)?)))" +   // Operation, groups: branches=3 commits=4 issues=5 pullrequest=6
                " (on|off)" +                                                                     // Setting, group 7
            ")?",
            Pattern.CASE_INSENSITIVE
    );

    public GithubPlugin() {
        storageManager = new StorageManager(database_file);
        listeners = new Listeners();
    }

    @Override
    public String getDescription() {
        return "This plugin hooks into Github's webhooks to announce changes in repositories.";
    }

    @Override
    public String[] getDependencies() {
        return new String[]{"FlagsPlugin", "Help",};
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) {
            this.flagsPlugin = (FlagsPlugin) plugin;
        }
        if (plugin instanceof Help) {
            Help help = (Help) plugin;
            help.addHelp(
                    "!github",
                    Flag.a,
                    "!github <repository> [channel] [branches|commits|issues|pullrequests on|off] - Manage github repository subscription for channels.",
                    "The operations can be shortened to b|c|i|pr. Channel is optional. Picks current channel if not specified, but mandatory in private.",
                    "If only a repository i specified, it will show the subscriptions for current channel or [channel] if specified."
            );
        }

        listeners = storageManager.loadListeners();
        if (listeners == null) {
            listeners = new Listeners();
        }
        githubCallback = new GithubCallback(wand, listeners);

        // Running the registering with the web server async, it may take time.
        new Runnable() {
            @Override
            public void run() {
                // Fetch the webserver instance.
                Webserver webServer = OnlineGuru.serviceLocator.getInstance(Webserver.class);
                // Register this plugins uri.
                webServer.registerWebserverCallback("/plugins/git", githubCallback);
            }
        }.run();
    }


    @Override
    public void incomingEvent(Event e) {
        if (e.getEventType() == EventType.PRIVMSG) {
            PrivMsgEvent pme = (PrivMsgEvent) e;
            String reply = handleCommand(pme);
            if (reply != null) {
                // Save the config. Not all calls to this method needs a save, but
                // distinguishing each of the calls from eachother is more complicated than
                // just saving in any case.
                // Saving is done in framework-invoked medthods in order to separate out
                // methods for testing and not overriding prod storage when testing.
                storageManager.saveListeners(listeners);

                wand.sendMessageToTarget(pme.getNetwork(), pme.getTarget(), "[github] " + reply);
            }
        }
    }

    protected String handleCommand(PrivMsgEvent e) {
        String message = e.getMessage().toLowerCase();
        if (message.startsWith("!github ")) {
            Matcher matcher = commandPattern.matcher(e.getMessage());

            // Legal characters are A-Z a-z 0-9 . -
            // Any other character will be converted to a dash -
            if (matcher.find()) {
                // Find a channel, or return an error
                String channel = matcher.group(2);
                if (channel == null) {
                    if (e.isChannelMessage()) {
                        channel = e.getChannel();
                    }
                    else {
                        return "You must specify a channel when using this command in private messages.";
                    }
                }

                // Check if the user has permissions to access.
                Set<Flag> flags = flagsPlugin.getFlags(e.getNetwork(), channel, e.getSender());
                if (!flags.contains(Flag.a) && !flags.contains(Flag.A)) {
                    return "You do not have access to use this command. It requires +a for info and +A to edit.";
                }

                String repo = matcher.group(1).toLowerCase();
                String setting = matcher.group(8);

                // Get the callback listener for the specified repo.
                CallbackListener cl = listeners.get("https://github.com/" + repo);
                // If it doesn't exist, create a new one.
                if (cl == null) {
                    cl = new CallbackListener();
                    listeners.put("https://github.com/" + repo, cl);
                }

                AnnounceSubscription as = cl.getOrCreateSubscription(e.getNetwork().getServerAlias(), channel);

                if (matcher.group(3) != null) {
                    if (flags.contains(Flag.A)) {
                        // Matched b(ranches)
                        if (matcher.group(4) != null) {
                            if (as.setWants_branches(setting.equals("on")))
                                return "Announcing of branch creating and deletion for " + repo + " in " + channel + " turned " + setting + ".";
                        }
                        // Matched c(ommits)
                        else if (matcher.group(5) != null) {
                            if (as.setWants_commits(setting.equals("on")))
                                return "Announcing of commits for " + repo + " in " + channel + " turned " + setting + ".";
                        }
                        // Matched i(ssues)
                        else if (matcher.group(6) != null) {
                            if (as.setWants_issues(setting.equals("on")))
                                return "Announcing of issue activity for " + repo + " in " + channel + " turned " + setting + ".";
                        }
                        // Matched p(ull)r(equests)
                        else if (matcher.group(7) != null) {
                            if (as.setWants_pull_requests(setting.equals("on")))
                                return "Announcing of pull request activity for " + repo + " in " + channel + " turned " + setting + ".";
                        }
                        return "No subscriptions updated.";
                    }
                    else {
                        return "You do not have access to this feature. It requires +A.";
                    }
                }
                else if (e.getMessage().split(" ").length > 2 && matcher.group(2) == null) {
                    return "Invalid operation '" + e.getMessage().split(" ")[2] + "'.";
                }
                else if (e.getMessage().split(" ").length > 3 && matcher.group(2) != null) {
                    return "Invalid operation '" + e.getMessage().split(" ")[3] + "'.";
                }
                else {
                    return String.format("Subscriptions for %s: %s", channel, as.toString());
                }
            }
            else {
                return "Unrecognized syntax. See !help !github for correct syntax.";
            }
        }

        return null;
    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }

}
