package no.fictive.irclib.control;

import java.util.Vector;

/**
 * @author Espen Jacobsson
 * Class containing all relevant fields for a rawline from an IRC server.
 */
public class IRCEventPacket {
	
	private String prefix = "";
	private String command = "";
	private Vector<String> parameters = new Vector<String>();
	private String rawline;
	private int index = 0;

	
	/**
	 * An IRCEventPacket will parse a raw line from an IRC server
	 * into sections, namely prefix, command and parameters.
	 * 
	 * @param rawline A raw line from an IRC server.
	 * 
	 */
	public IRCEventPacket(String rawline) {
		this.rawline = rawline;
		parserawline();
	}
	
	
	/**
	 * Parses the raw line into sections.
	 * @see <a href="http://irchelp.org/irchelp/rfc/rfc2812.txt"> RFC2812 </a>
	 * 
	 */
	private void parserawline() {
		
		/*
		 * <message> ::= [':' <prefix> <SPACE> ] <command> <params> <crlf>
		 * <prefix>  ::= <servername> | <nick> [ '!' <user> ] [ '@' <host> ]
		 * <command> ::= <letter> { <letter> } | <number> <number> <number>
		 * <SPACE>   ::= ' ' { ' ' }
		 * <params>  ::= <SPACE> [ ':' <trailing> | <middle> <params> ]
		 */
		
		if(rawline.startsWith(":")) {
			findPrefix();
			findNextNonWhite();
		}
		findCommand();
		findNextNonWhite();
		findParameters();
	}
	
	
	/**
	 * Finds the next non-white character in the raw-line.
	 * 
	 */
	private void findNextNonWhite() {
		for(int i = index; i < rawline.length(); i++) {
			if(rawline.charAt(i) != ' ') {
				index = i;
				return;
			}
		}
	}
	
	
	/**
	 * Finds the prefix in the raw-line.
	 * 
	 */
	private void findPrefix() {
		//	<prefix> ::= <servername> | <nick> [ '!' <user> ] [ '@' <host> ]
		
		prefix = rawline.substring(1, rawline.indexOf(' '));
		index += prefix.length() + 2;
	}
	
	
	/**
	 * Finds the command in the raw-line.
	 * 
	 */
	private void findCommand() {

		//	<command> ::= <letter> { <letter> } | <number> <number> <number>
		
		int nextspace = rawline.substring(index).indexOf(' ');
		command = rawline.substring(index).substring(0, nextspace).trim();
		index += command.length();
	}
	
	
	/**
	 * Finds the parameters in the raw-line.
	 * 
	 */
	private void findParameters() {
		String parameters = rawline.substring(index);
		parseParameters(parameters);
	}
	
	
	/**
	 * Parses the parameters in the raw-line.
	 * @param parameters A String containing the parameters of the raw-line.
	 * 
	 */
	private void parseParameters(String parameters) {
		
		//	<params> ::= <SPACE> [ ':' <trailing> | <middle> <params> ]
		
		String[] parametersArray = parameters.split("\\s");
		
		for(int i = 0; i < parametersArray.length; i++) {
			if(parametersArray[i].charAt(0) == ':') {
				
				/*
				 * Concatinate the rest of the array into one string.
				 * If a parameter starts with ':', the rest of the line is one parameter.
				 */
				String parametersConcatinated = parametersArray[i].substring(1);
				for(int o = i+1; o < parametersArray.length; o++) {
					parametersConcatinated += " " + parametersArray[o];
				}
				this.parameters.add(parametersConcatinated);
				return;
			}
			else {
				this.parameters.add(parametersArray[i]);
			}
		}
	}
	
	
	/**
	 * Finds out if the event is a numeric event or not.
	 * @return <code>true</code> if the event is a numeric event; <code>false</code> if not.
	 * 
	 */
	public boolean isNumeric() {
		try {
			Integer.parseInt(command);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Gets the raw-line.
	 * @return The raw-line of this event.
	 * 
	 */
	public String getRawline() {
		return rawline;
	}
	
	
	/**
	 * Gets the command.
	 * @return The command of this event.
	 * 
	 */
	public String getCommand() {
		return command;
	}
	
	
	/**
	 * Gets the numeric.
	 * @return The numeric of this event.
	 */
	public int getNumeric() {
		return Integer.parseInt(command);
	}
	
	
	/**
	 * Gets a parameter at the given index.
	 * @param index Index of a parameter.
	 * @return The contents of parameters at the given index, an empty String if the index was out of bounds.
	 */
	public String getParameter(int index) {
		if(parameters.size() > index) {
			return parameters.get(index);
		}
		return "";
	}
	
	
	/**
	 * Gets the parameters for this event.
	 * @return The parameters for this event.
	 */
	public Vector<String> getParameters() {
		return parameters;
	}
	
	
	/**
	 * Gets the nick associated with this event.
	 * @return The nick associated with this event.
	 */
	public String getNick() {
		
		String nick = "";
		int nickLimitIndex = prefix.indexOf('!');
		if(nickLimitIndex != -1) {
			nick = prefix.substring(0, nickLimitIndex);
		}
		return nick;
	}
	
	
	/**
	 * Gets the ident associated with this event.
	 * @return The ident associated with this event.
	 */
	public String getIdent() {
		String ident = "";
		int nickEndIndex = prefix.indexOf('!');
		int hostStartIndex = prefix.indexOf('@');
		
		if(nickEndIndex != -1) {
			if(hostStartIndex == -1) {
				ident = prefix.substring(nickEndIndex + 1, prefix.length());
			}
			else {
				ident = prefix.substring(nickEndIndex + 1, hostStartIndex);
			}
		}
		return ident;
	}
	
	
	/**
	 * Gets the hostname associated with this event.
	 * @return The hostname associated with this event.
	 */
	public String getHostname() {
		String hostname = "";
		
		int hostnameStartIndex = prefix.indexOf('@');
		if(hostnameStartIndex != -1) {
			hostname = prefix.substring(hostnameStartIndex + 1);
		}
		return hostname;
	}
	
	
	/**
	 * Gets the server associated with this event.
	 * @return The server associated with this event.
	 */
	public String getServer() {
		return prefix;
	}
}
