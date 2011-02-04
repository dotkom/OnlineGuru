package no.ntnu.online.onlineguru.utils.settingsreader;

import java.util.concurrent.ConcurrentHashMap;

import no.ntnu.online.onlineguru.VerifySettings;

import org.apache.log4j.Logger;

public class Settings {

	static Logger logger = Logger.getLogger(Settings.class);
	private ConcurrentHashMap<String, String> settings = new ConcurrentHashMap<String, String>();
	
	public Settings() {
	}
	
	public String getSetting(String field) {
		return settings.get(field);
	}
	
	public void addSetting(String field, String value) {
		String s = settings.get(field);
		if (s == null) {
			settings.putIfAbsent(field, value);
		}
		else {
			logger.error(String.format("Duplicate settings for '%s'", field));
		}
	}
	
}
