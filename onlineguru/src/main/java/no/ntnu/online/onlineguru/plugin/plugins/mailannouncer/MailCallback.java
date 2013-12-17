package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners.MailCallbackManager;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.model.Mail;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.Method;
import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.Response;
import no.ntnu.online.onlineguru.service.services.webserver.WebserverCallback;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class MailCallback implements WebserverCallback {

    static Logger logger = Logger.getLogger(MailCallback.class);

    private Gson gson;
    private Wand wand;

    private MailCallbackManager mailCallbackManager;

    public MailCallback(Wand wand, MailCallbackManager mailCallbackManager) {
        this.wand = wand;
        this.mailCallbackManager = mailCallbackManager;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .create();
    }

    @Override
    public Response serve(String uri, Method method,  Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        Mail mail = null;
        if (Method.POST.equals(method) && parms.containsKey("payload")) {
            logger.debug("Mail paload: "+parms.get("payload"));
            try {
                mail = gson.fromJson(parms.get("payload"), Mail.class);
            } catch (JsonParseException e) {
                logger.error("Failed to parse JSON from "+uri, e.getCause());
                logger.error(parms.get("payload"));
            }
        }

        if (mail != null) {
            mail.setMailinglistAlias(mailCallbackManager.getAlias(mail.getMailinglist()));
            // Find the lookup value.
            // If there is an alias, it will be used, if not the mailinglist's name will be used.
            // If there was no mailinglist in the mail either, the 'to' email will be used.
            String lookupValue = mail.getLookupValue();
            if (mailCallbackManager.containsKey(lookupValue)) {
                mailCallbackManager.get(lookupValue).incomingMail(this, mail);
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
