package no.ntnu.online.onlineguru.plugin.plugins.chanserv.handlers;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServDB;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.model.AuthorizedUser;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.statics.ErrorMessages;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.statics.SuccessMessages;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.util.ChanServUtil;
import no.ntnu.online.onlineguru.utils.WandRepository;

public class CommandHandler {

	private WandRepository wandRepository;
	private ChanServUtil util;
	private ChanServDB db;
	
	public CommandHandler(WandRepository wandRepository, ChanServUtil util, ChanServDB db) {
		this.wandRepository = wandRepository;
		this.util = util;
		this.db = db;
	}
	
	protected void handleCommand(PrivMsgEvent pme) {
		String[] parameters = pme.getMessage().split("\\s+");
		Network network = pme.getNetwork();
		String sender = pme.getSender();
		String command = parameters[0];
		
		if(command.equals("passwd")) {
			handlePasswordChange(parameters, network, sender);
		}
		else if(command.equals("modpasswd")) {
			handleModPasswordChange(parameters, network, sender);
		}
		else if(command.equals("login")) {
			handleLogin(parameters, network, sender);
		}
		else if(command.equals("logout")) {
			handleLogout(parameters, network, sender);
		}
		else if(command.equals("op")) {
			handleOp(parameters, network, sender);
		}
		else if(command.equals("deop")) {
			handleDeop(parameters, network, sender);
		}
		else if(command.equals("voice")) {
			handleVoice(parameters, network, sender);
		}
		else if(command.equals("devoice")) {
			handleDevoice(parameters, network, sender);
		}
		else if(command.equals("kick")) {
			handleKick(parameters, network, sender);
		}
		else if(command.equals("ban")) {
			handleBan(parameters, network, sender);
		}
		else if(command.equals("unban")) {
			handleUnban(parameters, network, sender);
		}
		else if(command.equals("kickban")) {
			handleKickBan(parameters, network, sender);
		}
		else if(command.equals("topic")) {
			handleTopicChange(pme.getMessage(), network, sender);
		}
		else if(command.equals("mute")) {
			handleMute(parameters, network, sender);
		}
		else if(command.equals("unmute")) {
			handleUnmute(parameters, network, sender);
		}
	}

	private void handlePasswordChange(String[] parameters, Network network,	String sender) {
		if(parameters.length == 3) {
			if(util.isLoggedInByIRCNickname(sender)) {
				String oldPassword = parameters[1];
				String newPassword = parameters[2];
				String username = util.getUsernameByIRCNickname(sender);
				
				if(db.changePassword(username, oldPassword, newPassword)) {
					util.sendSuccessMessage(network, sender, SuccessMessages.PASSWORD_CHANGE);
				}
				else {
					util.sendErrorMessage(network, sender, ErrorMessages.PASSWORD_CHANGE_ERROR);
				}
			} else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_PASSWORD_CHANGE_SYNTAX);
		}
		
	}

	private void handleModPasswordChange(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			if(util.isSuperUser(sender)) {
				String username = parameters[1];
				String newPassword = parameters[2];
				
				if(db.changePasswordModerator(username, newPassword)) {
					util.sendSuccessMessage(network, sender, SuccessMessages.MODPASSWORD_CHANGE);
				}
				else {
					util.sendErrorMessage(network, sender, ErrorMessages.MODPASSWORD_ERROR);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_SUPERUSER);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_MODPASSWORD_CHANGE_SYNTAX);
		}
	}

	private void handleLogin(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			String username = parameters[1];
			String password = parameters[2];
			
			if(db.login(username, password)) {
				if(wandRepository.isUserVisible(network, sender)) {
					if(!util.isLoggedInByBotUsername(username)) {
						if(util.isLoggedInByIRCNickname(sender)) {
							AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
							wandRepository.sendMessageToTarget(network, sender, "You have been logged out of the user '" + user.getUsername() + "'.");
							util.removeAuthorizedUserByIRCNickname(sender);
						}
						util.addAuthorizedUser(sender, username);
						util.sendSuccessMessage(network, sender, SuccessMessages.LOGIN_SUCCESS);
					}
					else {
						util.sendErrorMessage(network, sender, ErrorMessages.USER_ALREADY_LOGGED_IN);
					}
				}
				else {
					util.sendErrorMessage(network, sender, ErrorMessages.YOU_ARE_NOT_VISIBLE);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.LOGIN_FAILURE);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_LOGIN_SYNTAX);
		}
		
	}

	private void handleLogout(String[] parameters, Network network,	String sender) {
		if(parameters.length == 1) {
			if(util.isLoggedInByIRCNickname(sender)) {
				util.removeAuthorizedUserByIRCNickname(sender);
				util.sendSuccessMessage(network, sender, SuccessMessages.LOGOUT_SUCCESS);
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.LOGOUT_FAILURE);
			}
		}
		
	}

	private void handleOp(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			String channel = parameters[1];
			String nickname = parameters[2];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'O', channel, user, sender, network)) {
					wandRepository.op(network, nickname, channel);
					util.sendSuccessMessage(network, sender, SuccessMessages.OP_SUCCESS);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_OP_SYNTAX);
		}
	}

	private void handleDeop(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			String channel = parameters[1];
			String nickname = parameters[2];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'o', channel, user, sender, network)) {
					if(!wandRepository.isMe(network, nickname)) {
						wandRepository.deop(network, nickname, channel);
						util.sendSuccessMessage(network, sender, SuccessMessages.DEOP_SUCCESS);
					}
					else {
						util.sendErrorMessage(network, sender, ErrorMessages.WILL_NOT_DEOP_MYSELF);
					}
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_DEOP_SYNTAX);
		}
	}

	private void handleVoice(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			String channel = parameters[1];
			String nickname = parameters[2];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'V', channel, user, sender, network)) {
					wandRepository.voice(network, nickname, channel);
					util.sendSuccessMessage(network, sender, SuccessMessages.VOICE_SUCCESS);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_VOICE_SYNTAX);
		}
	}

	private void handleDevoice(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			String channel = parameters[1];
			String nickname = parameters[2];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'v', channel, user, sender, network)) {
					wandRepository.devoice(network, nickname, channel);
					util.sendSuccessMessage(network, sender, SuccessMessages.DEVOICE_SUCCESS);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_DEVOICE_SYNTAX);
		}
		
	}

	private void handleKick(String[] parameters, Network network, String sender) {
		if(parameters.length == 3 || parameters.length == 4) {
			String channel = parameters[1];
			String nickname = parameters[2];
			String reason = "";
			
			if(parameters.length == 4) reason = parameters[3];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'K', channel, user, sender, network)) {
					if(!wandRepository.isMe(network, nickname)) {
						if(reason.isEmpty()) {
							wandRepository.kick(network, nickname, channel);
						}
						else {
							wandRepository.kick(network, nickname, channel, reason);
						}
						util.sendSuccessMessage(network, sender, SuccessMessages.KICK_SUCCESS);
					}
					else {
						util.sendErrorMessage(network, sender, ErrorMessages.WILL_NOT_KICK_MYSELF);
					}
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_KICK_SYNTAX);
		}
		
	}

	private void handleBan(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			String channel = parameters[1];
			String mask = parameters[2];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'B', channel, user, sender, network)) {
					if(wandRepository.isMe(network, mask)) {
						wandRepository.ban(network, mask, channel);
						util.sendSuccessMessage(network, sender, SuccessMessages.BAN_SUCCESS);
					}
					else {
						util.sendErrorMessage(network, sender, ErrorMessages.WILL_NOT_BAN_MYSELF);
					}
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_BAN_SYNTAX);
		}
		
	}

	private void handleUnban(String[] parameters, Network network, String sender) {
		if(parameters.length == 3) {
			String channel = parameters[1];
			String mask = parameters[2];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'b', channel, user, sender, network)) {
					wandRepository.unban(network, mask, channel);
					util.sendSuccessMessage(network, sender, SuccessMessages.UNBAN_SUCCESS);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_UNBAN_SYNTAX);
		}
		
	}

	private void handleKickBan(String[] parameters, Network network, String sender) {
		if(parameters.length == 3 || parameters.length == 4) {
			String channel = parameters[1];
			String nickname = parameters[2];
			String reason = "";
			
			if(parameters.length == 4) reason = parameters[3];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'K', channel, user, sender, network)
						&& util.requireConditions(true, true, 'B', channel, user, sender, network)) {
					
					if(!wandRepository.isMe(network, nickname)) {
						if(reason.isEmpty()) {
							wandRepository.kickban(network, nickname, channel);
						}
						else {
							wandRepository.kickban(network, nickname, channel, reason);
						}
						util.sendSuccessMessage(network, sender, SuccessMessages.KICKBAN_SUCCESS);
					}
					else {
						util.sendErrorMessage(network, sender, ErrorMessages.WILL_NOT_KICKBAN_MYSELF);
					}
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_KICKBAN_SYNTAX);
		}
		
	}

	private void handleTopicChange(String message, Network network, String sender) {
		String[] parameters = message.split("\\s+");
		
		if(parameters.length > 2) {
			String channel = parameters[1];
			String topic = message.substring(message.indexOf(channel) + channel.length()).trim();
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'T', channel, user, sender, network)) {
					wandRepository.setTopic(network, channel, topic);
					util.sendSuccessMessage(network, sender, SuccessMessages.TOPIC_SUCCESS);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_TOPIC_SYNTAX);
		}
		
	}

	private void handleMute(String[] parameters, Network network, String sender) {
		if(parameters.length == 2) {
			String channel = parameters[1];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'm', channel, user, sender, network)) {
					wandRepository.mute(network, channel);
					util.sendSuccessMessage(network, sender, SuccessMessages.MUTE_SUCCESS);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_MUTE_SYNTAX);
		}
		
	}

	private void handleUnmute(String[] parameters, Network network, String sender) {
		if(parameters.length == 2) {
			String channel = parameters[1];
			
			if(util.isLoggedInByIRCNickname(sender)) {
				AuthorizedUser user = util.getAuthorizedUserByIRCNickname(sender);
				if(util.requireConditions(true, true, 'm', channel, user, sender, network)) {
					wandRepository.unmute(network, channel);
					util.sendSuccessMessage(network, sender, SuccessMessages.UNMUTE_SUCCESS);
				}
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_UNMUTE_SYNTAX);
		}
	}
	
}
