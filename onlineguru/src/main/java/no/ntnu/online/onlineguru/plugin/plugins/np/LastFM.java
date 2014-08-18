package no.ntnu.online.onlineguru.plugin.plugins.np;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import de.umass.lastfm.Caller;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.Alias;
import no.ntnu.online.onlineguru.plugin.plugins.np.storage.Storage;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;


public class LastFM {
    static Logger logger = Logger.getLogger(LastFM.class);

    private String apikey = null;
    private final String settings_folder = "settings/";
    private final String settings_file = settings_folder + "lastfm.conf";
    private final String database_folder = "database/";
    private final String database_file = database_folder + "lastfm.db";

    private Map<String, String> usernameMapping = new HashMap<String, String>();
    private Storage storage;

    public LastFM(Storage storage) {
        this.storage = storage;
        initiate();
        Caller.getInstance().setUserAgent("onlineguru");
    }

    private void initiate() {
        try {
            SimpleIO.createFolder(database_folder);
            SimpleIO.createFile(database_file);
            SimpleIO.createFile(settings_file);
            usernameMapping = SimpleIO.loadConfig(database_file);
            apikey = SimpleIO.loadConfig(settings_file).get("apikey");

            if (apikey == null) {
                SimpleIO.writelineToFile(settings_file, "apikey=");
                logger.error("Lastfm.conf is not configured correctly");
            }
            else if (apikey.isEmpty()) {
                logger.error("Lastfm API key is empty");
            }

            // Remove this now, should be gone already.
            Iterator it = usernameMapping.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                storage.putAlias((String)pair.getKey(), new Alias((String)pair.getKey(), (String)pair.getValue()));
                it.remove();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Track findRecentTrack(String username) {
        PaginatedResult<Track> pagedTracks = User.getRecentTracks(username, apikey);
        Collection<Track> tracks = pagedTracks.getPageResults();

        if (tracks.size() > 0) {
            for (Track track : tracks) {


                return track;
            }
        }

        return null;
    }

}
