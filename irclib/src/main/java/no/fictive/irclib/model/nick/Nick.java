package no.fictive.irclib.model.nick;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import no.fictive.irclib.model.channel.Channel;

/**
 * 
 * @author Espen Jacobsson
 *
 */
public class Nick {
	
	private String nickname;
	private String ident;
	private String hostname;
	private String server;
	private boolean ircop = false;
	
	private ConcurrentHashMap<Channel, HashSet<Character>> modes = new ConcurrentHashMap<Channel, HashSet<Character>>();
	private Set<Character> userModes = new HashSet<Character>();
	
	public Nick(String nickname) {
		this.nickname = nickname;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getIdent() {
		return ident;
	}
	
	public void setIdent(String ident) {
		this.ident = ident;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public boolean isIrcOp() {
		return ircop;
	}
	
	public void setIrcOp(boolean isIrcOp) {
		this.ircop = isIrcOp;
	}
	
	public Set<Channel> getChannels() {
		return modes.keySet();
	}
	
	public void joinChannel(Channel channel) {
		if(!modes.containsKey(channel)) {
			modes.put(channel, new HashSet<Character>());
		}
	}
	
	public void removeChannel(Channel channel) {
		if(modes.containsKey(channel)) {
			modes.remove(channel);
		}
	}
	
	public boolean isOnChannel(String channelname) {
		Enumeration<Channel> en = modes.keys();
		
		while(en.hasMoreElements()) {
			Channel channel = en.nextElement();
			if(channel.getChannelname().equals(channelname)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOnChannel(Channel channel) {
		return modes.contains(channel);
	}
	
	public boolean isOnAnyChannels() {
		return !modes.isEmpty();
	}
	
	public boolean isOwner(String channelname) {
		return containsMode(channelname, 'q');
	}
	
	public boolean isOwner(Channel channel) {
		return modes.get(channel).contains('o');
	}
	
	public boolean isOnCommonChannel(String nickname) {
		for(Channel channel : modes.keySet()) {
			if(channel.isOnChannel(this.nickname) && channel.isOnChannel(nickname)) {
				return true;
			}
		}
		return false;
	}

	public Vector<Channel> getCommonChannels(String nickname) {
		Vector<Channel> commonChannels = new Vector<Channel>();
		
		for(Channel channel : modes.keySet()) {
			if(channel.isOnChannel(this.nickname) && channel.isOnChannel(nickname)) {
				commonChannels.add(channel);
			}
		}
		return commonChannels;
	}
	
	public Vector<Channel> getUncommonChannels(String nickname) {
		Vector<Channel> unCommonChannels = new Vector<Channel>();
		
		for(Channel channel : modes.keySet()) {
			if(!(channel.isOnChannel(this.nickname) && channel.isOnChannel(nickname))) {
				unCommonChannels.add(channel);
			}
		}
		return unCommonChannels;
	}
	
	public boolean isProtected(String channelname) {
		return containsMode(channelname, 'q');
	}
	
	public boolean isProtected(Channel channel) {
		return modes.get(channel).contains('q');
	}
	
	public boolean isOp(String channelname) {
		return containsMode(channelname, 'o');
	}
	
	public boolean isOp(Channel channel) {
		return modes.get(channel).contains('o');
	}
	
	public boolean isHalfOp(String channelname) {
		return containsMode(channelname, 'h');
	}
	
	public boolean isHalfOp(Channel channel) {
		return modes.get(channel).contains('o');
	}
	
	public boolean isVoice(String channelname) {
		return containsMode(channelname, 'v');
	}
	
	public boolean isVoice(Channel channel) {
		return modes.get(channel).contains('o');
	}
	
	private boolean containsMode(String channelname, char c) {
		Enumeration<Channel> en = modes.keys();
		
		while(en.hasMoreElements()) {
			Channel channel = en.nextElement();
			if(channel.getChannelname().equals(channelname)) {
				if(modes.get(channel).contains(c)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addMode(Channel channel, char c) {
		if(modes.containsKey(channel)) {
			modes.get(channel).add(c);
		}
	}
	
	public void removeMode(Channel channel, char c) {
		if(modes.containsKey(channel)) {
			modes.get(channel).remove(c);
		}
	}
	
	public Set<Character> getModes(String channelname) {
		for(Channel channel : modes.keySet()) {
			if(channel.getChannelname().equals(channelname)) {
				return modes.get(channel);
			}
		}
		return new HashSet<Character>();
	}
	
	public Set<Character> getUserModes() {
		return userModes;
	}
	
	public void setUserModes(Set<Character> userModes) {
		this.userModes = userModes;
	}
	
	public String toString() {
		String ret =	"nickname: " + nickname + 
						", ident:" + ident +
						", hostname: " + hostname +
						", server: " + server + 
						", ircop: " + ircop + 
						", ";
		for(Channel channel : modes.keySet()) {
			ret += "On channel : " + channel.getChannelname() + ", with modes ";
			for(Character c : modes.get(channel)) {
				ret += c;
			}
			ret += ", ";
		}
		return ret;
	}
}
