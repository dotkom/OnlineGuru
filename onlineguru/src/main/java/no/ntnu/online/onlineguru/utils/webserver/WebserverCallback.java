package no.ntnu.online.onlineguru.utils.webserver;

import java.util.Properties;

/**
 * @author Roy Sindre Norangshol
 */
public interface WebserverCallback {
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files);
    public void httpdServerShutdown(String message);
}
