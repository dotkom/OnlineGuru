package no.ntnu.online.onlineguru.plugin.plugins.auth;

public class AuthEntry {
	
	String username;
	String password;
	
	public AuthEntry(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
