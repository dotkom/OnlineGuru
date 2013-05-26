package no.ntnu.online.onlineguru.plugin.plugins.github;

import no.fictive.irclib.event.container.Event;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.service.services.webserver.Webserver;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

/**
 * Original idea and implementation by Roy Sindre Norangshol
 *
 * @author Håvard Slettvold
 */
public class GithubPlugin implements PluginWithDependencies {

    static Logger logger = Logger.getLogger(GithubPlugin.class);

    private Wand wand;
    private FlagsPlugin flagsPlugin;

    private GithubCallback githubCallback;


    public GithubPlugin() {

    }

    private void registerWithWebserver() {
        githubCallback = new GithubCallback();

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
    public String getDescription() {
        return "This plugin hooks into Githubs webhooks to announce changes in repositories.";
    }

    @Override
    public String[] getDependencies() {
        return new String[]{"FlagsPlugin", };
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) {
            this.flagsPlugin = (FlagsPlugin) plugin;
        }
    }


    @Override
    public void incomingEvent(Event e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
