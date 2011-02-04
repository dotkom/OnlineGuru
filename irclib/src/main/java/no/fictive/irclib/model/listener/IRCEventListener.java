package no.fictive.irclib.model.listener;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.model.network.Network;

/**
 * 
 * @author Espen Jacobsson
 * Required to receive events and text from a {@link Network}
 * @see Session#addListener(IRCEventListener)
 */
public interface IRCEventListener {
	
	/**
	 * When a {@link NetworkEventHandler} parses an event, this method receives it.
	 * @param event @see {@link Event}
	 */
	public void receiveEvent(Event event);
	
	
	/**
	 * When a {@link NetworkEventHandler} parses an event it does not handle, this method
	 * receives the raw-data as text.
	 * @param session A {@link Network} 
	 * @param text The raw-data
	 */
	public void receiveText(Network network, String text);
}
