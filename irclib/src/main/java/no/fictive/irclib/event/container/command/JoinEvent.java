package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class JoinEvent extends Event {
	
	private String nick;
	private String ident;
	private String hostname;
	private String channel;
	
	public JoinEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.JOIN);
		nick 		= packet.getNick();
		ident 		= packet.getIdent();
		hostname 	= packet.getHostname();
		channel 	= packet.getParameter(0);
	}
	
	public String getIdent() {
		return ident;
	}

	public String getNick() {
		return nick;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public String getChannel() {
		return channel;
	}
}
