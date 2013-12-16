package no.ntnu.online.onlineguru.plugin.plugins.github;

import com.google.gson.Gson;
import no.ntnu.online.onlineguru.plugin.plugins.github.listeners.GithubCallbackListeners;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Håvard Slettvold
 */
public class StorageManager {

    static Logger logger = Logger.getLogger(StorageManager.class);

    private final String database_file;

    public StorageManager(String database_file) {
        this.database_file = database_file;
    }

    protected GithubCallbackListeners loadListeners() {
        String json = null;
        GithubCallbackListeners result = null;

        try {
            json = SimpleIO.loadConfig(database_file).get("json");
        } catch (IOException e) {
            logger.error("Failed to load Github listener settings.");
        }

        if (json != null) {
            Gson gson = new Gson();
            result = gson.fromJson(json, GithubCallbackListeners.class);
        }

        return result;
    }

    protected String saveListeners(GithubCallbackListeners githubCallbackListeners) {
        Gson gson = new Gson();
        final String json = gson.toJson(githubCallbackListeners);

        try {
            SimpleIO.saveConfig(database_file, new HashMap<String,String>() {
                {
                    put("json", json);
                }
            });
        } catch (IOException e) {
            logger.error("Failed to save Github listener settings.", e.getCause());
        }

        return json;
    }

}
