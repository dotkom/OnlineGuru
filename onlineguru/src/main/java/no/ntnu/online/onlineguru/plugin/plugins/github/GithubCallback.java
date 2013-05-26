package no.ntnu.online.onlineguru.plugin.plugins.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.ntnu.online.onlineguru.plugin.plugins.git.IRCAnnounce;
import no.ntnu.online.onlineguru.plugin.plugins.github.model.GithubJSONPayload;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.service.services.webserver.Response;
import no.ntnu.online.onlineguru.service.services.webserver.WebserverCallback;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * @author Håvard Slettvold
 */
public class GithubCallback implements WebserverCallback {

    static Logger logger = Logger.getLogger(GithubCallback.class);

    private Wand wand;
    private Gson gson;

    public GithubCallback(Wand wand) {
        this.wand = wand;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .create();
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        // make payload to get IRCAnnounce
        logger.debug("Request received from webserver.");

        GithubJSONPayload payload = null;
        if (method.equalsIgnoreCase("POST") && parms.containsKey("payload")) {
            payload = gson.fromJson(parms.getProperty("payload"), GithubJSONPayload.class);
        }

        if (payload != null) {
            System.out.println(payload);
//            if (announceHashMap.containsKey(payload.getIdentifier())) {
//                IRCAnnounce announce = announceHashMap.get(payload.getIdentifier());
//                IRCAnnounce toAnnounce = new IRCAnnounce(payload, announce.getAnnounceToChannels());
//                announceToIRC(toAnnounce);
//            } else {
//                logger.error(String.format("Missing announce settings for %s with payload %s", payload.getIdentifier(), payload.toString()));
//            }
        }

        return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_PLAINTEXT, "OK");
    }

    @Override
    public void httpdServerShutdown(String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
