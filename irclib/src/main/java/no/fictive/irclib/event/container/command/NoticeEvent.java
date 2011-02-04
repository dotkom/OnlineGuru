package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class NoticeEvent extends Event {
	
	private String notice;
	private String target;
	private String sender;
	private String senderHostname;
	
	public NoticeEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.NOTICE);
		sender 				= packet.getNick();
		senderHostname 		= packet.getHostname();
		target 				= packet.getParameter(0);
		notice 				= packet.getParameter(1);
	}
	
	public String getNotice() {
		return notice;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getSenderHostname() {
		return senderHostname;
	}

	public String getTarget() {
		return target;
	}
}
