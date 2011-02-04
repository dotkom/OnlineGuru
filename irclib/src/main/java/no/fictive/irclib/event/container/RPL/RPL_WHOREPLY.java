package no.fictive.irclib.event.container.RPL;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class RPL_WHOREPLY extends Event {

	private String channel;
	private String ident;
	private String hostname;
	private String server;
	private String nick;
	private String status;
	
	public RPL_WHOREPLY(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.RPL_WHOREPLY);
		channel = 	packet.getParameter(1);
		ident = 	packet.getParameter(2);
		hostname = 	packet.getParameter(3);
		server = 	packet.getParameter(4);
		nick = 		packet.getParameter(5);
		status = 	packet.getParameter(6);
	}
	
	public String getChannel() {
		return channel;
	}

	public String getIdent() {
		return ident;
	}

	public String getHostname() {
		return hostname;
	}

	public String getServer() {
		return server;
	}

	public String getNick() {
		return nick;
	}
	
	public String getStatus() {
		return status;
	}
}
