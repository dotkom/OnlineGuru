package no.fictive.irclib.event.container;

import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class Event {
	
	private Network network;
	private String rawData;
	private long time;
	private EventType eventType;
	
	public Event(Network network, String rawData, EventType eventType) {
		this.network = network;
		this.rawData = rawData;
		this.eventType = eventType;
		this.time = System.currentTimeMillis();
	}
	
	public Network getNetwork() {
		return network;
	}
	
	public String getRawData() {
		return rawData;
	}
	
	public long getTime() {
		return time;
	}

	public EventType getEventType() {
		return eventType;
	}
}
