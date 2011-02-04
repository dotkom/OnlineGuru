package no.fictive.irclib.event.container.command;

import java.util.Vector;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Numeric;
import no.fictive.irclib.model.network.Network;

public class NumericEvent extends Numeric {

	private String server;
	private int numeric;
	private String target;
	private String message; 
	private Vector<String> parameters;
	
	public NumericEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), packet.getNumeric());
		server			= packet.getServer();
		numeric = packet.getNumeric();
		target = packet.getParameter(0);
		message = packet.getParameter(1);
		parameters = packet.getParameters();
	}
	
	public String getSender() {
		return server;
	}
	
	public int getNumeric() {
		return numeric;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Vector<String> getParamaters() {
		return parameters;
	}
}
