package no.fictive.irclib.model.networksettings.channel;

import java.util.HashSet;
import java.util.Set;

public class ChannelModes {

	private ChannelModesType type;
	private Set<Character> modes = new HashSet<Character>();
	
	public ChannelModes(ChannelModesType type) {
		this.type = type;
	}
	
	public ChannelModesType getType() {
		return type;
	}
	
	public Set<Character> getModes() {
		return modes;
	}
	
	public void setModes(Set<Character> modes) {
		this.modes = modes;
	}
	
	@Override
	public String toString() {
		String ret = "";
		
		ret += type.toString() + " - ";
		
		for(Character c : modes) {
			ret += c + ", ";
		}
		
		return ret;
	}
}
