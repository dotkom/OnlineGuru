package no.ntnu.online.onlineguru.plugin.plugins.karma.logging;

import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Håvard Slettvold
 */
public class KarmaLogger {

    static Logger logger = Logger.getLogger(KarmaLogger.class);

    private final String log_file;

    public KarmaLogger(String log_file) {
        this.log_file = log_file;

        try {
            SimpleIO.createFile(log_file);
        } catch (IOException e) {
            logger.error("Failed to create log file for KarmaLogger.", e.getCause());
        }
    }

    public void appendLog(String sender, String target, int amount) {
        try {
            String line = String.format("%s;%s;%d\n", sender, target, amount);
            SimpleIO.appendToFile(log_file, line);
        } catch (IOException e) {
            logger.error("Failed to write line to log file.", e.getCause());
        }
    }
}
