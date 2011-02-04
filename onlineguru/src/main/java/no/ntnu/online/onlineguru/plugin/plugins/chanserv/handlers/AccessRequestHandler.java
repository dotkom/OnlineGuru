package no.ntnu.online.onlineguru.plugin.plugins.chanserv.handlers;

import java.util.ArrayList;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServDB;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.model.AuthorizedUser;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.statics.ErrorMessages;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.statics.SuccessMessages;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.util.ChanServUtil;
import no.ntnu.online.onlineguru.utils.WandRepository;

public class AccessRequestHandler {

	private WandRepository wandRepository;
	private ChanServUtil util;
	private ChanServDB db;
	
	public AccessRequestHandler(WandRepository wandRepository, ChanServUtil util, ChanServDB db) {
		this.wandRepository = wandRepository;
		this.util = util;
		this.db = db;
	}
	
	protected void handleAccess(PrivMsgEvent pme) {
		String[] parameters = pme.getMessage().split("\\s+");
		String sender = pme.getSender();
		
		if(parameters.length > 1) {
			
			if(util.isSuperUser(sender)) {
				
				//ACCESS ADD
				if(parameters[1].equals("add")) {
					handleAccessAdd(pme);
				}
				//ACCESS SET
				else if(parameters[1].equals("set")) {
					handleAccessSet(pme);
				}
				//ACCESS DEL
				else if(parameters[1].equals("del")) {
					handleAccessDel(pme);
				}
				//ACCESS LIST
				else if(parameters[1].equals("list")) {
					handleAccessList(pme);
				}
				//ACCESS <channel>
				else {
					handleAccessChannel(pme);
				}
			}
			else
				util.sendErrorMessage(pme.getNetwork(), pme.getSender(), ErrorMessages.YOU_MUST_BE_SUPERUSER);
		}
	}
	
	private void handleAccessAdd(PrivMsgEvent pme) {
		String[] parameters = pme.getMessage().split("\\s+");
		Network network = pme.getNetwork();
		String sender = pme.getSender();
		
		//ADD NORMAL USER
		if(parameters.length == 4) {
			handleAccessAddNormalUser(parameters, network, sender);
		}
		
		else if (parameters.length == 5) {
			//ADD SUPERUSER
			if(parameters[2].equals("superuser")) {
				handleAccessAddSuperuser(parameters, network, sender);
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_ACCESS_ADD_SYNTAX);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_ACCESS_ADD_SYNTAX);
		}
	}

	private void handleAccessAddNormalUser(String[] parameters, Network network, String sender) {
		String username = parameters[2];
		String password = parameters[3];
		if(db.addUser(username, password, false)) {
			util.sendSuccessMessage(network, sender, SuccessMessages.ACCESS_ADD);
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.USER_EXISTS);
		}
	}
	
	private void handleAccessAddSuperuser(String[] parameters, Network network,	String sender) {
		String username = parameters[3];
		String password = parameters[4];
		if(db.addUser(username, password, true)) {
			util.sendSuccessMessage(network, sender, SuccessMessages.ACCESS_ADD_SUPERUSER);
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.USER_EXISTS);
		}
	}
	
	private void handleAccessSet(PrivMsgEvent pme) {
		String[] parameters = pme.getMessage().split("\\s+");
		Network network = pme.getNetwork();
		String sender = pme.getSender();
		
		if(parameters.length == 5) {
			
			//SET SUPERUSER
			if(parameters[2].equals("superuser")) {
				handleAccessSetSuperuser(parameters, network, sender);
			}
			else {
				util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_ACCESS_SET_SYNTAX);
			}
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_ACCESS_SET_SYNTAX);
		}
	}

	private void handleAccessSetSuperuser(String[] parameters, Network network, String sender) {
		String username = parameters[3];
		
		if(parameters[4].equals("true")) {
			db.setSuperuser(username, true);
			AuthorizedUser user = util.getAuthorizedUserByBotUsername(username);
			
			if(user != null) {
				user.setSuperuser(true);
			}
			util.sendSuccessMessage(network, sender, SuccessMessages.ACCESS_SET_SUPERUSER);
		}
		else if(parameters[4].equals("false")) {
			db.setSuperuser(username, false);
			AuthorizedUser user = util.getAuthorizedUserByBotUsername(username);
			
			if(user != null) {
				user.setSuperuser(false);
			}
			util.sendSuccessMessage(network, sender, SuccessMessages.ACCESS_SET_SUPERUSER);
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_ACCESS_ADD_SUPERUSER_SYNTAX);
		}
	}
	
	private void handleAccessDel(PrivMsgEvent pme) {
		String[] parameters = pme.getMessage().split("\\s+");
		Network network = pme.getNetwork();
		String sender = pme.getSender();
		
		if(parameters.length == 3) {
			handleAccessDelUser(parameters, network, sender);
		}
		else {
			util.sendErrorMessage(network, sender, ErrorMessages.INCORRECT_ACCESS_DEL_USER_SYNTAX);
		}
	}
	
	private void handleAccessDelUser(String[] parameters, Network network, String sender) {
		String username = parameters[2];
		db.removeUser(username);
		util.removeAuthorizedUserByBotUsername(username);
		util.sendSuccessMessage(network, sender, SuccessMessages.ACCESS_DEL_USER);
	}
	
	private void handleAccessList(PrivMsgEvent pme) {
		for(String[] user : db.getUserList()) {
			wandRepository.sendMessageToTarget(pme.getNetwork(), pme.getSender(), user[0] + " - Superuser: " + user[1]);
		}
	}
	
	private void handleAccessChannel(PrivMsgEvent pme) {
		String[] parameters = pme.getMessage().split("\\s+");
		Network network = pme.getNetwork();
		String sender = pme.getSender();
		
		if(parameters.length == 3) {
			//ACCESS <CHANNEL> LIST
			if(parameters[2].equals("list")) {
				handleAccessChannelList(parameters, network, sender);
			}
			else {
				util.sendErrorMessage(pme.getNetwork(), pme.getSender(), ErrorMessages.INCORRECT_ACCESS_CHANNEL_SYNTAX);
			}
		}
		else if(parameters.length == 4) {
			//ACCESS <CHANNEL> DEL
			if(parameters[2].equals("del")) {
				handleAccessChannelDel(parameters, network, sender);
			}
			else {
				util.sendErrorMessage(pme.getNetwork(), pme.getSender(), ErrorMessages.INCORRECT_ACCESS_CHANNEL_SYNTAX);
			}
		}
		else if(parameters.length == 5) {
			//ACCESS <CHANNEL> FLAGS
			if(parameters[2].equals("flags")) {
				handleAccessChannelFlags(parameters, network, sender);
			}
			else {
				util.sendErrorMessage(pme.getNetwork(), pme.getSender(), ErrorMessages.INCORRECT_ACCESS_CHANNEL_SYNTAX);
			}
		}
		else {
			util.sendErrorMessage(pme.getNetwork(), pme.getSender(), ErrorMessages.INCORRECT_ACCESS_CHANNEL_SYNTAX);
		}
	}
	
	private void handleAccessChannelList(String[] parameters, Network network, String sender) {
		String channel = parameters[1];
		ArrayList<String[]> accessList = db.getAccessList(channel);
		
		if(accessList.size() == 0) {
			wandRepository.sendMessageToTarget(network, sender, "No authorized users for that channel");
			return;
		}
		
		for(String[] array : accessList) {
			String username = array[0];
			String flags = array[1];
			wandRepository.sendMessageToTarget(network, sender, username + ": " + flags);
		}
	}
	
	private void handleAccessChannelDel(String[] parameters, Network network, String sender) {
		String channel = parameters[1];
		String username = parameters[3];
		
		db.removeUserFromChannel(username, channel);
		AuthorizedUser user = util.getAuthorizedUserByBotUsername(username);
		
		if(user != null) {
			util.getAuthorizedUserByBotUsername(username).revokeAllPriviligesOnChannel(channel);
		}
		util.sendSuccessMessage(network, sender, SuccessMessages.ACCESS_CHANNEL_DEL_USER);
	}
	
	private void handleAccessChannelFlags(String[] parameters, Network network, String sender) {
		String channel = parameters[1];
		String username = parameters[3];
		String flags = parameters[4];
		
		db.changeFlags(username, channel, flags);
		AuthorizedUser user = util.getAuthorizedUserByBotUsername(username);
		
		if(user != null) {
			user.updateFlags(db.getFlags(username));
		}
		util.sendSuccessMessage(network, sender, SuccessMessages.ACCESS_CHANNEL_FLAGS);
	}
}
