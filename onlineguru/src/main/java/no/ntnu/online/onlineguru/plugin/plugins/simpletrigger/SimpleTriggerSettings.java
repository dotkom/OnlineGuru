package no.ntnu.online.onlineguru.plugin.plugins.simpletrigger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;


public class SimpleTriggerSettings {
    static Logger logger = Logger.getLogger(SimpleTriggerSettings.class);
	private SimpleTriggerPlugin simpleTriggerPlugin;
	private static String SETTINGS_FOLDER = "settings/";
	private static String SETTINGS_FILE = SETTINGS_FOLDER + "simpletrigger.conf";
	
	public SimpleTriggerSettings(SimpleTriggerPlugin simpleTriggerPlugin) {
		this.simpleTriggerPlugin = simpleTriggerPlugin;
	}
	
	public boolean loadConfig() {
		
		try {
			simpleTriggerPlugin.setTriggers(SimpleIO.loadConfig(SETTINGS_FILE));
			return true;
		} catch (FileNotFoundException e1) {
            logger.error("File not found", e1.getCause());
		} catch (IOException e1) {
            logger.error("I/O exception", e1.getCause());
		}
		return false;
	}

	protected void saveConfig(Map<String, String> config) {
		try {
			SimpleIO.saveConfig(SETTINGS_FILE, config);
		} catch (IOException e) {
            logger.error("Failed to save config", e.getCause());
		}
	}
	
	public void initiate() {
		SimpleIO.createFolder(SETTINGS_FOLDER);
		try {
			SimpleIO.createFile(SETTINGS_FILE);
			loadConfig();
		} catch (IOException e) {
            logger.error("I/O error .. Failed to init settings", e.getCause());
		}
	}
}
