package no.ntnu.online.onlineguru.plugin.plugins.np;

import de.umass.lastfm.Track;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.help.HelpPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.Alias;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.Scrobble;
import no.ntnu.online.onlineguru.plugin.plugins.np.storage.Storage;
import no.ntnu.online.onlineguru.service.services.webserver.Webserver;
import no.ntnu.online.onlineguru.utils.JSONStorage;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Håvard Slettvold
 */
public class NPPlugin implements PluginWithDependencies {

    static Logger logger = Logger.getLogger(NPPlugin.class);

    private Wand wand;

    private LastFM lastfm;
    private Storage storage;
    private ScrobblerCallback scrobblerCallback;

    public NPPlugin() {
    }

    @Override
    public String[] getDependencies() {
        // Doing this setup in a method that is solely called by the PluginManager once.
        // The reason is we do not want to execute these actions during tests.

        storage = (Storage) JSONStorage.load(storage.databaseFile, Storage.class);
        if (storage == null) {
            storage = new Storage();
        }
        lastfm = new LastFM(storage);
        scrobblerCallback = new ScrobblerCallback(storage);

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
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }

    @Override
    public void incomingEvent(Event e) {
        PrivMsgEvent pme = (PrivMsgEvent) e;
        String message = pme.getMessage();

        if (message.startsWith("!np unregister")) {
            handleUnregisterNickname(pme);
        }
        else if (message.startsWith("!np register")) {
            handleRegisterNickname(pme);
        }
        else if (message.startsWith("!np auth")) {
            handleAuth(pme);
        }
        else if (message.startsWith("!np")) {
            handleNowPlaying(pme);
        }
    }

    protected void handleRegisterNickname(PrivMsgEvent e) {
        String message = e.getMessage();
        String sender = e.getSender();
        String[] parameters = message.split("\\s+");

        if (parameters.length == 3) {
            Alias a = new Alias(e.getSender(), parameters[2]);
            if (storage.putAlias(sender, a)) {
                wand.sendMessageToTarget(e.getNetwork(), sender, "Your alias was registered successfully.");
            }
            else {
                wand.sendMessageToTarget(e.getNetwork(), sender, "Something went wrong with registering your last.fm nick");
            }
        }
    }

    protected void handleUnregisterNickname(PrivMsgEvent e) {
        String message = e.getMessage();
        String sender = e.getSender();
        String[] parameters = message.split("\\s+");

        if (parameters.length == 2) {
            if (storage.hasAlias(sender)) {
                if (storage.removeAlias(sender)) {
                    wand.sendMessageToTarget(e.getNetwork(), sender, "Your alias has been removed.");
                }
                else {
                    wand.sendMessageToTarget(e.getNetwork(), sender, "Something went wrong with unregistering your last.fm nick");
                }
            }
            else {
                wand.sendMessageToTarget(e.getNetwork(), sender, "You have not yet registered your nickname on last.fm");
            }
        }
    }

    protected void handleAuth(PrivMsgEvent e) {
        Alias alias = storage.getAlias(e.getSender());
        if (alias == null) alias = new Alias(e.getSender(), e.getSender());

        // Retrieve apikey from alias storage
        String apikey = alias.getApikey();
        // If it was empty, make one
        if (apikey.isEmpty()) {
            apikey = createApikey(alias.getNick());
            // If one was successfully created, store the new alias
            if (apikey != null) {
                Alias newAlias = new Alias(alias.getNick(), alias.getAlias(), apikey);
                storage.putAlias(alias.getNick(), newAlias);
            }
        }

        if (apikey == null) {
            wand.sendMessageToTarget(e.getNetwork(), e.getSender(), "There was an error while generating your apikey, please contact admins for more help.");
        }
        else {
            wand.sendMessageToTarget(e.getNetwork(), e.getSender(), "Your apikey is '" + apikey + "'. Copy and paste it into the Auth setting in Onlineguru Spotify Scrobbler.");
        }
    }

    protected void handleNowPlaying(PrivMsgEvent e) {
        String message = e.getMessage();
        String target = e.getTarget();
        String sender = e.getSender();
        Network network = e.getNetwork();
        Track lastfmTrack;

        String[] parameters = message.split("\\s+");

        String lookup = sender;
        if (parameters.length == 2) {
            lookup = parameters[1];
        }

        if (storage.hasAlias(lookup)) {
            lastfmTrack = lastfm.findRecentTrack(storage.getAlias(lookup).getAlias());
        }
        else {
            lastfmTrack = lastfm.findRecentTrack(lookup);
        }

        String artist = lastfmTrack.getArtist();
        String album = lastfmTrack.getAlbum();
        String song = lastfmTrack.getName();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:kk:ss");
        Date date = lastfmTrack.getPlayedWhen();

        String lastPlayedWhen = "";
        if (date != null) {
            lastPlayedWhen += " - Last played: " + sdf.format(date);
        }
        if (album != null && !album.isEmpty()) {
            album = " - Album: " + album;
        }

        wand.sendMessageToTarget(network, target, artist + " - " + song  + album + lastPlayedWhen);

        /*
        if (lastfmTrack.isNowPlaying()) {
            Scrobble scrobble = storage.getScrobble(sender);
        }
        else {
            DateTime lastfmDateTime = new DateTime(lastfmTrack.getPlayedWhen());
        }
        */
    }

    protected String createApikey(String nick) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();

            byte[] byteData = digest.digest(nick.getBytes("UTF-8"));
            System.out.println(byteData);
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException err) {
            logger.error("Algorithm for hashing does not exist.");
        } catch (UnsupportedEncodingException err) {
            logger.error("No such encoding (UTF-8).");
        }

        return null;
    }
}
