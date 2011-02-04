package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class QuitEvent extends Event {
	
	private String nick;
	private String ident;
	private String hostname;
	private String quitMessage;
	
	public QuitEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.QUIT);
		nick 			= packet.getNick();
		ident 			= packet.getIdent();
		hostname 		= packet.getHostname();
		quitMessage 	= packet.getParameter(0);
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getIdent() {
		return ident;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public String getQuitmessage() {
		return quitMessage;
	}
}
