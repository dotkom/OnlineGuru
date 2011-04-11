package no.ntnu.online.onlineguru.plugin.plugins.chanserv.util;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.handlers.AuthorizedUserHandler;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.model.AuthorizedUser;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.model.Flags;
import no.ntnu.online.onlineguru.utils.Wand;

public class ChanServUtil {
	
	private Wand wand;
	private AuthorizedUserHandler userHandler;
	
	public ChanServUtil(Wand wand, AuthorizedUserHandler userHandler) {
		this.wand = wand;
		this.userHandler = userHandler;
	}
	
	public void addAuthorizedUser(String nickname, String username) {
		userHandler.addAuthorizedUser(nickname, username);
	}
	
	public AuthorizedUser getAuthorizedUserByIRCNickname(String nickname) {
		return userHandler.getAuthorizedUserByIRCNickname(nickname);
	}
	
	public AuthorizedUser getAuthorizedUserByBotUsername(String username) {
		return userHandler.getAuthorizedUserByBotUsername(username);
	}
	
	public String getUsernameByIRCNickname(String nickname) {
		String username = "";
		AuthorizedUser user = getAuthorizedUserByIRCNickname(nickname);
		if(user != null) {
			username = user.getUsername();
		}
		return username;
	}
	
	public void removeAuthorizedUserByIRCNickname(String nickname) {
		userHandler.delAuthorizedUserByIRCNickname(nickname);
	}
	
	public void removeAuthorizedUserByBotUsername(String username) {
		userHandler.delAuthorizedUserByBotUsername(username);
	}
	
	public boolean requireConditions(boolean hasAccess, boolean botHasOp, char flag, String channel, AuthorizedUser user, String sender, Network network) {
		
		if(channel != null && !channel.isEmpty()) {
			if(!wand.amIOnChannel(network, channel)) {
				wand.sendMessageToTarget(network, sender, "I am not on that channel.");
				return false;
			}
		}
		if(hasAccess) {
			if(!user.isSuperuser()) {
				if(channel != null && !channel.isEmpty()) {
					if(!user.hasAccess(channel)) {
						wand.sendMessageToTarget(network, sender, "You do not have access to that channel.");
						return false;
					} else {
						if(flag != 0) {
							if(!user.getFlags(channel).containsFlag(flag)) {
								wand.sendMessageToTarget(network, sender, "You do not have the privileges to do that.");
								return false;
							}
						}
					}
				}
			}
		}
		if(botHasOp) {
			if(channel != null && !channel.isEmpty()) {
				if(!wand.amIOp(network, channel)) {
					wand.sendMessageToTarget(network, sender, "I am not an operator on that channel.");
					return false;
				}
			}
		}
		if(flag != 0) {
			Flags flags = user.getFlags(channel);
			if(flags != null) {
				if(!flags.containsFlag(flag)) {
					wand.sendMessageToTarget(network, sender, "You do not have the necessary privileges on that channel.");
					return false;
				}
			} else {
				if(!user.isSuperuser()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void sendErrorMessage(Network network, String nickname, String message) {
		wand.sendMessageToTarget(network, nickname, message);
	}
	
	public void sendSuccessMessage(Network network, String nickname, String message) {
		wand.sendMessageToTarget(network, nickname, message);
	}
	
	public boolean isLoggedInByIRCNickname(String nickname) {
		return userHandler.isLoggedIn(nickname);
	}
	
	public boolean isLoggedInByBotUsername(String username) {
		return userHandler.getAuthorizedUserByBotUsername(username) != null;
	}
	
	public boolean isSuperUser(String nickname) {
		return userHandler.isSuperUser(nickname);
	}
}
