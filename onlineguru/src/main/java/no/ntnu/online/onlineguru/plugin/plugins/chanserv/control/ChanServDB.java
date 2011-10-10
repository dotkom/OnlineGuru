package no.ntnu.online.onlineguru.plugin.plugins.chanserv.control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;

public class ChanServDB {
    static Logger logger = Logger.getLogger(ChanServDB.class);
	private Connection conn;
	private final String chanserv_database_folder = "database/";
	private final String chanserv_database_file = chanserv_database_folder + "chanserv.db";
	private final String chanserv_settings_folder = "settings/";
	private final String chanserv_settings_file = chanserv_settings_folder + "chanserv.conf";
	private String adminUsername = null;
	private String adminPassword = null;
	
	public ChanServDB() {
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		initiate();
		createAdmin();
	}
	
	public Connection connect() {
		try {
			return DriverManager.getConnection("jdbc:sqlite:" + chanserv_database_file);
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
	
	public boolean addUser(String username, String password, boolean superuser) {
		boolean success = true;
		
		if(!userExists(username)) {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO User (username, password, superuser) VALUES (?, ?, ?);");
				ps.setString(1, username);
				ps.setString(2, password);
				ps.setBoolean(3, superuser);
				ps.execute();
				
			} catch (SQLException e) {
                logger.error("Failed to add user", e.getCause());
				success = false;
			}
			disconnect();
		}
		else {
			success = false;
		}
		return success;
	}
	
	public boolean removeUser(String username) {
		boolean success = true;
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM User WHERE username=?;");
			ps.setString(1, username);
			ps.execute();
			
			ps = conn.prepareStatement("DELETE FROM Flags WHERE username=?;");
			ps.setString(1, username);
			ps.execute();
		} catch (SQLException e) {
            logger.error("Failed to remove user", e.getCause());
			success = false;
		}
		disconnect();
		return success;
	}
	
	public boolean removeUserFromChannel(String username, String channel) {
		boolean success = false;
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM Flags WHERE username=? AND channel_name=?;");
			ps.setString(1, username);
			ps.setString(2, channel);
			ps.execute();
			success = true;
		} catch(SQLException e) {
            logger.error(String.format("Failed to remove user %s from channel %s", username, channel), e.getCause());
		}
		disconnect();
		return success;
	}
	
	public boolean changePassword(String username, String oldPassword, String newPassword) {
		boolean success = false;
		if(userExists(username)) {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement("SELECT password FROM User WHERE username=? LIMIT 1;");
				ps.setString(1, username);
				ResultSet results = ps.executeQuery();
				
				String password = null;
				
				while(results.next()) {
					password = results.getString(1);
				}
				
				if(password.equals(oldPassword)) {
					PreparedStatement ps1 = conn.prepareStatement("UPDATE User SET password=? WHERE username=?;");
					ps1.setString(1, newPassword);
					ps1.setString(2, username);
					ps1.execute();
					success = true;
				}
			} catch (SQLException e) {
				logger.error(String.format("Failed to change password for %s", username), e.getCause());
				success = false;
			}
		}
		return success;
	}
	
	public boolean changePasswordModerator(String username, String password) {
		boolean success = false;
		if(userExists(username)) {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement("UPDATE User SET password=? WHERE username=?;");
				ps.setString(1, password);
				ps.setString(2, username);
				ps.execute();
				success = true;
			} catch(SQLException e) {
                logger.error(String.format("Failed to change password for moderator %s", username), e.getCause() );
			}
		}
		return success;
	}
	
	public HashMap<String, String> getFlags(String username) {
		HashMap<String, String> flags = new HashMap<String, String>();
		
		if(userExists(username)) {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement("SELECT channel_name, flags FROM Flags WHERE username=?;");
				ps.setString(1, username);
				ResultSet result = ps.executeQuery();
				
				while(result.next()) {
					flags.put(result.getString(1), result.getString(2));
				}
			} catch (SQLException e) {
                logger.error(String.format("Failed to receive flags for user %s", username), e.getCause());
			}
		}
		disconnect();
		return flags;
	}
	
	public boolean changeFlags(String username, String channel, String flags) {
		String existingFlags = "";
		
		if(!userExists(username)) {
			return false;
		}
		if(!channelExists(channel)) {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Channel(channel_name) VALUES(?);");
				ps.setString(1, channel);
				ps.execute();
				disconnect();
			} catch (SQLException e) {
                logger.error(String.format("Failed to insert channel %s", channel), e.getCause());
				disconnect();
				return false;
			}
		}
		if(flagsExist(username, channel)) {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement(	"SELECT flags FROM Flags WHERE username = ? AND " +
																"channel_name = (SELECT channel_name FROM Channel WHERE channel_name = ?);");
				ps.setString(1, username);
				ps.setString(2, channel);
				ResultSet result = ps.executeQuery();
				
				while(result.next()) {
					existingFlags = result.getString(1);
				}
				disconnect();
			} catch(SQLException e) {
                logger.error(String.format("Failed to fetch flags for user %s on channel %s", username, channel), e.getCause());
				disconnect();
				return false;
			}
		} else {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Flags(channel_name, username, flags) VALUES(?, ?, ?);");
				ps.setString(1, channel);
				ps.setString(2, username);
				ps.setString(3, "");
				ps.execute();
				disconnect();
			} catch(SQLException e) {
                logger.error(String.format("Failed to insert flags (%s) for user %s on channel %s", flags, username, channel), e.getCause());
				disconnect();
				return false;
			}
		}
		
		boolean plus = false;
		
		for(char c : flags.toCharArray()) {
			if(c == '+') { plus = true; }
			if(c == '-') { plus = false;}
			
			if(plus) {
				if(!existingFlags.contains(String.valueOf(c))) {
					existingFlags += c;
				}
				continue;
			}
			else {
				if(existingFlags.contains(String.valueOf(c))) {
					existingFlags = existingFlags.replaceAll(String.valueOf(c), "");
				}
				continue;
			}
		}
		
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement(	"UPDATE Flags SET flags=? WHERE " +
															"username = ? AND channel_name = ?;");
			ps.setString(1, existingFlags);
			ps.setString(2, username);
			ps.setString(3, channel);
			ps.execute();
		} catch(SQLException e) {
            logger.error(String.format("Failed to update flags (%s) for user %s on channel %s", flags, username, channel), e.getCause());
			disconnect();
			return false;
		}
		disconnect();
		return true;
	}
	
	public boolean setSuperuser(String username, boolean superuser) {
		boolean success = false;
		if(userExists(username)) {
			try {
				conn = connect();
				PreparedStatement ps = conn.prepareStatement("UPDATE User SET superuser=? WHERE username=?;");
				ps.setBoolean(1, superuser);
				ps.setString(2, username);
				ps.execute();
				success = true;
			} catch(SQLException e) {
                logger.error(String.format("Failed to change user %s superuser status to %s", username, superuser) , e.getCause());
				success = false;
			}
		}
		return success;
	}
	
	public ArrayList<String[]> getAccessList(String channel) {
		
		ArrayList<String[]> userList = new ArrayList<String[]>();
		
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("SELECT username, flags FROM Flags WHERE channel_name=?;");
			ps.setString(1, channel);
			ResultSet result = ps.executeQuery();
			
			while(result.next()) {
				userList.add(new String[] { result.getString(1), result.getString(2) });
			}
		} catch(SQLException e) {
            logger.error(String.format("Failed to get accesslist for channel %s", channel), e.getCause());
		}
		disconnect();
		return userList;
	}
	
	public ArrayList<String[]> getUserList() {
		
		ArrayList<String[]> userList = new ArrayList<String[]>();
		
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("SELECT username, superuser FROM User;");
			ResultSet result = ps.executeQuery();
			
			while(result.next()) {
				userList.add(new String[] { result.getString(1), String.valueOf(result.getBoolean(2)) });
			}
		} catch(SQLException e) {
            logger.error("Failed to get user list", e.getCause());
		}
		disconnect();
		return userList;
	}
	
	public boolean login(String username, String password) {
		boolean success = false;
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("SELECT username FROM User WHERE username=? AND password=? LIMIT 1;");
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				success = true;
				break;
			}
		} catch(SQLException e) {
            logger.error(String.format("Failed to handle login for user %s", username), e.getCause());
			success = false;
		}
		disconnect();
		return success;
	}
	
	public boolean userExists(String username) {
		boolean success = false;
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM User WHERE username=?;");
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				success = true;
				break;
			}
		} catch (SQLException e) {
            logger.error(String.format("Failed to check if user %s already exists", username), e.getCause());
			success = false;
		}
		disconnect();
		return success;
	}
	
	public boolean isSuperUser(String username) {
		boolean isSuperUser = false;
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("SELECT superuser FROM User WHERE username=?;");
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				isSuperUser = result.getBoolean(1);
			}
		} catch (SQLException e) {
            logger.error(String.format("Failed to fetch user %s's superstatus", username), e.getCause());
		}
		disconnect();
		return isSuperUser;
	}
	
	private boolean channelExists(String channel) {
		boolean channelExists = false;
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Channel WHERE channel_name=?;");
			ps.setString(1, channel);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				channelExists = true;
			}
		} catch (SQLException e) {
            logger.error(String.format("Failed to check if channel %s already exists", channel), e.getCause());
		}
		disconnect();
		return channelExists;
	}
	
	private boolean flagsExist(String username, String channel) {
		boolean exists = false;
		try {
			conn = connect();
			PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Flags WHERE username = ? AND channel_name = ?;");
			ps.setString(1, username);
			ps.setString(2, channel);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				exists = true;
			}
		} catch(SQLException e) {
            logger.error(String.format("Failed to check if flags exists for user %s on %s", username, channel), e.getCause());
			exists = false;
		}
		disconnect();
		return exists;
	}
	
	private void initiate() {
		
		try {
			SimpleIO.createFolder(chanserv_database_folder);
			SimpleIO.createFile(chanserv_database_file);
			SimpleIO.createFolder(chanserv_settings_folder);
			SimpleIO.createFile(chanserv_settings_file);
			
			Map<String, String> settings = SimpleIO.loadConfig(chanserv_settings_file);
			adminUsername = settings.get("admin_username");
			adminPassword = settings.get("admin_password");
			
			if(adminUsername == null) {
				SimpleIO.appendLineToFile(chanserv_settings_file, "admin_username=");
			}
			if(adminPassword == null) {
				SimpleIO.appendLineToFile(chanserv_settings_file, "admin_password=");
			}
			
			if(adminPassword == null || adminUsername == null) {
                logger.error("chanserv.conf configured incorrectly");
			}
			
			if(adminUsername != null && adminUsername.isEmpty()) {
                logger.warn("Chanserv admin username is not specified");
			}
			if(adminPassword != null && adminPassword.isEmpty()) {
                logger.warn("Chanserv admin password is not specified");
			}
			
			conn = connect();
			conn.setAutoCommit(true);
			Statement statement = conn.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS User(username TEXT PRIMARY KEY, password TEXT, superuser BOOLEAN);");
			statement.execute("CREATE TABLE IF NOT EXISTS Channel(channel_name TEXT PRIMARY KEY);");
			statement.execute("CREATE TABLE IF NOT EXISTS Flags(channel_name TEXT, username TEXT, flags TEXT, FOREIGN KEY (channel_name) REFERENCES Channel(channel_name), FOREIGN KEY (username) REFERENCES User(username));");
			conn.close();
			
		} catch (SQLException e) {
            logger.error("Init plugin failed" , e.getCause());
		} catch (IOException e) {
            logger.error("Init plugin failed" , e.getCause());
		}
	}
	
	private void createAdmin() {
		
		if(adminUsername != null && adminPassword != null) {
			if(!adminUsername.isEmpty() && !adminPassword.isEmpty()) {
			
				if(!userExists(adminUsername)) {
					Statement statement;
					try {
						conn = connect();
						statement = conn.createStatement();
						statement.execute("INSERT INTO User (username, password, superuser)" +
								"VALUES ('" + adminUsername + "', '" + adminPassword + "', '1');");
						conn.close();
					} catch (SQLException e) {
                        logger.error("Failed to create admin user", e.getCause());
					}
				}
			}
		}
	}
}