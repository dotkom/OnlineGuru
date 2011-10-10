package no.fictive.irclib.model.nick;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Espen Jacobsson
 * Handles all nicks known to a {@link no.fictive.irclib.model.network.Network}
 */
public class NickHandler {
	
	ConcurrentHashMap<String, Nick> users = new ConcurrentHashMap<String, Nick>();
	
	
	/**
	 * Returns a {@link Nick} with the username provided.
	 * @param nickname Nickname of a user.
	 * @return A {@link Nick}. <code>null</code> if no user was found.
	 */
	public Nick getNick(String nickname) {
		return users.get(nickname);
	}
	
	
	/**
	 * Adds a {@link Nick} to the nick handler.
	 * @param nick {@link Nick} to add.
	 */
	public void addNick(Nick nick) {
		users.putIfAbsent(nick.getNickname(), nick);
	}
	
	
	/**
	 * Removes a {@link Nick} from the nick handler.
	 * @param nickname Nickname of a user.
	 */
	public void removeNick(String nickname) {
		users.remove(nickname);
	}
	
	
	/**
	 * Checks if a {@link Nick} with the nickname provided exists.
	 * @param nickname Nickname of a user.
	 * @return <code>true</code> if the nickname exists, <code>false</code> if it does not.
	 */
	public boolean exists(String nickname) {
		return users.containsKey(nickname);
	}
	
	
	/**
	 * Replaces the entry of a {@link Nick} with a new nickname.
	 * Should only be used when a user changes his or her nick.
	 * @param newNick New nickname
	 * @param nick A {@link Nick}
	 */
	public void replace(String oldNick, String newNick, Nick nick) {
		users.remove(oldNick);
		users.put(newNick, nick);
	}
	
	public ConcurrentHashMap<String, Nick> getNicks() {
		return users;
	}

}
