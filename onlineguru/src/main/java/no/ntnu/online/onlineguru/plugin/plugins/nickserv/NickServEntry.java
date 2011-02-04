package no.ntnu.online.onlineguru.plugin.plugins.nickserv;

public class NickServEntry {
	
	String username;
	String password;
	
	public NickServEntry(String username, String password) {
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
