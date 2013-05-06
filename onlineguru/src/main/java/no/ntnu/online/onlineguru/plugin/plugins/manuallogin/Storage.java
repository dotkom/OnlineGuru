package no.ntnu.online.onlineguru.plugin.plugins.manuallogin;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Storage {

    private static final Path databasePath = Paths.get("database", "manual_login");
    private static final Logger logger = Logger.getLogger(Storage.class);


    protected static void saveNetworkLoginDatabase(Network network, Map<String, String> networkLogins) {

        try {
            ensureDirectoryStructureIsIntact(network);

            Path networkLoginPath = getNetworkDatabasePath(network);
            SimpleIO.saveConfig(networkLoginPath.toString(), networkLogins);
        }
        catch(IOException e) {
            logger.warn(String.format("Could not save login database for %s", network.getServerAlias()));
        }
    }

    protected static Map<String, String> loadNetworkLoginDatabase(Network network) {

        try {
            ensureDirectoryStructureIsIntact(network);

            Path networkLoginPath = getNetworkDatabasePath(network);
            return SimpleIO.loadConfig(networkLoginPath.toString());
        }
        catch (IOException e) {
            logger.warn(String.format("Could not load login database for %s", network.getServerAlias()));
        }

        return new HashMap();
    }

    private static Path getNetworkDatabasePath(Network network) {

        return Paths.get(databasePath.toString(), network.getServerAlias() + ".db");
    }

    private static void ensureDirectoryStructureIsIntact(Network network) throws IOException {

        if (!Files.exists(databasePath)) {
            Files.createDirectories(databasePath);
        }

        if (!Files.exists(getNetworkDatabasePath(network))) {
            createDefaultRootUserPassword(network);
            return;
        }

        Map<String, String> logins = SimpleIO.loadConfig(getNetworkDatabasePath(network).toString());

        if(!logins.containsKey("root"))
            createDefaultRootUserPassword(network);
    }

    private static void createDefaultRootUserPassword(Network network) throws IOException {
        SimpleIO.writelineToFile(getNetworkDatabasePath(network).toString(),
                                 String.format("%s=%s", Settings.defaultRootUser, Settings.defaultRootPassword));

        logger.info(String.format("Login database for %s loaded with default password '%s'",
                                  network.getServerAlias(), Settings.defaultRootPassword));
    }
}
