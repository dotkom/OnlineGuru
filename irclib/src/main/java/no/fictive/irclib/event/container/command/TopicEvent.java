package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class TopicEvent extends Event {
	
	private String topic;
	private String changedByNick;
	private String changedByIdent;
	private String changedByHostname;
	private String channel;
	
	public TopicEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.TOPIC);
		channel 			= packet.getParameter(0);
		topic 				= packet.getParameter(1);
		changedByNick 		= packet.getNick();
		changedByHostname 	= packet.getHostname();
	}
	
	public String getTopic() {
		return topic;
	}
	
	public String getChangedByNick() {
		return changedByNick;
	}
	
	public String getChangedByIdent() {
		return changedByIdent;
	}
	
	public String getChangedByHostname() {
		return changedByHostname;
	}
	
	public String getChannel() {
		return channel;
	}
}
