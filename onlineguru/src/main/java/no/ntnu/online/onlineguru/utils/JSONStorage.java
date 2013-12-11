package no.ntnu.online.onlineguru.utils;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Håvard Slettvold
 */
public class JSONStorage {

    static Logger logger = Logger.getLogger(JSONStorage.class);

    public static boolean save(String filename, Object object) {
        Gson gson = new Gson();
        final String json = gson.toJson(object);
        try {
            SimpleIO.writelineToFile(filename, json);
            return true;
        } catch (IOException e) {
            logger.error("Failed to save JSON to "+ filename +".", e.getCause());
            return false;
        }
    }

    public static Object load(String filename, Class c) {
        String json = null;
        try {
            json = SimpleIO.readFileAsString(filename);
        } catch (IOException e) {
            logger.error("Failed to load JSON from "+ filename +".", e.getCause());
        }

        if (json != null) {
            Gson gson = new Gson();
            Object result = gson.fromJson(json, c);
            return result;
        }
        return null;
    }

}
