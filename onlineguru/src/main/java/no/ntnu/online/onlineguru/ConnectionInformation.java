package no.ntnu.online.onlineguru;

import java.util.Vector;
import no.fictive.irclib.model.user.Profile;

public class ConnectionInformation {
	
	private Profile profile;
	private String hostname = "";
	private String port = "";
	private String serveralias = "";
	private Vector<String> channels = new Vector<String>();
	
	public boolean isValid() {
		if (!serveralias.isEmpty() && !hostname.isEmpty() && !port.isEmpty() && profile != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Profile getProfile() {
		return profile;
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getServeralias() {
		return serveralias;
	}
	
	public void setServeralias(String serveralias) {
		this.serveralias = serveralias;
	}
	
	public void addChannel(String channel) {
		channels.add(channel);
	}
	
	public Vector<String> getChannels() {
		return channels;
	}
}
