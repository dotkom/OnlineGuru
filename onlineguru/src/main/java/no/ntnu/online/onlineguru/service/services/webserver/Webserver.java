package no.ntnu.online.onlineguru.service.services.webserver;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Roy Sindre Norangshol
 */
public class Webserver extends NanoHTTPD {
    Map<String, WebserverCallback> services;
    static Logger logger = Logger.getLogger(Webserver.class);


    public Webserver() throws IOException {
        super(9875);
        services = new HashMap<String, WebserverCallback>();
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        logger.debug(String.format("uri: %s, method: %s, header: %s, parms: %s, files: %s", uri, method, header, parms, files));
        if (services.containsKey(uri)) {
            return services.get(uri).serve(uri, method, header, parms, files);
        }
        return new Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, "No access");

     }

    public boolean registerWebserverCallback(String uri, WebserverCallback callback) {
        if (!services.containsKey(uri)) {
            services.put(uri, callback);
            return true;
        } else
            return false;
    }

}

