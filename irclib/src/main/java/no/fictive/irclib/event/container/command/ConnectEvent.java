package no.fictive.irclib.event.container.command;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class ConnectEvent extends Event {
	
	public ConnectEvent(Network network) {
		super(network, "ConnectEvent. Numeric 376", EventType.CONNECT);
	}
}
