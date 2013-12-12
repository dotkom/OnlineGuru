package no.ntnu.online.onlineguru.service.services.webserver;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roy Sindre Norangshol
 */
public class Webserver extends NanoHTTPD {

    Map<String, WebserverCallback> services;
    static Logger logger = Logger.getLogger(Webserver.class);

    public Webserver() throws IOException {
        super("localhost", 9875);
        services = new HashMap<String, WebserverCallback>();
        this.start();
    }

    @Override
    public Response serve(HTTPSession session) {
        logger.debug(String.format("uri: %s, method: %s", session.getUri(), session.getMethod().name()));
        if (services.containsKey(session.getUri())) {
            return services.get(session.getUri()).serve(session.getUri(), session.getMethod(), session.getHeaders(), session.getParms());
        }

        return new Response(Response.Status.FORBIDDEN, MIME_PLAINTEXT, "No access");
    }

    public boolean registerWebserverCallback(String uri, WebserverCallback callback) {
        if (!services.containsKey(uri)) {
            services.put(uri, callback);
            return true;
        } else
            return false;
    }

}

