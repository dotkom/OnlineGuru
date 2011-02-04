package no.fictive.irclib.model.network;

/**
 * States for a connection.
 * @author Espen Jacobsson
 */
public enum State {

	CONNECTING,
	CONNECTED,
	DISCONNECTED,
	NEEDS_PING,
	PING_SENT,
	
}
