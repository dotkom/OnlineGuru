package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class NickEvent extends Event {
	
	private String oldNick;
	private String newNick;
	private String ident;
	private String hostname;
	
	public NickEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.NICK);
		oldNick 	= packet.getNick();
		newNick 	= packet.getParameter(0);
		ident 		= packet.getIdent();
		hostname 	= packet.getHostname();
	}
	
	public String getIdent() {
		return ident;
	}
	
	public String getOldNick() {
		return oldNick;
	}
	
	public String getNewNick() {
		return newNick;
	}
	
	public String getHostname() {
		return hostname;
	}
}
