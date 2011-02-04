package no.fictive.irclib.event.model;

/**
 * 
 * @author Espen Jacobsson & H�vard Svae Slettvold
 *
 */
public enum EventType {
	CONNECT,
	CTCP,
	ERROR,
	INVITE,
	JOIN,
	KICK,
	KILL,
	MODE,
	NICK,
	NOTICE,
	NUMERIC,
	PART,
	PING,
	PRIVMSG,
	QUIT,
	TOPIC,
	
	RPL_NAMREPLY,
	RPL_WHOREPLY,
}
