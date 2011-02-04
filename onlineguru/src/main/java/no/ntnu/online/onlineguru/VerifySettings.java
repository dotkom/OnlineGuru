package no.ntnu.online.onlineguru;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import no.fictive.irclib.model.user.Profile;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.settingsreader.Settings;
import no.ntnu.online.onlineguru.utils.settingsreader.SettingsReader;

import org.apache.log4j.Logger;

public class VerifySettings {
    static Logger logger = Logger.getLogger(VerifySettings.class);
    private static final String settings_folder = "settings/";
	private static final String settings_file = settings_folder + "settings.conf";
	
	protected static ArrayList<ConnectionInformation> readSettings() {
		ArrayList<ConnectionInformation> conInfoList = new ArrayList<ConnectionInformation>();
		
		ArrayList<Settings> settingsList = SettingsReader.readSettings(settings_file);
		
		try {
			
			if (settingsList == null) {
				SimpleIO.createFolder(settings_folder);
				SimpleIO.createFile(settings_file);
				createSettings();
	            logger.error("Populate settings.conf before running.");
				System.exit(1);
			}
			
		} catch (IOException e) {
            logger.error("I/O error", e.getCause());
		}
		
				
		ConnectionInformation connectionInformation;
		Profile profile;
		
		for(Settings settings : settingsList) {
			connectionInformation = new ConnectionInformation();
			connectionInformation.setServeralias(settings.getSetting("server_alias"));
			connectionInformation.setHostname(settings.getSetting("hostname"));
			connectionInformation.setPort(settings.getSetting("port"));
			
			String channels = settings.getSetting("channels");
			if(channels != null) {
				for(String channel : channels.split(",")) {
					connectionInformation.addChannel(channel);
				}
			}
			
			profile = new Profile	(	
									settings.getSetting("nickname"),
									settings.getSetting("alt_nickname"),
									settings.getSetting("realname"),
									settings.getSetting("ident"),
									settings.getSetting("email")
								 	);
			profile.setQuitMessage(settings.getSetting("quitmsg"));
			connectionInformation.setProfile(profile);

			if(connectionInformation.isValid() && profile.isValid()) {
				conInfoList.add(connectionInformation);
				logger.info("Settings are in order, connecting..");
			} else {
                logger.error("Settings need to be populated properly.");
			}
		}
			
		return conInfoList;
	}
	
	protected static void createSettings() throws IOException {
		File file = new File(settings_file);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		writer.write("[network]\n");
		writer.write("server_alias=\n");
		writer.write("hostname=\n");
		writer.write("port=\n");
		writer.write("nickname=\n");
		writer.write("alt_nickname=\n");
		writer.write("ident=\n");
		writer.write("realname=\n");
		writer.write("email=\n");
		writer.write("quitmsg=\n");
		writer.write("channels=\n");
		writer.write("# Example:\n");
		writer.write("#channels=#online, #channelwithkey keyhere\n");
		writer.close();
	}
	
}
