package no.ntnu.online.onlineguru.plugin.control;

import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Functions;
import no.ntnu.online.onlineguru.utils.IrcWand;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PluginManager {

    private HashMap<String, Plugin> loadedPlugins;
    private EventDistributor eventDistributor;
    private Wand wand;

    private static String SETTINGS_FOLDER = "settings/";
    private static String SETTINGS_FILE = SETTINGS_FOLDER + "plugins.conf";

    static Logger logger = Logger.getLogger(PluginManager.class);

    public PluginManager(EventDistributor eventDistributor, OnlineGuru onlineguru) {
        loadedPlugins = new HashMap<String, Plugin>();
        this.eventDistributor = eventDistributor;
        wand = new IrcWand(onlineguru, this);

        List<String> activePlugins = null;
        try {
            activePlugins = SimpleIO.readFileAsList(SETTINGS_FILE);
        } catch (IOException e) {
            logger.error(e);
            logger.error(String.format("Could not find any plugin settings, please make %s", SETTINGS_FILE));
            System.exit(1);
        }
        if (activePlugins == null || activePlugins.size() < 1) {
            activePlugins = loadMinimalAndEssentialPlugins();
            try {
                SimpleIO.appendLinesToFile(SETTINGS_FILE, activePlugins);
            } catch (IOException e) {
                logger.error(e);
                logger.error(String.format("Could not save minimal and essential plugins to %s", SETTINGS_FILE));
            }
        }

        loadPlugins(activePlugins);
        loadDependencies();
    }

    private List<String> loadMinimalAndEssentialPlugins() {
        logger.warn("Loading minimal and essential plugins for the bot to run, please edit plugins.conf to add more plugins");
        return Arrays.asList("no.ntnu.online.onlineguru.plugin.plugins.auth.Auth",
                "no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ",
                "no.ntnu.online.onlineguru.plugin.plugins.channeljoiner.ChannelJoiner",
                "no.ntnu.online.onlineguru.plugin.plugins.die.Die",
                "no.ntnu.online.onlineguru.plugin.plugins.help.Help",
                "no.ntnu.online.onlineguru.plugin.plugins.nickserv.NickServ",
                "no.ntnu.online.onlineguru.plugin.plugins.version.Version");
    }

    private void loadPlugins(List<String> activePlugins) {
        for (String plugin : activePlugins) {
            try {
                initiatePlugin((Plugin) Class.forName(plugin).newInstance());
            } catch (InstantiationException e) {
                logger.error(e);
                System.exit(2);
            } catch (IllegalAccessException e) {
                logger.error(e);
                System.exit(2);
            } catch (ClassNotFoundException e) {
                logger.error(e);
                System.exit(2);
            }
        }
        // @todo remove this in the future..
        /*initiatePlugin(new HistoryPlugin());
        initiatePlugin(new Auth());
        initiatePlugin(new AutoOp());
        initiatePlugin(new Bus());
		initiatePlugin(new ChanServ());
		initiatePlugin(new ChannelJoiner());
		initiatePlugin(new Die());
		initiatePlugin(new Help());
        initiatePlugin(new LastFM());
        initiatePlugin(new Middag());
        initiatePlugin(new NickServ());
        initiatePlugin(new Peak());
        initiatePlugin(new SimpleTrigger());
        initiatePlugin(new Spotify());
        initiatePlugin(new TwitterPlugin());
        initiatePlugin(new URLHandler());
        initiatePlugin(new Version());
        initiatePlugin(new LmgtfyPlugin());
        initiatePlugin(new SeenPlugin());
        initiatePlugin(new RegexPlugin());
        //initiatePlugin(new ShellPlugin()); // not really a safe plugin ;-)

        // These take a long time to load, before they are threaded, they need to be last on the list.
        initiatePlugin(new CalendarPlugin());
        initiatePlugin(new Git());
        initiatePlugin(new MailAnnouncer());*/

    }

    private void initiatePlugin(Plugin plugin) {
        plugin.addEventDistributor(eventDistributor);
        plugin.addWand(wand);
        loadedPlugins.put(Functions.getClassName(plugin).toUpperCase(), plugin);
    }

    private void loadDependencies() {
        new DependencyManager(loadedPlugins);
    }

    public Plugin getPlugin(String pluginClassName) {
        pluginClassName = pluginClassName.toUpperCase();
        if (loadedPlugins.containsKey(pluginClassName)) {
            return loadedPlugins.get(pluginClassName);
        } else {
            return null;
        }
    }
}
