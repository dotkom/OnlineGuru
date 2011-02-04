package no.fictive.irclib.model.channel;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import no.fictive.irclib.event.container.command.ModeEvent;
import no.fictive.irclib.event.container.command.ModeEvent.Mode;
import no.fictive.irclib.model.nick.Nick;

/**
 * 
 * @author Espen Jacobsson
 * This object represents a Channel.
 * It stores the modes of the channel, the channel name, topic,
 * who the topic was set by, when the topic was set, the channel key,
 * as well as a list of users residing the channel.
 */
public class Channel {
	
	private ConcurrentHashMap<String, Nick> nicks = new ConcurrentHashMap<String, Nick>();
	
	private Set<Character> modes = new HashSet<Character>();
	private String channelname;
	private String topic;
	private String topicSetBy;
	private String key;
	private long topicSetTime;
	
	
	/**
	 * Creates a new Channel with a channel name provided.
	 * @param channelname The channels name.
	 */
	public Channel(String channelname) {
		this.channelname = channelname;
	}
	
	
	/**
	 * Gets an hashmap of the users currently in the channel.
	 * @return A ConcurrentHashMap mapping nicknames against {@link Nick}s.
	 */
	public ConcurrentHashMap<String, Nick> getNicks() {
		return nicks;
	}
	
	
	/**
	 * Adds a new {@link Nick} to the channel.
	 * @param nick A {@link Nick}.
	 */
	public void addNick(Nick nick) {
		nicks.put(nick.getNickname(), nick);
	}
	
	
	/**
	 * Gets the modes currently set for this channel.
	 * @return The modes for this channel.
	 */
	public Set<Character> getModes() {
		return modes;
	}
	
	
	/**
	 * Adds a mode to this channel.
	 * This mode will not be added if it already exists.
	 * @param mode A character representing a mode.
	 */
	public void addMode(char mode) {
		modes.add(mode);
	}
	
	/**
	 * Removes a mode from this channel.
	 * @param mode A character representing a mode.
	 */
	public void removeMode(char mode) {
		modes.remove(mode);
	}
	
	
	/**
	 * Gets the channel's name.
	 * @return The channel's name.
	 */
	public String getChannelname() {
		return channelname;
	}
	
	
	/**
	 * Sets the channel's name.
	 * @param channelname The channel's name.
	 */
	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}
	
	
	/**
	 * Gets the topic currently set for this channel.
	 * @return The topic currently set for this channel.
	 */
	public String getTopic() {
		return topic;
	}
	
	
	/**
	 * Sets the topic for this channel.
	 * @param topic The new topic.
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	
	/**
	 * Returns the nickname of the user who set the current topic.
	 * @return The nickname of the user who set the current topic.
	 */
	public String getTopicSetBy() {
		return topicSetBy;
	}
	
	
	/**
	 * Sets the nickname of the user who created the current topic.
	 * @param topicSetBy Nickname of the user who created the topic.
	 */
	public void setTopicSetBy(String topicSetBy) {
		this.topicSetBy = topicSetBy;
	}
	
	
	/**
	 * Gets when the topic was set.
	 * @return A <code>long</code> for when the topic was set.
	 */
	public long getTopicSetTime() {
		return topicSetTime;
	}
	
	
	/**
	 * Sets when the topic was set.
	 * @param topicSetTime A <code>long</code> for when the topic was set.
	 */
	public void setTopicSetTime(long topicSetTime) {
		this.topicSetTime = topicSetTime;
	}
	
	
	/**
	 * Gets the channel key.
	 * @return the channel key.
	 */
	public String getChannelKey() {
		return key;
	}
	
	
	/**
	 * Sets the channel key.
	 * @param key The channel key.
	 */
	public void setChannelKey(String key) {
		this.key = key;
	}

	
	/**
	 * Checks if a user is on this channel.
	 * @param nickname User to check for.
	 * @return <code>true</code> if the user exists, <code>false</code> if not.
	 */
	public boolean isOnChannel(String nickname) {
		return nicks.containsKey(nickname);
	}
	
	
	/**
	 * Checks if a user is the owner of this channel.
	 * @param nickname User to check.
	 * @return <code>true</code> if the user is the owner of this channel, <code>false</code> if not.
	 */
	public boolean isOwner(String nickname) {
		return nicks.get(nickname).isOwner(this);
	}
	
	
	/**
	 * Checks if a user is protected on this channel.
	 * @param nickname User to check.
	 * @return <code>true</code> if the user is protected on this channel, <code>false</code> if not.
	 */
	public boolean isProtected(String nickname) {
		return nicks.get(nickname).isProtected(this);
	}
	
	
	/**
	 * Checks if a user is an operator on this channel.
	 * @param nickname User to check.
	 * @return <code>true</code> if the user is an operator on this channel, <code>false</code> if not.
	 */
	public boolean isOp(String nickname) {
		return nicks.get(nickname).isOp(this);
	}
	
	
	/**
	 * Checks if a user is half-operator on this channel.
	 * @param nickname User to check.
	 * @return <code>true</code> if the user is an half-operator on this channel, <code>false</code> if not.
	 */
	public boolean isHalfOp(String nickname) {
		return nicks.get(nickname).isHalfOp(nickname);
	}
	
	
	/**
	 * Checks if a user is voiced on this channel.
	 * @param nickname User to check.
	 * @return <code>true</code> if the user is the owner of this channel, <code>false</code> if not.
	 */
	public boolean isVoiced(String nickname) {
		return nicks.get(nickname).isVoice(nickname);
	}
	
	
	/**
	 * Gets all the owners of this channel.
	 * @return A <code>Vector</code> containing all {@link Nick}s who are owners on this channel.
	 */
	public Vector<Nick> getOwners() {
		Vector<Nick> owners = new Vector<Nick>();
		for(Nick nick : nicks.values()) {
			if(nick.isOwner(this)) {
				owners.add(nick);
			}
		}
		return owners;
	}
	
	
	/**
	 * Gets all the protected users on this channel.
	 * @return A <code>Vector</code> containing all {@link Nick}s who are protected on this channel.
	 */
	public Vector<Nick> getProtected() {
		Vector<Nick> _protected = new Vector<Nick>();
		for(Nick nick : nicks.values()) {
			if(nick.isProtected(this)) {
				_protected.add(nick);
			}
		}
		return _protected;
	}
	
	
	/**
	 * Gets all the operators on this channel.
	 * @return A <code>Vector</code> containing all {@link Nick}s who are operators on this channel.
	 */
	public Vector<Nick> getOps() {
		Vector<Nick> ops = new Vector<Nick>();
		for(Nick nick : nicks.values()) {
			if(nick.isOp(this)) {
				ops.add(nick);
			}
		}
		return ops;
	}
	
	
	/**
	 * Gets all the half-operators on this channel.
	 * @return A <code>Vector</code> containing all {@link Nick}s who are half-operators on this channel.
	 */
	public Vector<Nick> getHalfOps() {
		Vector<Nick> halfOps = new Vector<Nick>();
		for(Nick nick : nicks.values()) {
			if(nick.isHalfOp(this)) {
				halfOps.add(nick);
			}
		}
		return halfOps;
	}
	
	
	/**
	 * Gets all the voiced users on this channel.
	 * @return A <code>Vector</code> containing all {@link Nick}s who are voiced on this channel.
	 */
	public Vector<Nick> getVoices() {
		Vector<Nick> voices = new Vector<Nick>();
		for(Nick nick : nicks.values()) {
			if(nick.isVoice(this)) {
				voices.add(nick);
			}
		}
		return voices;
	}
	
	
	/**
	 * Joins a user to this channel.
	 * @param nick {@link Nick} joining.
	 */
	public void handleJoin(Nick nick) {
		nicks.putIfAbsent(nick.getNickname(), nick);
	}
	
	
	/**
	 * Kicks a user from this channel.
	 * @param nickname Nickname of the user to be kicked.
	 */
	public void handleKick(String nickname) {
		nicks.remove(nickname);
	}
	
	
	/**
	 * Handles a {@link ModeEvent} for this channel.
	 * @param modeEvent {@link ModeEvent} triggered.
	 */
	public void handleMode(ModeEvent modeEvent) {
		for(Mode mode : modeEvent.getModes()) {
			if(mode.isChannelUserMode()) {
				Nick nick = nicks.get(mode.getParameter());
				//Can be null in case of a *.net *.split
				if(nick != null) {
					switch(mode.getFunction()) {
						case ADDED:
							nick.addMode(this, mode.getMode());
							break;
						case SUBTRACTED:
							nick.removeMode(this, mode.getMode());
							break;
					}
				}
			}
			else {
				modes.add(mode.getMode());
				switch(mode.getMode()) {
					case 'k':
						key = mode.getParameter();
						break;
				}
			}
		}
	}

	
	/**
	 * Removes a user from this channel.
	 * @param nickname Nickname of the user to be removed.
	 */
	public void removeNick(String nickname) {
		nicks.remove(nickname);
	}
	
	
	/**
	 * Handles a nick change for a user on this channel.
     * @param oldNick old nickname for user
     * @param newNick new nickname for user
	 * @param nick Nick ref. for mapping to newNick
	 */
	public void handleNickChange(String oldNick, String newNick, Nick nick) {
		if(nicks.containsKey(oldNick)) {
			nicks.remove(oldNick);
			nicks.put(newNick, nick);
		}
	}
}
