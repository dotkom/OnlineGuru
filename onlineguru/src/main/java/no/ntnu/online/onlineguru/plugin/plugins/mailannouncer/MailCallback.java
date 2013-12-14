package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners.Listeners;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.model.Mail;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.Method;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.Response;
import no.ntnu.online.onlineguru.service.services.webserver.WebserverCallback;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.Map;
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
    public Response serve(String uri, Method method,  Map<String, String> headers, Map<String, String> parms) {
        Mail mail = null;
        if (method == Method.POST && parms.containsKey("payload")) {
            try {
                gson.fromJson(parms.get("payload"), Mail.class);
            } catch (JsonParseException e) {
                logger.error("Failed to parse JSON from "+uri, e.getCause());
                logger.error(parms.get("payload"));
            }
        }

        if (mail != null) {
            String mailinglist = mail.getMailinglist();
            if (listeners.containsKey(mailinglist)) {
                listeners.get(mailinglist).incomingMail(this, mail);
            }
            else {
                logger.debug(String.format("No subscriptions for mailinglist '%s'", mail.getMailinglist()));
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
