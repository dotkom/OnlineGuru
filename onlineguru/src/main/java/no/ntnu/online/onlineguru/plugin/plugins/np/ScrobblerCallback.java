package no.ntnu.online.onlineguru.plugin.plugins.np;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.Scrobble;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.ScrobbleStorage;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.*;
import no.ntnu.online.onlineguru.service.services.webserver.WebserverCallback;
import no.ntnu.online.onlineguru.utils.JSONStorage;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class ScrobblerCallback implements WebserverCallback {

    static Logger logger = Logger.getLogger(ScrobblerCallback.class);

    private Gson gson = new Gson();

    private ScrobbleStorage scrobbleStorage;

    public ScrobblerCallback(ScrobbleStorage scrobbleStorage) {
        this.scrobbleStorage = scrobbleStorage;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .create();
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        Scrobble scrobble = null;
        if (Method.POST.equals(method) && parms.containsKey("payload")) {
            try {
                scrobble = gson.fromJson(parms.get("payload"), Scrobble.class);
            } catch (JsonParseException e) {
                logger.error("Failed to parse JSON from "+uri, e.getCause());
                logger.error(parms.get("payload"));
            }
        }

        if (scrobble != null) {
            scrobbleStorage.put("hurr", scrobble);
            JSONStorage.save(ScrobbleStorage.database_file, scrobbleStorage);
        }

        return new Response(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "OK");
    }

    @Override
    public void httpdServerShutdown(String message) {

    }
}
