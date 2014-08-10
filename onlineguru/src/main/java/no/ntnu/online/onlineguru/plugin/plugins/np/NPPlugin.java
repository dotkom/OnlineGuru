package no.ntnu.online.onlineguru.plugin.plugins.np;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.help.HelpPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.ScrobbleStorage;
import no.ntnu.online.onlineguru.service.services.webserver.Webserver;
import no.ntnu.online.onlineguru.utils.JSONStorage;
import no.ntnu.online.onlineguru.utils.Wand;

/**
 * @author Håvard Slettvold
 */
public class NPPlugin implements PluginWithDependencies {

    private Wand wand;

    private LastFM lastfm;
    private ScrobbleStorage scrobbleStorage;
    private ScrobblerCallback scrobblerCallback;

    public NPPlugin() {

    }

    @Override
    public String[] getDependencies() {
        // Doing this setup in a method that is solely called by the PluginManager once.
        // The reason is we do not want to execute these actions during tests.

        scrobbleStorage = (ScrobbleStorage) JSONStorage.load(ScrobbleStorage.database_file, ScrobbleStorage.class);
        if (scrobbleStorage == null) {
            scrobbleStorage = new ScrobbleStorage();
        }
        scrobblerCallback = new ScrobblerCallback(scrobbleStorage);

        // Running the registering with the web server async, it may take time.
        new Thread() {
            @Override
            public void run() {
                // Fetch the webserver instance.
                Webserver webServer = OnlineGuru.serviceLocator.getInstance(Webserver.class);
                // Register this plugins uri.
                webServer.registerWebserverCallback("/plugins/scrobble", scrobblerCallback);
            }
        }.start();

        return new String[]{"HelpPlugin",};
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof HelpPlugin) {
            HelpPlugin help = (HelpPlugin) plugin;
            help.addHelp(
                    "!np",
                    Flag.ANYONE,
                    "!np <Last.fm username> - Displays the last track played by the supplied Last.fm api.",
                    "!np register <Last.fm username> - Links the Last.fm username to your nick.",
                    "!np unregister <Last.fm username> - Unlinks the Last.fm username from your nick."
            );
        }
    }

    @Override
    public String getDescription() {
        return "Displays last.fm information.";
    }

    @Override
    public void incomingEvent(Event e) {
        PrivMsgEvent pme = (PrivMsgEvent) e;
        String message = pme.getMessage();

        if (message.startsWith("!np unregister")) {
            lastfm.handleUnregisterNickname(e);
        }
        else if (message.startsWith("!np register")) {
            lastfm.handleRegisterNickname(e);
        }
        else if (message.startsWith("!np")) {
            lastfm.handleNowPlaying(e);
        }
    }


    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

        @Override
    public void addWand(Wand wand) {
        this.wand = wand;
        // Create lastfm part when we get the wand.
        lastfm = new LastFM(wand);
    }
}
