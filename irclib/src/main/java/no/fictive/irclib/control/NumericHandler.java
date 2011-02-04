package no.fictive.irclib.control;

import no.fictive.irclib.event.container.RPL.RPL_NAMREPLY;
import no.fictive.irclib.event.container.RPL.RPL_WHOREPLY;
import no.fictive.irclib.event.container.command.ConnectEvent;
import no.fictive.irclib.event.container.command.NumericEvent;
import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.network.NetworkEventHandler;
import no.fictive.irclib.model.network.State;

/**
 * 
 * @author Espen Jacobsson
 * Handles all numeric events.
 *
 */
public class NumericHandler {
	
	private Network network;
	private NetworkEventHandler networkEventHandler;
	private NumericISupportHandler iSupportHandler;
	
	
	/**
	 * Creates a numeric event handler associated with a {@link Network}.
	 * @param network A {@link Network}.
	 * @param networkEventHandler A {@link NetworkEventHandler}.
	 */
	public NumericHandler(Network network, NetworkEventHandler networkEventHandler) {
		this.network = network;
		this.networkEventHandler = networkEventHandler;
		iSupportHandler = new NumericISupportHandler(network);
	}
	
	
	/**
	 * Handles numeric events.
	 * @param packet An {@link IRCEventPacket}.
	 */
	protected void handleNumeric(IRCEventPacket packet) {
		
		int numeric = packet.getNumeric();
		NumericEvent numericEvent = new NumericEvent(packet, network);
		switch(numeric) {
			case 005:
				iSupportHandler.handleISupport(packet);
				network.fireEvent(numericEvent);
				break;
			case 352:
				RPL_WHOREPLY whoreply = new RPL_WHOREPLY(packet, network);
				networkEventHandler.handleWhoReply(whoreply);
				network.fireEvent(whoreply);
				break;
			case 353:
				RPL_NAMREPLY namreply = new RPL_NAMREPLY(packet, network);
				networkEventHandler.handleNamReply(namreply);
				network.fireEvent(namreply);
				break;
			case 376:
				network.setState(State.CONNECTED);
				network.fireEvent(numericEvent);
				network.fireEvent(new ConnectEvent(network));
				break;
			default:
				network.fireEvent(numericEvent);
		}
	}
}
