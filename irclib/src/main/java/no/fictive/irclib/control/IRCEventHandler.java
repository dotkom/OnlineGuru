package no.fictive.irclib.control;

import no.fictive.irclib.event.container.command.CTCPEvent;
import no.fictive.irclib.event.container.command.ErrorEvent;
import no.fictive.irclib.event.container.command.InviteEvent;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.KickEvent;
import no.fictive.irclib.event.container.command.KillEvent;
import no.fictive.irclib.event.container.command.ModeEvent;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.NoticeEvent;
import no.fictive.irclib.event.container.command.PartEvent;
import no.fictive.irclib.event.container.command.PingEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.container.command.QuitEvent;
import no.fictive.irclib.event.container.command.TopicEvent;
import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.network.NetworkEventHandler;
import no.fictive.irclib.model.user.Profile;

/**
 * 
 * @author Espen Jacobsson
 * A class for handling all non-numeric events.
 */
public class IRCEventHandler {
	
	private Network network;
	private Profile profile;
	private NetworkEventHandler networkEventHandler;
	private NumericHandler numericHandler;
	
	/**
	 * Creates a new EventHandler
	 * @param network A {@link Network}
	 * @param profile A {@link Profile}
	 * @param networkEventHandler A {@link NetworkEventHandler}
	 * @param numericHandler A {@link NumericHandler}
	 */
	public IRCEventHandler(Network network, Profile profile, NetworkEventHandler networkEventHandler) {
		this.network = network;
		this.profile = profile;
		this.networkEventHandler = networkEventHandler;
		this.numericHandler = new NumericHandler(network, networkEventHandler);
	}
	
	/**
	 * Handles an event from a raw-line received from an IRC server
	 * @param rawData A raw-line received from an IRC server
	 */
	public void handleEvent(String rawData) {
		IRCEventPacket ircEventPacket = new IRCEventPacket(rawData);
		if(ircEventPacket.isNumeric()) {
			numericHandler.handleNumeric(ircEventPacket);
		} else {
			handleEvent(ircEventPacket);
		}
	}
	
	
	/**
	 * Handles a non-numeric event 
	 * @param packet An {@link IRCEventPacket}
	 */
	private synchronized void handleEvent(IRCEventPacket packet) {
		String command = packet.getCommand();
		
		if(equals(command, "PING")) {
			handlePing(packet);
		}
		else if(equals(command, "PRIVMSG")) {
			handlePrivmsg(packet);
		}
		else if(equals(command, "INVITE")) {
			handleInvite(packet);
		}
		else if(equals(command, "ERROR")) {
			handleError(packet);
		}
		else if(equals(command, "JOIN")) {
			handleJoin(packet);
		}
		else if(equals(command, "KICK")) {
			handleKick(packet);
		}
		else if(equals(command, "KILL")) {
			handleKill(packet);
		}
		else if(equals(command, "MODE")) {
			handleMode(packet);
		}
		else if(equals(command, "NICK")) {
			handleNick(packet);
		}
		else if(equals(command, "NOTICE")) {
			handleNotice(packet);
		}
		else if(equals(command, "PART")) {
			handlePart(packet);
		}
		else if(equals(command, "QUIT")) {
			handleQuit(packet);
		}
		else if(equals(command, "TOPIC")) {
			handleTopic(packet);
		}
		else if(equals(command, "CAP")) {
			handleCAP(packet);
		}
	}

	/**
	 * Handles a ping event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handlePing(IRCEventPacket packet) {
		PingEvent pingEvent = new PingEvent(packet, network);
		network.sendToServer("PONG :" + packet.getParameter(0));
		network.fireEvent(pingEvent);
	}

	
	/**
	 * Handles a privmsg event
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handlePrivmsg(IRCEventPacket packet) {
		
		if(packet.getParameter(1).length() > 0) {
			if(packet.getParameter(1).charAt(0) == 1 && packet.getParameter(1).charAt(packet.getParameter(1).length() - 1) == 1) {
				CTCPEvent ctcpEvent = new CTCPEvent(packet, network);
				network.fireEvent(ctcpEvent);
			}
			else {
				PrivMsgEvent privMsgEvent = new PrivMsgEvent(packet, network);
				network.fireEvent(privMsgEvent);
			}
		}
	}
	
	
	/**
	 * Handles an invite event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleInvite(IRCEventPacket packet) {
		InviteEvent inviteEvent = new InviteEvent(packet, network);
		network.fireEvent(inviteEvent);
	}
	
	
	/**
	 * Handles an error event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleError(IRCEventPacket packet) {
		ErrorEvent errorEvent = new ErrorEvent(packet, network);
		network.fireEvent(errorEvent);
	}
	
	
	/**
	 * Handles a join event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleJoin(IRCEventPacket packet) {
		JoinEvent joinEvent = new JoinEvent(packet, network);
		networkEventHandler.handleJoin(joinEvent);
		network.fireEvent(joinEvent);
	}
	
	
	/**
	 * Handles a kick event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleKick(IRCEventPacket packet) {
		KickEvent kickEvent = new KickEvent(packet, network);
		network.fireEvent(kickEvent);
		networkEventHandler.handleKick(kickEvent);
	}
	
	
	/**
	 * Handles a kill event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleKill(IRCEventPacket packet) {
		KillEvent killEvent = new KillEvent(packet, network);
		network.fireEvent(killEvent);
		networkEventHandler.handleKill(killEvent);
	}
	
	
	/**
	 * Handles a mode event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleMode(IRCEventPacket packet) {
		ModeEvent modeEvent = new ModeEvent(packet, network, profile);
		networkEventHandler.handleMode(modeEvent);
		network.fireEvent(modeEvent);
	}
	
	
	/**
	 * Handles a nick event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleNick(IRCEventPacket packet) {
		NickEvent nickEvent = new NickEvent(packet, network);
		networkEventHandler.handleNickChange(nickEvent);
		network.fireEvent(nickEvent);
	}
	
	
	/**
	 * Handles a notice event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleNotice(IRCEventPacket packet) {
		NoticeEvent noticeEvent = new NoticeEvent(packet, network);
		network.fireEvent(noticeEvent);
	}
	
	
	/**
	 * Handles a part event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handlePart(IRCEventPacket packet) {
		PartEvent partEvent = new PartEvent(packet, network);
		network.fireEvent(partEvent);
		networkEventHandler.handlePart(partEvent);
	}
	
	
	/**
	 * Handles a quit event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleQuit(IRCEventPacket packet) {
		QuitEvent quitEvent = new QuitEvent(packet, network);
		network.fireEvent(quitEvent);
		networkEventHandler.handleQuit(quitEvent);
	}
	
	
	/**
	 * Handles a topic event.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleTopic(IRCEventPacket packet) {
		TopicEvent topicEvent = new TopicEvent(packet, network);
		network.fireEvent(topicEvent);
	}
	
	
	/**
	 * Handles the CAP protocol.
	 * Let the server know we can handle multi-prefix for nicks.
	 * @param packet An {@link IRCEventPacket}
	 */
	private void handleCAP(IRCEventPacket packet) {
		network.fireText(packet.getRawline());
		if(packet.getParameter(1).equals("LS")) {
			network.sendToServer("CAP REQ :multi-prefix");
			network.sendToServer("CAP END");
			network.getNetworkSettings().setNAMESX(true);
		}
	}
	
	/**
	 * Compares a String to another String.
	 * @param s1 String number one.
	 * @param s2 String number two.
	 * @return <code>true</code> if the two Strings match, <code>false</code> if not.
	 */
	private boolean equals(String s1, String s2) {
		return s1.equals(s2);
	}
}
