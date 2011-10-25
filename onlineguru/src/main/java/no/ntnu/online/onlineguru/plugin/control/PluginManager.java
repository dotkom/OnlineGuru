package no.ntnu.online.onlineguru.plugin.control;

import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.plugins.auth.Auth;
import no.ntnu.online.onlineguru.plugin.plugins.autoop.AutoOp;
import no.ntnu.online.onlineguru.plugin.plugins.buss.Bus;
import no.ntnu.online.onlineguru.plugin.plugins.calendar.CalendarPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.channeljoiner.ChannelJoiner;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ;
import no.ntnu.online.onlineguru.plugin.plugins.die.Die;
import no.ntnu.online.onlineguru.plugin.plugins.git.Git;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;
import no.ntnu.online.onlineguru.plugin.plugins.history.HistoryPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.lastfm.LastFM;
import no.ntnu.online.onlineguru.plugin.plugins.lmgtfy.LmgtfyPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.MailAnnouncer;
import no.ntnu.online.onlineguru.plugin.plugins.middag.Middag;
import no.ntnu.online.onlineguru.plugin.plugins.nickserv.NickServ;
import no.ntnu.online.onlineguru.plugin.plugins.peak.Peak;
import no.ntnu.online.onlineguru.plugin.plugins.seen.SeenPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.simpletrigger.SimpleTrigger;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.Spotify;
import no.ntnu.online.onlineguru.plugin.plugins.twitter.TwitterPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.urlhandler.URLHandler;
import no.ntnu.online.onlineguru.plugin.plugins.version.Version;
import no.ntnu.online.onlineguru.utils.Functions;
import no.ntnu.online.onlineguru.utils.IrcWand;
import no.ntnu.online.onlineguru.utils.Wand;

import java.util.HashMap;

public class PluginManager {
	
	private HashMap<String, Plugin> loadedPlugins;
	private EventDistributor eventDistributor;
	private Wand wand;
	
	public PluginManager(EventDistributor eventDistributor, OnlineGuru onlineguru) {
		loadedPlugins = new HashMap<String, Plugin>();
		this.eventDistributor = eventDistributor;
		wand = new IrcWand(onlineguru, this);
		loadPlugins();
		loadDependencies();
	}
	
	private void loadPlugins() {
        initiatePlugin(new HistoryPlugin());
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

        // These take a long time to load, before they are threaded, they need to be last on the list.
        initiatePlugin(new CalendarPlugin());
        initiatePlugin(new Git());
        initiatePlugin(new MailAnnouncer());

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
		if(loadedPlugins.containsKey(pluginClassName)) {
			return loadedPlugins.get(pluginClassName);
		}
		else {
			return null;
		}
	}
}
