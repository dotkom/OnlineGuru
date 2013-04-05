package no.ntnu.online.onlineguru.plugin.plugins.flags.storage;

import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Håvard Slettvold
 */
public class DBHandler {
    static Logger logger = Logger.getLogger(DBHandler.class);

    private Enumeration<Network> networks;

    private Connection conn;
    private static final String database_folder = "database/";
    private static final String flags_database_folder = database_folder + "flags/";
    private static final String flags_settings_folder = "settings/";
    private static final String flags_settings_file = flags_settings_folder + "flags.conf";

    private String root_username = null;
    private String root_password = null;

    public DBHandler() {
        /* Make sure the JDBC for sqlite is ready */
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        verifySettings();
    }

    private void verifySettings() {
        SimpleIO.createFolder(flags_database_folder);

        try {
            Map<String, String> settings = SimpleIO.loadConfig(flags_settings_file);
            root_username = settings.get("admin");
            root_password = settings.get("password");

            if (root_username == null) {
                SimpleIO.appendLineToFile(flags_settings_file, "root_username=");
            }
            if (root_password == null) {
                SimpleIO.appendLineToFile(flags_settings_file, "root_password=");
            }
            if (root_password == null || root_username == null) {
                logger.error("Flags configured incorrectly. Check flags.conf.");
            }
            if (root_password != null && root_password.isEmpty()) {
                logger.error("Flags root username not specified.");
            }
            if (root_username != null && root_username.isEmpty()) {
                logger.error("Flags root password not specified.");
            }

        } catch (IOException ioe) {
            logger.error("Failed to read settings file.", ioe.getCause());
        }
    }

    /*
     * Helper methods
     */

    public String getDBNetworkPath(Network network) {
        return flags_database_folder + network.getServerAlias() + ".db";
    }

    public void initiate(Network network) throws IOException {
        SimpleIO.createFile(getDBNetworkPath(network));
        createSuperuserTable(network);
        if (!isSuperuser(network, root_username)) {
            createSuperuser(network, root_username, root_password);
        }
    }

    public String shaHex(String s) {
        return DigestUtils.shaHex(s);
    }

    public String getAndCleanChannel(String channel) {
        return channel.replaceAll("#", "");
    }

    /*
     * DB Handling
     */

    public Connection connect(Network network) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + getDBNetworkPath(network));
        } catch (SQLException e) {
            logger.error("Could not connect to sqlite", e.getCause());
        }
        return null;
    }

    public void disconnect() {
        try {
            if(conn != null)
                conn.close();
        } catch(SQLException e) {
            logger.error("Could not disconnect sqlite", e.getCause());
        }
    }

    // Works
    public void createSuperuserTable(Network network) {
        try {
            conn = connect(network);
            conn.setAutoCommit(true);
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS Superuser(username TEXT PRIMARY KEY, password TEXT);");
        } catch(SQLException e) {
            logger.error(String.format("Failed to create Superuser table for %s.", network), e.getCause());
        }
        disconnect();
    }

    // Works
    public void createSuperuser(Network network, String username, String password) {
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Superuser (username, password) VALUES (?, ?);");
            ps.setString(1, username);
            ps.setString(2, shaHex(password));
            ps.execute();

        } catch (SQLException e) {
            logger.error(String.format("Failed to create Superuser for %s.", network.getServerAlias()), e.getCause());
        }
        disconnect();
    }

    // TODO test
    public boolean removeSuperuser(Network network, String username) {
        boolean success = false;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Superuser WHERE username=?;");
            ps.setString(1, username);
            ps.execute();
            success = true;
        } catch (SQLException e) {
            logger.error(String.format("Failed to remove Superuser from %s.", network.getServerAlias()), e.getCause());
            success = false;
        }
        disconnect();
        return success;
    }

    public String getFlags(Network network, String username) {
        return null;
    }

    // Works
    public boolean isSuperuser(Network network, String username) {
        boolean success = false;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Superuser WHERE username=?;");
            ps.setString(1, username);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                success = true;
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to check if user %s is a superuser.", username), e.getCause());
            success = false;
        }

        disconnect();
        return success;
    }

    // Works
    public void createChannel(JoinEvent e) {
        String channel = getAndCleanChannel(e.getChannel());

        try {
            conn = connect(e.getNetwork());
            conn.setAutoCommit(true);
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + channel + "(username TEXT PRIMARY KEY, flags TEXT);");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            logger.error(String.format("Failed to create channel '%s' in DB.", e.getChannel()), sqle.getCause());
        }
        disconnect();
    }

    public boolean userExistsInChannel(Network network, String channel, String username) {
        String c = getAndCleanChannel(channel);

        boolean success = false;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM ? WHERE username=?;");
            ps.setString(1, c);
            ps.setString(2, username);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                success = true;
            }
        } catch (SQLException e) {
            logger.error(String.format("Failed to check if user %s is a superuser.", username), e.getCause());
            success = false;
        }

        disconnect();
        return success;
    }

    public void setFlags(Network network, String channel, String username, String flags) {
        String c = getAndCleanChannel(channel);

        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("SELECT flags FROM ? WHERE username=?;");
            ps.setString(1, c);
            ps.setString(2, username);
            ResultSet result = ps.executeQuery();


        } catch(SQLException e) {
            logger.error(String.format("Failed to rerieve flags for '%s' in %s on network %s", username, channel, network.getServerAlias()));
        }
        disconnect();

    }

    public void getFlags(Network network, String channel, String username) {
        String c = getAndCleanChannel(channel);

        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("SELECT flags FROM ? WHERE username=?;");
            ps.setString(1, c);
            ps.setString(2, username);
            ResultSet result = ps.executeQuery();


        } catch(SQLException e) {
            logger.error(String.format("Failed to rerieve flags for '%s' in %s on network %s", username, channel, network.getServerAlias()));
        }
        disconnect();
    }

}
