package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class KickEvent extends Event {
	
	private String channel;
	private String nickKicking;
	private String hostnameKicking;
	private String nickKicked;
	private String reason;
	
	public KickEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.KICK);
		nickKicking 		= packet.getNick();
		hostnameKicking 	= packet.getHostname();
		channel 			= packet.getParameter(0);
		nickKicked 			= packet.getParameter(1);
		reason 				= packet.getParameter(2);
	}
	
	public String getChannel() {
		return channel;
	}
	
	public String getNickKicking() {
		return nickKicking;
	}
	
	public String getHostnameKicking() {
		return hostnameKicking;
	}
	
	public String getNickKicked() {
		return nickKicked;
	}
	
	public String getReason() {
		return reason;
	}
}
