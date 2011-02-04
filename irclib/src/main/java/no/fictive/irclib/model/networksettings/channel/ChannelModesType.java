package no.fictive.irclib.model.networksettings.channel;

public enum ChannelModesType {
	/**
	 * Mode that adds or removes a nick or address to a list.
	 * Always has a parameter.
	 */
	A,
	
	/**
	 * Mode that changes a setting and always has a parameter.
	 */
	B,
	
	/**
	 * Mode that changes a setting and only has a parameter when set.
	 */
	C,
	
	/**
	 * Mode that changes a setting and never has a parameter
	 */
	D,
	
	/**
	 * Unknown mode.
	 */
	UNKNOWN,
}
