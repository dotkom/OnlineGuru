package no.fictive.irclib.event.container;

import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;


public class Numeric extends Event {
	
	private int numeric;

	public Numeric(Network session, String rawData, int numeric) {
		super(session, rawData, EventType.NUMERIC);
		this.numeric = numeric;
	}
	
	public int getNumeric() {
		return numeric;
	}
}
