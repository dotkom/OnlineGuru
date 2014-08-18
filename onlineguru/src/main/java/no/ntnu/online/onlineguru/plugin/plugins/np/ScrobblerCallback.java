package no.ntnu.online.onlineguru.plugin.plugins.np;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.xml.internal.ws.util.StringUtils;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.Alias;
import no.ntnu.online.onlineguru.plugin.plugins.np.model.Scrobble;
import no.ntnu.online.onlineguru.plugin.plugins.np.storage.Storage;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.*;
import no.ntnu.online.onlineguru.service.services.webserver.WebserverCallback;
import no.ntnu.online.onlineguru.utils.JSONStorage;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class ScrobblerCallback implements WebserverCallback {

    static Logger logger = Logger.getLogger(ScrobblerCallback.class);

    private Gson gson = new Gson();

    private Storage storage;

    public ScrobblerCallback(Storage storage) {
        this.storage = storage;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .create();
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        Scrobble scrobble = null;
        if (Method.POST.equals(method)) {
            String scrobbleJSON = parms.keySet().iterator().next();
            System.out.println("JSON: "+scrobbleJSON);
            if (scrobbleJSON != null && !scrobbleJSON.isEmpty()) {
                try {
                    scrobble = gson.fromJson(scrobbleJSON, Scrobble.class);
                } catch (JsonParseException e) {
                    logger.error("Failed to parse JSON from " + uri, e.getCause());
                    logger.error(parms.get("payload"));
                }
            }
        }

        if (scrobble != null) {

            String apikey = scrobble.getAuth();
            // if there is no apikey, it's an invalid call
            if (apikey == null) return forbidden("No apikey found");

            Alias alias = storage.getAliasByApikey(apikey);
            // If no alias was found, the request was illegal
            if (alias == null) return forbidden("No matching apikey found");

            storage.putScrobble(alias.getNick(), scrobble);
        }

        return new Response(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "OK");
    }

    @Override
    public void httpdServerShutdown(String message) {

    }

    private Response forbidden(String text) {
        return new Response(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, "Unauthorized: "+text);
    }
}
