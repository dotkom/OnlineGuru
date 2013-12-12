package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import no.fictive.irclib.event.container.Event;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners.Listeners;
import no.ntnu.online.onlineguru.service.services.webserver.Webserver;
import no.ntnu.online.onlineguru.utils.Wand;

/**
 * @author Håvard Slettvold
 */
public class MailAnnouncerPlugin implements PluginWithDependencies {

    private Wand wand;

    private MailCallback mailCallback;
    private Listeners listeners;

    @Override
    public String[] getDependencies() {
        // Doing this setup in a method that is solely called by the PluginManager once.
        // The reason is we do not want to execute these actions during tests.
//        listeners = storageManager.loadListeners();
//        if (listeners == null) {
//            listeners = new Listeners();
//        }
//        githubCallback = new GithubCallback(wand, listeners);
        mailCallback = new MailCallback(wand, listeners);

        // Running the registering with the web server async, it may take time.
        new Thread() {
            @Override
            public void run() {
                // Fetch the webserver instance.
                Webserver webServer = OnlineGuru.serviceLocator.getInstance(Webserver.class);
                // Register this plugins uri.
                webServer.registerWebserverCallback("/plugins/mail", mailCallback);
            }
        }.start();

        // Return dependencies.
        return new String[]{"FlagsPlugin", "HelpPlugin", };
    }

    @Override
    public void loadDependency(Plugin plugin) {

    }

    @Override
    public String getDescription() {
        return "Takes requests from the email-script running on dworek and announces incoming mail on irc.";
    }

    @Override
    public void incomingEvent(Event e) {

    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {

    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
