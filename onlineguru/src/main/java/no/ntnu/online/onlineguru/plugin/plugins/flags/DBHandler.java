package no.ntnu.online.onlineguru.plugin.plugins.flags;

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
            logger.error("Need sqlite JDBC.");
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
    protected String getDBNetworkPath(Network network) {
        return flags_database_folder + network.getServerAlias() + ".db";
    }

    protected void initiate(Network network) throws IOException {
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

    // Has test
    protected Connection connect(Network network) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + getDBNetworkPath(network));
        } catch (SQLException e) {
            logger.error("Could not connect to sqlite", e.getCause());
        }
        return null;
    }

    // Has test
    protected void disconnect() {
        try {
            if(conn != null) {
                conn.close();
            }
        } catch(SQLException e) {
            logger.error("Could not disconnect sqlite", e.getCause());
        }
    }

    // Has test
    protected boolean createSuperuserTable(Network network) {
        boolean success = true;
        try {
            conn = connect(network);
            conn.setAutoCommit(true);
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS superuser(username TEXT PRIMARY KEY, password TEXT);");
        } catch(SQLException e) {
            logger.error(String.format("Failed to create superuser table for %s.", network), e.getCause());
            success = false;
        }
        disconnect();
        return success;
    }

    // Has test
    protected boolean createSuperuser(Network network, String username, String password) {
        boolean success = true;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("INSERT INTO superuser (username, password) VALUES (?, ?);");
            ps.setString(1, username);
            ps.setString(2, shaHex(password));
            ps.execute();

        } catch (SQLException e) {
            logger.error(String.format("Failed to create superuser for %s.", network.getServerAlias()), e.getCause());
            success = false;
        }
        disconnect();
        return success;
    }

    // Has test
    protected boolean removeSuperuser(Network network, String username) {
        boolean success = false;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM superuser WHERE username=?;");
            ps.setString(1, username);
            ps.execute();
            success = true;
        } catch (SQLException e) {
            logger.error(String.format("Failed to remove superuser from %s.", network.getServerAlias()), e.getCause());
            success = false;
        }
        disconnect();
        return success;
    }

    // Has test
    protected boolean isSuperuser(Network network, String username) {
        boolean success = false;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM superuser WHERE username=?;");
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
    protected boolean createChannel(JoinEvent e) {
        String channel = getAndCleanChannel(e.getChannel());

        boolean success = true;
        try {
            conn = connect(e.getNetwork());
            conn.setAutoCommit(true);
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + channel + "(username TEXT PRIMARY KEY, flags TEXT);");
            if (!userExistsInChannel(e.getNetwork(), e.getChannel(), e.getNick())) {
                statement.execute("INSERT INTO " + channel + "(username, flags) VALUES ('"+ e.getNick() + "', '');");
            }
        } catch (SQLException sqle) {
            logger.error(String.format("Failed to create channel '%s' in DB.", e.getChannel()), sqle.getCause());
            success = false;
        }
        disconnect();
        return success;
    }

    public boolean userExistsInChannel(Network network, String channel, String username) {
        String c = getAndCleanChannel(channel);

        boolean success = false;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM "+ c +" WHERE username=?;");
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

    public boolean setFlags(Network network, String channel, String username, String flags) {
        String c = getAndCleanChannel(channel);

        boolean success = true;
        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("UPDATE "+ c +" SET flags=? WHERE username=?;");
            ps.setString(1, flags);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch(SQLException e) {
            logger.error(String.format("Failed to update flags for '%s' in %s on network %s", username, channel, network.getServerAlias()));
        }
        disconnect();
        return success;
    }

    public String getFlags(Network network, String channel, String username) {
        String c = getAndCleanChannel(channel);

        try {
            conn = connect(network);
            PreparedStatement ps = conn.prepareStatement("SELECT flags FROM "+ c +" WHERE username=?;");
            ps.setString(1, username);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                return result.getString("flags");
            }
        } catch(SQLException e) {
            logger.error(String.format("Failed to retrieve flags for '%s' in %s on network %s", username, channel, network.getServerAlias()));
        }
        disconnect();
        return "";
    }

}
