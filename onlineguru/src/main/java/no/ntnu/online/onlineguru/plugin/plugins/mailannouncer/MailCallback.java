package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners.Listeners;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.service.services.webserver.Response;
import no.ntnu.online.onlineguru.service.services.webserver.WebserverCallback;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * @author Håvard Slettvold
 */
public class MailCallback implements WebserverCallback {

    static Logger logger = Logger.getLogger(MailCallback.class);

    private Gson gson;
    private Wand wand;

    private Listeners listeners;

    public MailCallback(Wand wand, Listeners listeners) {
        this.wand = wand;
        this.listeners = listeners;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .create();
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        System.out.println("Received mail call");


        return new Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_PLAINTEXT, "OK");
    }

    @Override
    public void httpdServerShutdown(String message) {
        logger.error(message);
    }

}
