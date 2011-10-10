package no.ntnu.online.onlineguru.plugin.plugins.chanserv.model;

import java.util.HashMap;
import java.util.Iterator;


public class AuthorizedUser {
	
	private String username;
	private boolean superuser = false;
	HashMap<String, Flags> flags = new HashMap<String, Flags>();
	
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
	
	public void updateFlags(HashMap<String, String> flags) {
		
		Iterator<String> en = flags.keySet().iterator();
		
		while(en.hasNext()) {
			String channel = en.next();
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