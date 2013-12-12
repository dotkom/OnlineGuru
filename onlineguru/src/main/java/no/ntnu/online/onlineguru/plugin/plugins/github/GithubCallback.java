package no.ntnu.online.onlineguru.plugin.plugins.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.Listeners;
import no.ntnu.online.onlineguru.plugin.plugins.github.model.GithubPayload;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.*;
import no.ntnu.online.onlineguru.service.services.webserver.WebserverCallback;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Properties;

/**
 * @author Håvard Slettvold
 */
public class GithubCallback implements WebserverCallback {

    static Logger logger = Logger.getLogger(GithubCallback.class);

    private Wand wand;
    private Gson gson;

    private Listeners listeners;

    public GithubCallback(Wand wand, Listeners listeners) {
        this.wand = wand;
        this.listeners = listeners;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .create();
    }

    @Override
    public Response serve(String uri, Method method,  Map<String, String> headers, Map<String, String> parms) {
        // make payload to get IRCAnnounce
        logger.debug("Request received from webserver.");

        GithubPayload payload = null;
        if (method.name().equalsIgnoreCase("POST") && parms.containsKey("payload")) {
            try {
                payload = gson.fromJson(parms.get("payload"), GithubPayload.class);
            } catch (JsonParseException e) {
                logger.error("Failed to parse JSON from "+uri, e.getCause());
                logger.error(parms.get("payload"));
            }
        }

        if (payload != null) {
            String repository = payload.getIdentifier().toLowerCase();
            if (listeners.containsKey(repository)) {
                listeners.get(repository).incomingPayload(this, payload);
            }
            else {
                logger.debug(String.format("No listener for repository %s", payload.getIdentifier()));
            }
        }

        return new Response(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "OK");
    }

    @Override
    public void httpdServerShutdown(String message) {
        logger.error(message);
    }

    public void announceToIRC(String network, String channel, String output) {
        wand.sendMessageToTarget(wand.getNetworkByAlias(network), channel, output);
    }

}
