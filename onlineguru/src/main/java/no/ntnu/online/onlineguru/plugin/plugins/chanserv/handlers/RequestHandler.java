package no.ntnu.online.onlineguru.plugin.plugins.chanserv.handlers;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServDB;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.statics.ErrorMessages;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.util.ChanServUtil;
import no.ntnu.online.onlineguru.utils.WandRepository;

public class RequestHandler {

	private WandRepository wandRepository;
	private AuthorizedUserHandler userhandler;
	private AccessRequestHandler accessRequestHandler;
	private CommandHandler commandHandler;
	private ChanServDB db;
	
	public RequestHandler(WandRepository wandRepository, AuthorizedUserHandler authorizedUserHandler, ChanServDB chanServDB) {
		this.wandRepository = wandRepository;
		db = chanServDB;
		
		userhandler = authorizedUserHandler;
		ChanServUtil util = new ChanServUtil(wandRepository, userhandler);
		accessRequestHandler = new AccessRequestHandler(wandRepository, util, db);
		commandHandler = new CommandHandler(wandRepository, util, db);
	}
	
	public void handleRequest(PrivMsgEvent pme) {
		
		String message = pme.getMessage();
		
		if(pme.isPrivateMessage()) {

			//ACCESS HANDLING
			if(message.startsWith("access")) {
				if(isLoggedIn(pme.getSender()))
					accessRequestHandler.handleAccess(pme);
				else
					sendErrorMessage(pme.getNetwork(), pme.getSender(), ErrorMessages.YOU_MUST_BE_LOGGED_IN);
			}
			//COMMAND HANDLING
			else {
				commandHandler.handleCommand(pme);
			}
		}
		
	}
	
	protected void sendErrorMessage(Network network, String nickname, String message) {
		wandRepository.sendMessageToTarget(network, nickname, message);
	}
	
	protected void sendSuccessMessage(Network network, String nickname, String message) {
		wandRepository.sendMessageToTarget(network, nickname, message);
	}
	
	protected boolean isLoggedIn(String nickname) {
		return userhandler.isLoggedIn(nickname);
	}
	
	protected boolean isSuperUser(String nickname) {
		return userhandler.isSuperUser(nickname);
	}
}
