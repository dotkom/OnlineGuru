package no.ntnu.online.onlineguru.plugin.plugins.middag;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Håvard Slettvold
 */

public class UpdateMenu implements Runnable {

    static Logger logger = Logger.getLogger(UpdateMenu.class);
    private MiddagPlugin middagPlugin;
    private String kantine;

    private final String URLTEXT = "https://www.sit.no/ajaxdinner/get";

    public UpdateMenu(MiddagPlugin middagPlugin, String kantine) {
        this.middagPlugin = middagPlugin;
        this.kantine = kantine;

        new Thread(this).start();
    }

    public void run() {
        StringBuilder sb = new StringBuilder();

        // This posts the required fields to the sit.no RESTful menu api.
        try {
            // Prepare post data
            String postData = "diner="+ kantine.toLowerCase() +"&trigger=single";

            // Open connection
            java.net.URL url = new URL(URLTEXT);
            URLConnection conn = url.openConnection();

            // Write the post data
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(postData);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            wr.close();
            rd.close();
        } catch (IOException e) {
            logger.error("Failed to get JSON data from "+URLTEXT, e.getCause());
        }

        // Get the html content into a clean string
        String json = new String(sb);
        json = StringEscapeUtils.unescapeJava(json);
        String html = json.split(":")[1].replaceAll("^\"|\"}$", "");

        System.out.println(html);

        // Traverse html with Jsoup
        String menu = findMenuItems(html);

        setMenu(kantine, menu);
    }

    protected static String findMenuItems(String html) {
        Document doc = Jsoup.parse(html);
        String menu = "";

        Element ul = doc.select("ul").first();

        for (Element li : ul.children()) {
            menu += li.select(".food").text() + " " + li.select(".price").text() + " ";
        }

        return menu;
    }

    private void setMenu(String kantine, String menu) {
        switch (Kantiner.valueOf(kantine)) {
            case HANGAREN: {
                middagPlugin.setHangaren(menu);
                break;
            }
            case REALFAG: {
                middagPlugin.setRealfag(menu);
                break;
            }
        }
    }

}