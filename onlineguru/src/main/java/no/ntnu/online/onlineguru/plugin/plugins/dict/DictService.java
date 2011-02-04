package no.ntnu.online.onlineguru.plugin.plugins.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.codehaus.httpcache4j.util.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Roy Sindre Norangshol <Rockj>
 */
public class DictService {

    private final String SCHEME = "http";
    private final String HOST = "annnon";
    private final String PATH_LOOKUP = "/lookup/";
    private final String PATH_DICTS = "/languages/";
    public Vector<String> languages;
    private final UsernamePasswordCredentials UPC = new UsernamePasswordCredentials("keke", "kekek");

    public DictService() {
        languages = new Vector<String>();
        getDictionaries();
    }

    private void getDictionaries() {
        try {
            languages.clear();
            HttpResponse response = httpGet(urlEncodeDictionaries(), UPC);
            String json = readString(response.getEntity().getContent());
            System.out.println(json);
            JSONArray items = null;
            if (json != null && json.startsWith("{")) {
                try {
                    JSONObject j = new JSONObject(json);
                    items = j.getJSONArray("languages");
                    j = null;
                    for (int i = 0; i < items.length(); i++) {
                        j = items.getJSONObject(i);
                        languages.add(j.getString("language"));
                    }
                } catch (JSONException ex) {
                    throw new InternalError("Could not parse JSON and load dictionaries..");
                }
            }
        } catch (IOException ex) {
            throw new InternalError("Could not contact service, is service down?");
        } catch (URISyntaxException ex) {
            throw new InternalError("Please define a correct service URL in settings..");
        }

    }

    public String lookup(String dict, String word) {
        if (dict != null && !languages.contains(dict.toUpperCase())) {
            String ret = "Du må velge en gyldig ordbok, gyldige ordbøker: ";
            for (String language : languages) {
                ret += language + ", ";
            }
            return ret.substring(0, ret.length() - 2);
        }
        if (dict == null) {
            dict = "NO-UK";
        }
        try {
            HttpResponse response = httpGet(urlEncodeLookup(dict, word), UPC);
            String responsetext = readString(response.getEntity().getContent());
            return "[" + dict.toUpperCase() + "] " + convertJsonIntoResult(responsetext).getTranslation(word);
        } catch (IOException ex) {
            return "Klarte ikke å kontakte tjeneren, kanskje tjeneren er nede? Prøv igjen kanskje?";
        } catch (URISyntaxException ex) {
            return "Feil oppstod ved spørring, noe er obiously feil i koden...";
        }
    }

    public String lookup(String word) {
        return lookup(null, word);
    }

    private URI urlEncodeLookup(String dict, String word) throws URISyntaxException, MalformedURLException {
        URIBuilder builder = URIBuilder.fromURI(new URI(SCHEME, HOST, PATH_LOOKUP, null));
        builder = builder.addParameter("dict", dict);
        builder = builder.addParameter("word", word);
        return builder.toURI();
    }

    private URI urlEncodeDictionaries() throws URISyntaxException, MalformedURLException {
        return new URI(SCHEME, HOST, PATH_DICTS, null);
    }

    private Result convertJsonIntoResult(String json) {
        Result results = new Result();
        JSONArray items = null;


        if (json != null && json.startsWith("{")) {
            try {
                JSONObject j = new JSONObject(json);
                items = j.getJSONArray("result");
                j = null;
                for (int i = 0; i < items.length(); i++) {
                    j = items.getJSONObject(i);
                    results.addTranslationToWord(j.getString("word"), j.getString("translation").replaceAll("<br>", "\n"));
                }
            } catch (JSONException ex) {
                Logger.getLogger(DictService.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return results;
    }

    private HttpResponse httpGet(URI uri, UsernamePasswordCredentials creds) throws IOException, URISyntaxException {

        DefaultHttpClient httpClient = new DefaultHttpClient();

        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(uri.getHost(), uri.getPort()),
                creds);

        HttpGet httpGet = new HttpGet(uri);

        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("User-Agent", "OnlineGuru/0.1");

        return httpClient.execute(httpGet);
    }

    protected String readString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        DictService ds = new DictService();
        System.out.println(ds.lookup("kake"));
    }
}
