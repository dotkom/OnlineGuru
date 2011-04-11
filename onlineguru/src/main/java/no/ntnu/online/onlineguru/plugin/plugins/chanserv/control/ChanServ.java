package no.ntnu.online.onlineguru.plugin.plugins.chanserv.control;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.KickEvent;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.PartEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.container.command.QuitEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.handlers.AuthorizedUserHandler;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.handlers.RequestHandler;
import no.ntnu.online.onlineguru.utils.Wand;

public class ChanServ implements Plugin {
	
	private RequestHandler requestHandler;
	private AuthorizedUserHandler userHandler;
	private ChanServDB db;
	
	public ChanServ() {
		db = new ChanServDB();
		userHandler = new AuthorizedUserHandler(db);
	}
	
	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.PRIVMSG);
		eventDistributor.addListener(this, EventType.PART);
		eventDistributor.addListener(this, EventType.QUIT);
		eventDistributor.addListener(this, EventType.KICK);
		eventDistributor.addListener(this, EventType.NICK);
	}

	public void addWand(Wand wand) {
		requestHandler = new RequestHandler(wand, userHandler, db);
	}

	public String getDescription() {
		return "Keeps a record over authorized bot users.";
	}

	public void incomingEvent(Event e) {
		switch(e.getEventType()) {
			case PRIVMSG:
				requestHandler.handleRequest((PrivMsgEvent)e);
				break;
			case PART:
				userHandler.handlePartEvent((PartEvent)e);
				break;
			case QUIT:
				userHandler.handleQuitEvent((QuitEvent)e);
				break;
			case KICK:
				userHandler.handleKickEvent((KickEvent)e);
				break;
			case NICK:
				userHandler.handleNickEvent((NickEvent)e);
				break;
		}
	}
	
	public boolean isNickLoggedIn(String nickname) {
		return userHandler.isLoggedIn(nickname);
	}
}
