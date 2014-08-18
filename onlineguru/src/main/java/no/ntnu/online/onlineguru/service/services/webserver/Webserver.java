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
        super(9875);
        services = new HashMap<String, WebserverCallback>();
        this.start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        logger.debug("---- Incoming webserver call");
        logger.debug("URI: " + session.getUri());
        logger.debug("Method: "+session.getMethod());
        logger.debug("Headers: "+session.getHeaders());

        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (ResponseException re) {
                return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
            }
        }

        logger.debug("Parms: "+session.getParms());
        logger.debug("-----------------------------");

        if (services.containsKey(session.getUri())) {
            return services.get(session.getUri()).serve(session.getUri(), method, session.getHeaders(), session.getParms(), files);
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

