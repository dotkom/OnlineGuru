package no.fictive.irclib.model.user;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author Espen Jacobsson
 *
 */
public class Profile {
	
	private String nickname = "";
	private String nickname_alternative;
	private String realname;
	private String ident = "";
	private String email;
	private String quitMessage;
	private Set<Character> modes = new HashSet<Character>();
	private Set<String> autojoinChannels = new HashSet<String>();
	
	public Profile(String nickname, String nickname_alternative, String realname, String ident, String email) {
		this.nickname = nickname;
		this.nickname_alternative = nickname_alternative;
		this.realname = realname;
		this.ident = ident;
		this.email = email;
	}
	
	public boolean isValid() {
		if (!nickname.isEmpty() && !ident.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getAlternativeNickname() {
		return nickname_alternative;
	}
	
	public void setAlternativeNickname(String alternative_nickname) {
		this.nickname_alternative = alternative_nickname;
	}

	
	public String getRealname() {
		return realname;
	}

	
	public void setRealname(String realname) {
		this.realname = realname;
	}

	
	public String getIdent() {
		return ident;
	}

	
	public void setIdent(String ident) {
		this.ident = ident;
	}

	
	public String getEmail() {
		return email;
	}

	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getQuitMessage() {
		return quitMessage;
	}
	
	public void setQuitMessage(String quitMessage) {
		this.quitMessage = quitMessage;
	}
	
	public Set<Character> getModes() {
		return modes;
	}
	
	public void addMode(char c) {
		modes.add(c);
	}
	
	public void removeMode(char c) {
		modes.remove(c);
	}
	
	public Set<String> getAutojoinChannels() {
		return autojoinChannels;
	}
	
	public void addChannelToAutojoin(String channelname) {
		autojoinChannels.add(channelname);
	}
	
	public void removeChannelfromAutojoin(String channelname) {
		autojoinChannels.remove(channelname);
	}
}
