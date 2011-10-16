package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class PrivMsgEvent extends Event {
	
	private String channel;
	private String sender;
	private String senderIdent;
	private String senderHostname;
    private String target;
	private String message;
	
	public PrivMsgEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.PRIVMSG);
		sender 			= packet.getNick();
		senderHostname 	= packet.getHostname();
		senderIdent 	= packet.getIdent();
		message 		= packet.getParameter(1);
        if (packet.getParameter(0).startsWith("#")) {
            channel = packet.getParameter(0);
            target = channel;
        }
        else {
            channel = null;
            target = sender;
        }
	}

	public boolean isChannelMessage() {
		return channel != null;
	}

	public boolean isPrivateMessage() {
		return channel == null;
	}

	public String getTarget() {
		return target;
	}

	public String getSender() {
		return sender;
	}

	public String getHostname() {
		return senderHostname;
	}

	public String getIdent() {
		return senderIdent;
	}

	public String getMessage() {
		return message;
	}

    public String getChannel() {
        return channel;
    }
}
