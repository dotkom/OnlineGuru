package no.fictive.irclib.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.networksettings.NetworkSettings;
import no.fictive.irclib.model.networksettings.channel.ChannelModes;
import no.fictive.irclib.model.networksettings.channel.ChannelModesType;
import org.apache.log4j.Logger;

public class NumericISupportHandler {
    static Logger logger = Logger.getLogger(NumericISupportHandler.class);
	private Network network;
	private NetworkSettings settings;
	private String myNick;
	
	public NumericISupportHandler(Network network) {
		this.network = network;
		settings = network.getNetworkSettings();
		myNick = network.getProfile().getNickname();
	}
	
	protected void handleISupport(IRCEventPacket event) {
		
		Vector<String> protocols = event.getParameters();
		
		for(String protocol : protocols) {
			if(protocol.equals(myNick) || protocol.equals("are supported by this server")) {
				continue;
			}
			if(equalsProtocol(protocol, "PREFIX")) {
				handlePrefix(protocol);
			}
			else if(equalsProtocol(protocol, "CHANTYPES")) {
				handleChanTypes(protocol);
			}
			else if(equalsProtocol(protocol, "CHANMODES")) {
				handleChanModes(protocol);
			}
			else if(equalsProtocol(protocol, "MODES")) {
				handleModes(protocol);
			}
			else if(equalsProtocol(protocol, "MAXCHANNELS")) {
				handleMaxChannels(protocol);
			}
			else if(equalsProtocol(protocol, "CHANLIMIT")) {
				handleChanLimit(protocol);
			}
			else if(equalsProtocol(protocol, "NICKLEN")) {
				handleNickLen(protocol);
			}
			else if(equalsProtocol(protocol, "MAXBANS")) {
				handleMaxBans(protocol);
			}
			else if(equalsProtocol(protocol, "MAXLIST")) {
				handleMaxList(protocol);
			}
			else if(equalsProtocol(protocol, "NETWORK")) {
				handleNetwork(protocol);
			}
			else if(equalsProtocol(protocol, "EXCEPTS")) {
				handleExcepts(protocol);
			}
			else if(equalsProtocol(protocol, "INVEX")) {
				handleInvex(protocol);
			}
			else if(equalsProtocol(protocol, "WALLCHOPS")) {
				handleWallCHops(protocol);
			}
			else if(equalsProtocol(protocol, "WALLVOICES")) {
				handleWallVoices(protocol);
			}
			else if(equalsProtocol(protocol, "STATUSMSG")) {
				handleStatusMSG(protocol);
			}
			else if(equalsProtocol(protocol, "CASEMAPPING")) {
				handleCaseMapping(protocol);
			}
			else if(equalsProtocol(protocol, "ELIST")) {
				handleEList(protocol);
			}
			else if(equalsProtocol(protocol, "TOPICLEN")) {
				handleTopicLen(protocol);
			}
			else if(equalsProtocol(protocol, "KICKLEN")) {
				handleKickLen(protocol);
			}
			else if(equalsProtocol(protocol, "CHANNELLEN")) {
				handleChannelLen(protocol);
			}
			else if(equalsProtocol(protocol, "CHIDLEN")) {
				handleChIDLen(protocol);
			}
			else if(equalsProtocol(protocol, "IDCHAN")) {
				handleIDChan(protocol);
			}
			else if(equalsProtocol(protocol, "STD")) {
				handleSTD(protocol);
			}
			else if(equalsProtocol(protocol, "SILENCE")) {
				handleSilence(protocol);
			}
			else if(equalsProtocol(protocol, "RFC2812")) {
				handleRFC2812(protocol);
			}
			else if(equalsProtocol(protocol, "PENALTY")) {
				handlePenalty(protocol);
			}
			else if(equalsProtocol(protocol, "FNC")) {
				handleFNC(protocol);
			}
			else if(equalsProtocol(protocol, "SAFELIST")) {
				handleSafeList(protocol);
			}
			else if(equalsProtocol(protocol, "AWAYLEN")) {
				handleAwayLen(protocol);
			}
			else if(equalsProtocol(protocol, "NOQUIT")) {
				handleNoQuit(protocol);
			}
			else if(equalsProtocol(protocol, "USERIP")) {
				handleUserIP(protocol);
			}
			else if(equalsProtocol(protocol, "CPRIVMSG")) {
				handleCPrivMSG(protocol);
			}
			else if(equalsProtocol(protocol, "CNOTICE")) {
				handleCNotice(protocol);
			}
			else if(equalsProtocol(protocol, "MAXNICKLEN")) {
				handleMaxNickLen(protocol);
			}
			else if(equalsProtocol(protocol, "MAXTARGETS")) {
				handleMaxTargets(protocol);
			}
			else if(equalsProtocol(protocol, "KNOCK")) {
				handleKnock(protocol);
			}
			else if(equalsProtocol(protocol, "VCHANS")) {
				handleVChans(protocol);
			}
			else if(equalsProtocol(protocol, "WATCH")) {
				handleWatch(protocol);
			}
			else if(equalsProtocol(protocol, "WHOX")) {
				handleWHOX(protocol);
			}
			else if(equalsProtocol(protocol, "CALLERID")) {
				handleCallerID(protocol);
			}
			else if(equalsProtocol(protocol, "ACCEPT")) {
				handleAccept(protocol);
			}
			else if(equalsProtocol(protocol, "LANGUAGE")) {
				handleLanguage(protocol);
			}
			else if(equalsProtocol(protocol, "NAMESX")) {
				handleNamesX(protocol);	
			}
			else if(equalsProtocol(protocol, "MAXCHANNELLEN")) {
				handleMaxChannelLen(protocol);
			}
			else if(equalsProtocol(protocol, "CHARSET")) {
				handleCharSet(protocol);
			}
			else if(equalsProtocol(protocol, "ETRACE")) {
				handleETrace(protocol);
			}
			else if(equalsProtocol(protocol, "DEAF")) {
				handleDeaf(protocol);
			}
			else if(equalsProtocol(protocol, "MONITOR")) {
				handleMonitor(protocol);
			}
			else if(equalsProtocol(protocol, "TARGMAX")) {
				handleTargMax(protocol);
			}
			else if(equalsProtocol(protocol, "EXTBAN")) {
				handleExtBan(protocol);
			}
			else if(equalsProtocol(protocol, "CLIENTVER")) {
				handleClientVer(protocol);
			}
			else {
                logger.error("UNHANDLED PROTOCOL: " + protocol);
				settings.addUnhandledProtocol(protocol);
			}
		}
		
		cleanUpInconsistency();
	}
	
	private void cleanUpInconsistency() {
		
		//Clean up max nick length
		if(settings.getMaxNickLen() == 0 && settings.getNickLen() != 0) {
			settings.setMaxNickLen(settings.getNickLen());
		}
		if(settings.getMaxNickLen() != 0 && settings.getNickLen() == 0) {
			settings.setNickLen(settings.getMaxNickLen());
		}
		
		//Clean up max channel length
		if(settings.getMaxChannelLen() == 0 && settings.getChannelLen() != 0) {
			settings.setMaxChannelLen(settings.getChannelLen());
		}
		if(settings.getMaxChannelLen() != 0 && settings.getChannelLen() == 0) {
			settings.setChannelLen(settings.getMaxChannelLen());
		}
		
		//Clean up channel limit
		if(settings.getChanLimit().isEmpty()) {
			Hashtable<Character, Integer> chanLimit = new Hashtable<Character, Integer>();
			chanLimit.put('#', 0);
			settings.setChanLimit(chanLimit);
		}
	}

	private void handleClientVer(String protocol) {
		String clientVer = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			clientVer = parameters[1];
		}
		
		settings.setClientVer(clientVer);
	}

	private void handleExtBan(String protocol) {
		String extBan = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			extBan = parameters[1];
		}
		
		settings.setExtBan(extBan);
	}

	private void handleTargMax(String protocol) {
		Hashtable<String, String> targmax = new Hashtable<String, String>();
		
		if(containsSplit(protocol)) {
			String[] parametersTargMax = splitOnEquals(protocol);
			String[] parameters = parametersTargMax[1].split(",");
			
			for(String p : parameters) {
				String[] protocolParameters = p.split(":");
				
				if(equalsProtocol(p, "NAMES")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else if(equalsProtocol(p, "LIST")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else if(equalsProtocol(p, "KICK")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else if(equalsProtocol(p, "WHOIS")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else if(equalsProtocol(p, "PRIVMSG")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else if(equalsProtocol(p, "NOTICE")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else if(equalsProtocol(p, "ACCEPT")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else if(equalsProtocol(p, "MONITOR")) {
					if(protocolParameters.length == 2) {
						targmax.put(protocolParameters[0], protocolParameters[1]);
					}
				}
				else {
                    logger.error("UNKNOWN PROTOCOL IN TARGMAX: " + p);
				}
			}
		}
		
		settings.setTargMax(targmax);
	}

	private void handleMonitor(String protocol) {
		int monitor = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			monitor = Integer.parseInt(parameters[1]);
		}
		settings.setMonitor(monitor);
	}

	private void handleDeaf(String protocol) {
		String deaf = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			deaf = parameters[1];
		}
		
		settings.setDeaf(deaf);
	}

	private void handleETrace(String protocol) {
		settings.setETrace(true);
	}

	private void handleCharSet(String protocol) {
		String charset = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			charset = parameters[1];
		}
		
		settings.setCharset(charset);
	}

	private void handleMaxChannelLen(String protocol) {
		int maxChannelLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			maxChannelLen = Integer.parseInt(parameters[1]);
		}
		settings.setMaxChannelLen(maxChannelLen);
	}
	
	private void handleNamesX(String protocol) {
		settings.setNAMESX(true);
		network.sendToServer("PROTOCOTL NAMESX");
	}

	private void handleLanguage(String protocol) {
		String language = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			language = parameters[1];
		}
		
		settings.setLanguage(language);
	}

	private void handleAccept(String protocol) {
		settings.setAccept(true);
	}

	private void handleCallerID(String protocol) {
		String callerID = "g";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			callerID = parameters[1];
		}
		
		settings.setCallerID(callerID);
	}

	private void handleWHOX(String protocol) {
		settings.setWHOX(true);
	}

	private void handleWatch(String protocol) {
		int watch = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			watch = Integer.parseInt(parameters[1]);
		}
		
		settings.setWatch(watch);
	}

	private void handleVChans(String protocol) {
		settings.setVChans(true);
	}

	private void handleKnock(String protocol) {
		settings.setKnock(true);
	}

	private void handleMaxTargets(String protocol) {
		int maxTargets = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			maxTargets = Integer.parseInt(parameters[1]);
		}
		
		settings.setMaxTargets(maxTargets);
	}

	private void handleMaxNickLen(String protocol) {
		int maxNickLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			maxNickLen = Integer.parseInt(parameters[1]);
		}
		
		settings.setMaxNickLen(maxNickLen);
	}

	private void handleCNotice(String protocol) {
		settings.setCNotice(true);
	}

	private void handleCPrivMSG(String protocol) {
		settings.setCPrivMSG(true);
	}

	private void handleUserIP(String protocol) {
		settings.setUserIP(true);
	}

	private void handleNoQuit(String protocol) {
		settings.setNoQuit(true);
	}

	private void handleAwayLen(String protocol) {
		int awayLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			awayLen = Integer.parseInt(parameters[1]);
		}
		
		settings.setAwayLen(awayLen);
	}

	private void handleSafeList(String protocol) {
		settings.setSafeList(true);
	}

	private void handleFNC(String protocol) {
		settings.setFNC(true);
	}

	private void handlePenalty(String protocol) {
		settings.setPenalty(true);
	}

	private void handleRFC2812(String protocol) {
		settings.setRFC2812(true);
		
	}

	private void handleSilence(String protocol) {
		int silence = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			silence = Integer.parseInt(parameters[1]);
		}
		
		settings.setSilence(silence);
	}

	private void handleSTD(String protocol) {
		String STD = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			STD = parameters[1];
		}
		
		settings.setSTD(STD);
	}

	private void handleIDChan(String protocol) {
		Hashtable<Character, Integer> IDChan = new Hashtable<Character, Integer>();
		
		if(containsSplit(protocol)) {
			String parameters = splitOnEquals(protocol)[1];
			String[] partParameters = parameters.split(",");
			
			for(String parameter : partParameters) {
				String[] moreParameterParts = parameter.split(":");
				String prefixes = moreParameterParts[0];
				int limit = Integer.parseInt(moreParameterParts[1]);
				
				for(Character prefix : prefixes.toCharArray()) {
					IDChan.put(prefix, limit);
				}
			}
		}
		
		settings.setIDChan(IDChan);
	}

	private void handleChIDLen(String protocol) {
		int chIDLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			chIDLen = Integer.parseInt(parameters[1]);
		}
		
		settings.setChIDLen(chIDLen);
	}

	private void handleChannelLen(String protocol) {
		int channelLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			channelLen = Integer.parseInt(parameters[1]);
		}
		
		settings.setChannelLen(channelLen);
	}

	private void handleKickLen(String protocol) {
		int kickLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			kickLen = Integer.parseInt(parameters[1]);
		}
		
		settings.setKickLen(kickLen);
	}

	private void handleTopicLen(String protocol) {
		int topicLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			topicLen = Integer.parseInt(parameters[1]);
		}
		
		settings.setTopicLen(topicLen);
	}

	private void handleEList(String protocol) {
		Set<Character> elist = new HashSet<Character>();
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			String extensions = parameters[1];
			
			for(char c : extensions.toCharArray()) {
				elist.add(c);
			}
		}
		
		settings.setEList(elist);
	}

	private void handleCaseMapping(String protocol) {
		String caseMapping = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			caseMapping = parameters[1];
		}
		
		settings.setCaseMapping(caseMapping);
	}

	private void handleStatusMSG(String protocol) {
		Set<Character> prefix = new HashSet<Character>();
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			
			for(char c : parameters[1].toCharArray()) {
				prefix.add(c);
			}
		}
		
		settings.setStatusMSG(prefix);
	}

	private void handleWallVoices(String protocol) {
		settings.setWallVoices(true);
	}

	private void handleWallCHops(String protocol) {
		settings.setWallCHops(true);
	}

	private void handleInvex(String protocol) {
		String invex = "I";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			invex = parameters[1];
		}
		
		settings.setInvex(invex);
	}

	private void handleExcepts(String protocol) {
		String excepts = "e";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			excepts = parameters[1];
		}
		
		settings.setExcepts(excepts);
	}

	private void handleNetwork(String protocol) {
		String network = "";
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			network = parameters[1];
		}
		
		settings.setNetwork(network);
	}

	private void handleMaxList(String protocol) {
		Hashtable<Character, Integer> maxList = new Hashtable<Character, Integer>();
		
		if(containsSplit(protocol)) {
			String parameters = splitOnEquals(protocol)[1];
			String[] partParameters = parameters.split(",");
			
			for(String parameter : partParameters) {
				String[] moreParameterParts = parameter.split(":");
				String prefixes = moreParameterParts[0];
				int limit = Integer.parseInt(moreParameterParts[1]);
				
				for(Character prefix : prefixes.toCharArray()) {
					maxList.put(prefix, limit);
				}
			}
		}
		
		settings.setMaxList(maxList);
	}
	
	private void handleMaxBans(String protocol) {
		int maxbans = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			maxbans = Integer.parseInt(parameters[1]);
		}
		
		settings.setMaxBans(maxbans);
	}

	private void handleNickLen(String protocol) {
		int nickLen = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			nickLen = Integer.parseInt(parameters[1]);
		}
		
		settings.setNickLen(nickLen);
	}

	private void handleChanLimit(String protocol) {
		Hashtable<Character, Integer> chanLimit = new Hashtable<Character, Integer>();
		
		if(containsSplit(protocol)) {
			String parameters = splitOnEquals(protocol)[1];
			String[] partParameters = parameters.split(",");
			
			for(String parameter : partParameters) {
				String[] moreParameterParts = parameter.split(":");
				String prefixes = moreParameterParts[0];
				int limit = Integer.parseInt(moreParameterParts[1]);
				
				for(Character prefix : prefixes.toCharArray()) {
					chanLimit.put(prefix, limit);
				}
			}
		}
		
		settings.setChanLimit(chanLimit);
	}
	
	private void handleMaxChannels(String protocol) {
		int maxchannels = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			maxchannels = Integer.parseInt(parameters[1]);
		}
		
		settings.setMaxChannels(maxchannels);
	}
	
	private void handleModes(String protocol) {
		int modes = 0;
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			modes = Integer.parseInt(parameters[1]);
		}
		
		settings.setModes(modes);
	}

	private void handleChanModes(String protocol) {
		ArrayList<ChannelModes> channelModesList = new ArrayList<ChannelModes>();
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			String[] modes = parameters[1].split(",");
			
			ChannelModesType channelModesType;
			
			for(int index = 0; index < modes.length; index++) {
				switch(index) {
					case 0:
						channelModesType = ChannelModesType.A;	break;
					case 1:
						channelModesType = ChannelModesType.B;	break;
					case 2:
						channelModesType = ChannelModesType.C;	break;
					case 3:
						channelModesType = ChannelModesType.D;	break;
					default:
						channelModesType = ChannelModesType.UNKNOWN; break;
				}
				
				ChannelModes channelModes = new ChannelModes(channelModesType);
				Set<Character> modesSet = new HashSet<Character>();
				
				for(char c : modes[index].toCharArray()) {
					modesSet.add(c);
				}
				channelModes.setModes(modesSet);
				channelModesList.add(channelModes);
			}
		}
		
		settings.setChanModes(channelModesList);
	}

	private void handleChanTypes(String protocol) {
		Set<Character> channelTypes = new HashSet<Character>();
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			char[] types = parameters[1].toCharArray();
			
			for(char type : types) {
				channelTypes.add(type);
			}
		}
		
		settings.setChanTypes(channelTypes);
	}

	private void handlePrefix(String protocol) {
		Hashtable<Character, Character> nickPrefixes = new Hashtable<Character, Character>();
		
		if(containsSplit(protocol)) {
			String[] parameters = splitOnEquals(protocol);
			
			int startOfModes 	= parameters[1].indexOf('(') + 1;
			int endOfModes   	= parameters[1].indexOf(')');
			int startOfPrefixes = endOfModes + 1;
			
			String modes 	= parameters[1].substring(startOfModes, endOfModes);
			String prefixes = parameters[1].substring(startOfPrefixes);
			
			
			for(int index = 0; index < modes.length(); index++) {
				char prefix = prefixes.charAt(index);
				char mode = modes.charAt(index);
				nickPrefixes.put(prefix, mode);
			}
		}

		settings.setPrefixes(nickPrefixes);
	}
	
	private boolean containsSplit(String parameter) {
		return parameter.contains("=");
	}
	private String[] splitOnEquals(String parameter) {
		if(containsSplit(parameter)) {
			return parameter.split("=");
		}
		return new String[] {};
	}
	
	private boolean equalsProtocol(String str, String str2) {
		return str.startsWith(str2);
	}
}
