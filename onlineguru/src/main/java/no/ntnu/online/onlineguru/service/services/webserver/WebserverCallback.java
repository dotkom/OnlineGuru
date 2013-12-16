package no.ntnu.online.onlineguru.service.services.webserver;

import no.ntnu.online.onlineguru.service.services.webserver.NanoHTTPD.Method;

import java.util.Map;

/**
 * @author Roy Sindre Norangshol
 */
public interface WebserverCallback {
    public NanoHTTPD.Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files);
    public void httpdServerShutdown(String message);
}
