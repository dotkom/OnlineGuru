package no.ntnu.online.onlineguru.plugin.plugins.chanserv.handlers;

import java.util.concurrent.ConcurrentHashMap;

import no.fictive.irclib.event.container.command.KickEvent;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.PartEvent;
import no.fictive.irclib.event.container.command.QuitEvent;
import no.fictive.irclib.model.nick.Nick;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServDB;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.model.AuthorizedUser;

public class AuthorizedUserHandler {
	
	private ConcurrentHashMap<String, AuthorizedUser> authorizedUsers = new ConcurrentHashMap<String, AuthorizedUser>();
	private ChanServDB db;
	
	public AuthorizedUserHandler(ChanServDB chanServDB) {
		this.db = chanServDB;
	}
	
	public void addAuthorizedUser(String nickname, String username) {
		if(!authorizedUsers.containsKey(nickname)) {
			AuthorizedUser authorizedUser = new AuthorizedUser(username);
			
			if(db.isSuperUser(username)) {
				authorizedUser.setSuperuser(true);
			}
			authorizedUser.updateFlags(db.getFlags(username));
			authorizedUsers.put(nickname, authorizedUser);
		}
	}
	
	public void delAuthorizedUserByIRCNickname(String nickname) {
		authorizedUsers.remove(nickname);
	}
	
	public void delAuthorizedUserByBotUsername(String username) {
		AuthorizedUser user = getAuthorizedUserByBotUsername(username);
		authorizedUsers.remove(user);
	}
	
	public AuthorizedUser getAuthorizedUserByIRCNickname(String nickname) {
		return authorizedUsers.get(nickname);
	}
	

	public AuthorizedUser getAuthorizedUserByBotUsername(String username) {
		AuthorizedUser user = null;
		
		for(AuthorizedUser aUser : authorizedUsers.values()) {
			if(aUser.getUsername().equals(username)) {
				user = aUser;
				break;
			}
		}
		return user;
	}
	
	public boolean isLoggedIn(String nickname) {
		return authorizedUsers.containsKey(nickname);
	}
	
	public boolean isSuperUser(String nickname) {
		if(isLoggedIn(nickname)) {
			return authorizedUsers.get(nickname).isSuperuser();
		}
		return false;
	}

	public void handlePartEvent(PartEvent e) {
		String nickname = e.getNick();
		String channel = e.getChannel();
		if(authorizedUsers.containsKey(nickname)) {
			Nick nick = e.getNetwork().getNick(nickname);
			
			if(nick != null) {
				AuthorizedUser user = authorizedUsers.get(nickname);
				user.revokeAllPriviligesOnChannel(channel);
			}
			else {
				authorizedUsers.remove(nickname);
			}
		}
	}
	
	public void handleQuitEvent(QuitEvent e) {
		String nickname = e.getNick();
		
		if(authorizedUsers.containsKey(nickname)) {
			authorizedUsers.remove(nickname);
		}
	}
	
	public void handleKickEvent(KickEvent e) {
		String nickname = e.getNickKicked();
		String channel = e.getChannel();
		
		if(authorizedUsers.containsKey(nickname)) {
			Nick nick = e.getNetwork().getNick(nickname);
			
			if(nick != null) {
				AuthorizedUser user = authorizedUsers.get(nickname);
				user.revokeAllPriviligesOnChannel(channel);
			}
			else {
				authorizedUsers.remove(nickname);
			}
		}
	}
	
	public void handleNickEvent(NickEvent e) {
		String oldNickname = e.getOldNick();
		String newNickname = e.getNewNick();
		
		if(authorizedUsers.containsKey(oldNickname)) {
			AuthorizedUser user = authorizedUsers.get(oldNickname);
			authorizedUsers.remove(oldNickname);
			authorizedUsers.put(newNickname, user);
		}
	}
	
}
