package no.ntnu.online.onlineguru.plugin.plugins.chanserv.model;

import java.util.Enumeration;
import java.util.Hashtable;


public class AuthorizedUser {
	
	private String username;
	private boolean superuser = false;
	Hashtable<String, Flags> flags = new Hashtable<String, Flags>();
	
	public AuthorizedUser(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setSuperuser(boolean superuser) {
		this.superuser = superuser;
	}
	
	public boolean isSuperuser() {
		return superuser;
	}
	
	public boolean hasAccess(String channel) {
		if(channel == null) return false;
		if(superuser) return true;
		return flags.containsKey(channel);
	}
	
	public Flags getFlags(String channel) {
		if(flags.containsKey(channel)) {
			return flags.get(channel);
		}
		return null;
	}
	
	public void updateFlags(Hashtable<String, String> flags) {
		
		Enumeration<String> en = flags.keys();
		
		while(en.hasMoreElements()) {
			String channel = en.nextElement();
			String strFlags = flags.get(channel);
			updateFlags(channel, strFlags);
		}
	}
	
	public void updateFlags(String channel, String flags) {
		if(!this.flags.containsKey(channel)) {
			this.flags.put(channel, new Flags(flags));
		}
		else {
			this.flags.get(channel).updateFlags(flags);
		}
		
	}
	
	public void revokeAllPriviligesOnChannel(String channel) {
		if(flags.containsKey(channel)) {
			flags.remove(channel);
		}
	}
}