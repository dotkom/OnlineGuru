package no.fictive.irclib.event.container.command;

import java.util.Collection;
import java.util.Set;
import java.util.Vector;
import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.user.Profile;

public class ModeEvent extends Event {
	
	public enum ModeType {
		USER,
		CHANNEL
	}
	public enum ModeFunction {
		ADDED,
		SUBTRACTED
	}
	public class Mode {
		private ModeFunction function;
		private Character mode;
		private String parameter;
		private boolean isChannelUserMode = false;

		public Mode(ModeFunction function, Character mode) {
			this.function = function;
			this.mode = mode;
		}
		
		public Mode(ModeFunction function, Character mode, String argument) {
			this.function = function;
			this.mode = mode;
			this.parameter = argument;
		}
		
		public ModeFunction getFunction() {
			return function;
		}
		
		public Character getMode() {
			return mode;
		}
		
		public String getParameter() {
			return parameter;
		}
		
		public void setIsChannelUserMode(boolean isChannelUserMode) {
			this.isChannelUserMode = isChannelUserMode;
		}
		
		public boolean isChannelUserMode() {
			return isChannelUserMode;
		}
	}
	
	private String nick;
	private String ident;
	private String hostname;
	private String channel;
	private ModeType type;
	private Vector<Mode> modes = new Vector<Mode>();

	public ModeEvent(IRCEventPacket packet, Network network, Profile profile) {
		super(network, packet.getRawline(), EventType.MODE);
		nick = packet.getNick();
		ident = packet.getIdent();
		hostname = packet.getHostname();
		
		findModeType(packet, profile);
		
		switch(type) {
			case USER:
				handleUserMode(packet, profile);
				break;
			case CHANNEL:
				handleChannelMode(packet);
				break;
		}
	}
	
	private void findModeType(IRCEventPacket packet, Profile profile) {
		if(packet.getParameter(0).equals(profile.getNickname())) {
			type = ModeType.USER;
		}
		else {
			type = ModeType.CHANNEL;
			channel = packet.getParameter(0);
		}
	}
	
	private void handleUserMode(IRCEventPacket packet, Profile profile) {
		
		boolean adding = false;
		for(Character c : packet.getParameter(1).toCharArray()) {
			if(c == '+') { adding = true;  continue; }
			if(c == '-') { adding = false; continue; }
			
			if(adding) {
				profile.addMode(c);
			}
			else {
				profile.removeMode(c);
			}
		}
	}
	
	private void handleChannelMode(IRCEventPacket packet) {
		
		Set<Character> modesWithParameters = getNetwork().getNetworkSettings().getChanModesWithParameters();
		Collection<Character> userModesWithParameters = getNetwork().getNetworkSettings().getPrefixModes();
		
		int modeParameterIndex = 2;
		
		boolean adding = false;
		for(Character c : packet.getParameter(1).toCharArray()) {
			if(c == '+') { adding = true;  continue; }
			if(c == '-') { adding = false; continue; }
			
			ModeFunction function = adding ? ModeFunction.ADDED : ModeFunction.SUBTRACTED;
			
			if(modesWithParameters.contains(c) || userModesWithParameters.contains(c)) {
				if(userModesWithParameters.contains(c)) {
					Mode mode = new Mode(function, c, packet.getParameter(modeParameterIndex));
					mode.setIsChannelUserMode(true);
					modes.add(mode);
				} else {
					modes.add(new Mode(function, c, packet.getParameter(modeParameterIndex)));
				}
				modeParameterIndex++;
			}
			else {
				modes.add(new Mode(function, c));
			}
		}
	}
	
	public String getChannel() {
		return channel;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getIdent() {
		return ident;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public Vector<Mode> getModes() {
		return modes;
	}
	
	public ModeType getModeType() {
		return type;
	}
}
