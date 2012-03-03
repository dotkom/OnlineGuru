package no.ntnu.online.onlineguru.utils.urlreader.connection;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The purpose of this factory is to produce URL objects from url-Strings.
 * It will handle HttpURLConnection responsecodes, and attempt to find a valid encoding for the URL.
 *
 * @author Håvard Slettvold
 */


public class URLFactory {

    static Logger logger = Logger.getLogger(URLFactory.class);
    private String urltext;
    private URL url;
    private HttpURLConnection conn;
    private boolean connected = false;
    private Charset charset = null;

    public URLFactory(String urltext) {
        this.urltext = urltext;
    }

    public void start() {
        connect();
        findCharset();
    }

    private void connect() {
        try {
            url = new URL(urltext);
            conn = (HttpURLConnection) url.openConnection();

            int code = conn.getResponseCode();
            switch (code) {
                case HttpURLConnection.HTTP_OK:
                    connected = true;
                    return;
                case HttpURLConnection.HTTP_ACCEPTED:
                    connected = true;
                    return;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    logger.error("Error: 400 Bad Request");
                    return;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    logger.error("Error: 401 Unauthorized");
                    return;
                case HttpURLConnection.HTTP_PAYMENT_REQUIRED:
                    logger.error("Error: 402 Payment Required");
                    return;
                case HttpURLConnection.HTTP_FORBIDDEN:
                    logger.error("Error: 403 Forbidden");
                    return;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    logger.error("Error: 404 Not found");
                    return;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    logger.error("Error: 500 Internal error");
                    return;
                case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
                    logger.error("Error: 501 Not implemented");
                    return;
                case HttpURLConnection.HTTP_BAD_GATEWAY:
                    logger.error("Error: 502 Bad Gateway");
                    return;
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    logger.error("Error: 503 Unavailable");
                    return;
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    logger.error("Error: 504 Gateway timeout");
                    return;
                case HttpURLConnection.HTTP_VERSION:
                    logger.error("Error: 505 HTTP Version Not Supported");
                    return;
                default:
                    logger.error("Unknown HTML code: "+code);
                    return;
            }

        } catch (MalformedURLException e) {
            logger.error("Error: malformed URL", e.getCause());
        } catch (IOException e) {
            logger.error("Error: IOException", e.getCause());
        }

    }

    private void findCharset() {
        if (connected) {

            String cs = "";
            int i = 1;

            // Declared last in the while and before it so that the check
            // for null never affects the loops contents.
            String header = conn.getHeaderFieldKey(i);
            String value = conn.getHeaderField(i);

            while (header != null && value != null) {

                // Small print to show headers and their values. For debugging.
                // System.out.println("Header: "+header+" Value: "+value);

                if (header.equals("Content-Type")) {
                    if (value.contains("=")) {
                        cs = value.split("=")[1];
                    }
                }

                header = conn.getHeaderFieldKey(i);
                value = conn.getHeaderField(i);
                i++;
            }

            if (!cs.isEmpty()) {
                try {
                    charset = Charset.forName(cs.toUpperCase());
                } catch (IllegalCharsetNameException e) {
                    logger.debug("Headers contained an Illegal charset, looking in source. Charset: '"+cs+"' URL: '"+urltext+"'");
                    charset = null;
                    findCharsetFromSource();
                } catch (UnsupportedCharsetException e) {
                    logger.debug("Headers contained an Unsupported charset, looking in source. Charset: '"+cs+"' URL: '"+urltext+"'");
                    charset = null;
                    findCharsetFromSource();
                }
            }
            else {
                logger.debug("Headers did not contain a charset, looking in source. URL: '"+urltext+"'");
                findCharsetFromSource();
            }

            if (charset == null) {
                logger.debug("No valid charset obtained. URL: '"+urltext+"'");
            }
        }
        else {
            logger.error("Not connected. Can't retrieve charset. URL: '"+urltext+"'");
        }
    }

    private void findCharsetFromSource() {
        String cs = "";
        try {

            Pattern pattern = Pattern.compile(xmlRegex() + "|" + htmlRegex(), Pattern.CASE_INSENSITIVE);
            Matcher matcher;
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                matcher = pattern.matcher(inputLine);
                if (matcher.find()) {
                    if (matcher.group(1) == null) {
                        cs = matcher.group(2);
                    }
                    else {
                        cs = matcher.group(1);
                    }
                }
                if (inputLine.contains("</head>")) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Read error while trying to read from URL. URL: '"+urltext+"'", e.getCause());
        }

        if (!cs.isEmpty()) {
            try {
                charset = Charset.forName(cs.toUpperCase());
            } catch (IllegalCharsetNameException e) {
                logger.debug("Source contained no valid charset. URL: '"+urltext+"'");
                charset = null;
            } catch (UnsupportedCharsetException e) {
                logger.debug("Source contained no valid charset. URL: '"+urltext+"'");
                charset = null;
            }
        }
    }

    public URL getURL() {
        return url;
    }

    public HttpURLConnection getConnection() {
        return conn;
    }

    public Charset getCharset() {
        return charset;
    }
    
    public String xmlRegex() {
        return "<\\?xml version=\"[^\"]+\" encoding=\"([^\"]+)\"\\?>";
    }

    public String htmlRegex() {
        return "<meta (?:http-equiv=\"Content-Type\" content=\"text/html; )?charset=(?:\"|')?([^\"']+)(?:\"|') ?/?>";
    }
    
}
