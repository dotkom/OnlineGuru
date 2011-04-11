package no.ntnu.online.onlineguru.plugin.model;

import no.fictive.irclib.event.container.Event;
import no.ntnu.online.onlineguru.plugin.control.*;
import no.ntnu.online.onlineguru.utils.Wand;

public interface Plugin {
	public String getDescription();
	public void incomingEvent(Event e);
	public void addEventDistributor(EventDistributor eventDistributor);
	public void addWand(Wand wand);
}
