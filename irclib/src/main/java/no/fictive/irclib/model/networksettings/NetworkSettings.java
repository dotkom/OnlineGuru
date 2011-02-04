package no.fictive.irclib.model.networksettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import no.fictive.irclib.model.networksettings.channel.ChannelModes;
import no.fictive.irclib.model.networksettings.channel.ChannelModesType;

public class NetworkSettings {

	private boolean							ACCEPT			= false;
	private int								AWAYLEN			= 0;
	private String							CALLERID		= "";
	private String							CASEMAPPING		= "rfc1459";
	private Hashtable<Character, Integer>	CHANLIMIT 		= new Hashtable<Character, Integer>();
	private ArrayList<ChannelModes> 		CHANMODES 		= new ArrayList<ChannelModes>();
	private int								CHANNELLEN		= 0;
	private Set<Character> 					CHANTYPES 		= new HashSet<Character>();
	private String							CHARSET			= "";
	private int								CHIDLEN			= 0;
	private String							CLIENTVER		= "";			
	private boolean							CNOTICE			= false;
	private boolean							CPRIVMSG		= false;
	private String							DEAF			= "";
	private Set<Character>					ELIST			= new HashSet<Character>();
	private boolean							ETRACE			= false;
	private String							EXCEPTS			= "";
	private String							EXTBAN			= "";
	private boolean							FNC				= false;
	private Hashtable<Character, Integer>	IDCHAN			= new Hashtable<Character, Integer>();
	private String							INVEX			= "";
	private int								KICKLEN			= 0;
	private boolean							KNOCK			= false;
	private String							LANGUAGE		= "";
	private int								MAXBANS			= 0;
	private int								MAXCHANNELLEN	= 0;
	private int								MAXCHANNELS 	= 0;
	private Hashtable<Character, Integer>	MAXLIST 		= new Hashtable<Character, Integer>();
	private int								MAXNICKLEN		= 0;
	private int								MAXTARGETS		= 0;
	private int								MODES 			= 0;
	private int								MONITOR			= 0;
	private boolean							NAMESX			= false;
	private String 							NETWORK 		= "";
	private int								NICKLEN 		= 0;
	private boolean							NOQUIT			= false;
	private boolean							PENALTY			= false;
	private Hashtable<Character, Character>	PREFIX 			= new Hashtable<Character, Character>();
	private boolean							RFC2812			= false;
	private boolean							SAFELIST		= false;
	private int								SILENCE			= 0;
	private Set<Character>					STATUSMSG		= new HashSet<Character>();
	private String							STD				= "";
	private Hashtable<String, String>		TARGMAX			= new Hashtable<String, String>();
	private int								TOPICLEN		= 0;
	private boolean							USERIP			= false;
	private boolean							VCHANS			= false;
	private boolean							WALLCHOPS		= false;
	private boolean							WALLVOICES		= false;
	private int								WATCH			= 0;
	private boolean							WHOX			= false;
	
	private Vector<String>					UNHANDLESPROTOCOLS = new Vector<String>();
	
	public void addUnhandledProtocol(String protocol) {
		UNHANDLESPROTOCOLS.add(protocol);
	}
	
	public Vector<String> getUnhandledProtocols() {
		return UNHANDLESPROTOCOLS;
	}
	
	public String getExtBan() {
		return EXTBAN;
	}

	public void setExtBan(String extBan) {
		EXTBAN = extBan;
	}
	
	public String getClientVer() {
		return CLIENTVER;
	}
	
	public void setClientVer(String clientVer) {
		CLIENTVER = clientVer;
	}
	
	public String getCharset() {
		return CHARSET;
	}

	public void setCharset(String charset) {
		CHARSET = charset;
	}

	public boolean isETrace() {
		return ETRACE;
	}

	public void setETrace(boolean ETrace) {
		ETRACE = ETrace;
	}

	public String getDeaf() {
		return DEAF;
	}

	public void setDeaf(String deaf) {
		DEAF = deaf;
	}

	public int getMonitor() {
		return MONITOR;
	}

	public void setMonitor(int monitor) {
		MONITOR = monitor;
	}

	public Hashtable<String, String> getTargMax() {
		return TARGMAX;
	}

	public void setTargMax(Hashtable<String, String> targMax) {
		TARGMAX = targMax;
	}

	
	/**
	 * Sets the nick prefixes. This ArrayList has to be sorted from most
	 * powerful to least powerful prefix.
	 * @param prefixes List of prefixes.
	 */
	public void setPrefixes(Hashtable<Character, Character> prefixes) {
		this.PREFIX = prefixes;
	}
	
	/**
	 * Returns nick prefixes sorted from most powerful to least powerful.
	 * @return All prefixes supported by this network
	 */
	public Hashtable<Character, Character> getPrefixes() {
		return PREFIX;
	}
	
	public Set<Character> getPrefixRepresentations() {
		return PREFIX.keySet();
	}
	
	public Collection<Character> getPrefixModes() {
		return PREFIX.values();
	}
	
	/**
	 * Sets the channel types available on this network.
	 * @param chanTypes All channel types supported by this network
	 */
	public void setChanTypes(Set<Character> chanTypes) {
		this.CHANTYPES = chanTypes;
	}
	
	
	/**
	 * Returns channel types supported by this network;
	 * @return All channel types supported by this network.
	 */
	public Set<Character> getChanTypes() {
		return CHANTYPES;
	}
	
	/**
	 * Sets the channel modes available on this network.
	 * @param chanModes Modes available.
	 */
	public void setChanModes(ArrayList<ChannelModes> chanModes) {
		this.CHANMODES = chanModes;
	}
	
	/**
	 * Gets the channel modes available on this network.
	 * @return An Array of ChannelModes
	 */
	public ArrayList<ChannelModes> getChanModes() {
		return CHANMODES;
	}
	
	public ChannelModes getChanModesByType(ChannelModesType type) {
		ChannelModes channelModes = null;
		
		for(ChannelModes cm : this.CHANMODES) {
			if(cm.getType() == type) {
				channelModes = cm;
				break;
			}
		}
		return channelModes;
	}
	
	public Set<Character> getChanModesWithParameters() {
		Set<Character> typeA = getChanModesByType(ChannelModesType.A).getModes();
		Set<Character> typeB = getChanModesByType(ChannelModesType.B).getModes();
		Set<Character> typeC = getChanModesByType(ChannelModesType.C).getModes();
		
		Set<Character> typeABC = new HashSet<Character>();
		
		for(char c : typeA) typeABC.add(c);
		for(char c : typeB) typeABC.add(c);
		for(char c : typeC) typeABC.add(c);
		
		return typeABC;
	}

	/**
	 * Sets the maximum number of channel modes with parameter
	 * allowed per MODE command
	 * @param modes Maximum number of channel modes with parameter allowed per MODE command
	 */
	public void setModes(int modes) {
		this.MODES = modes;	
	}
	
	/**
	 * Sets the maximum number of channel modes with parameter
	 * allowed per MODE command 
	 * @return Maximum number of channel modes with parameter allowed per MODE command
	 */
	public int getModes() {
		return MODES;
	}

	public void setChanLimit(Hashtable<Character, Integer> chanLimit) {
		this.CHANLIMIT = chanLimit;
	}
	
	public Hashtable<Character, Integer> getChanLimit() {
		return CHANLIMIT;
	}
	
	public void setNickLen(int nickLen) {
		this.NICKLEN = nickLen;
	}
	
	public int getNickLen() {
		return NICKLEN;
	}
	
	public void setMaxBans(int maxbans) {
		MAXBANS = maxbans;
	}

	public int getMaxBans() {
		return MAXBANS;
	}

	public void setMaxList(Hashtable<Character, Integer> maxList) {
		this.MAXLIST = maxList;
	}
	
	public Hashtable<Character, Integer> getMaxList() {
		return MAXLIST;
	}

	public void setNetwork(String network) {
		this.NETWORK = network;
	}
	
	public String getNetwork() {
		return NETWORK;
	}

	public void setMaxChannels(int maxChannels) {
		MAXCHANNELS = maxChannels;
	}

	public int getMaxChannels() {
		return MAXCHANNELS;
	}

	public void setExcepts(String excepts) {
		EXCEPTS = excepts;
	}

	public String getExcepts() {
		return EXCEPTS;
	}

	public void setInvex(String invex) {
		INVEX = invex;
	}

	public String getInvex() {
		return INVEX;
	}

	public void setWallCHops(boolean wallCHops) {
		WALLCHOPS = wallCHops;
	}

	public boolean isWallCHops() {
		return WALLCHOPS;
	}

	public void setWallVoices(boolean wallVoices) {
		WALLVOICES = wallVoices;
	}

	public boolean isWallVoices() {
		return WALLVOICES;
	}

	public void setStatusMSG(Set<Character> statusMSG) {
		STATUSMSG = statusMSG;
	}

	public Set<Character> getStatusMSG() {
		return STATUSMSG;
	}

	public void setCaseMapping(String caseMapping) {
		CASEMAPPING = caseMapping;
	}

	public String getCaseMapping() {
		return CASEMAPPING;
	}

	public void setEList(Set<Character> eList) {
		ELIST = eList;
	}

	public Set<Character> getEList() {
		return ELIST;
	}

	public void setTopicLen(int topicLen) {
		TOPICLEN = topicLen;
	}

	public int getTopicLen() {
		return TOPICLEN;
	}

	public void setKickLen(int kickLen) {
		KICKLEN = kickLen;
	}

	public int getKickLen() {
		return KICKLEN;
	}
	
	public void setChannelLen(int channelLen) {
		CHANNELLEN = channelLen;
	}
	
	public int getChannelLen() {
		return CHANNELLEN;
	}

	public void setChIDLen(int chIDLen) {
		CHIDLEN = chIDLen;
	}

	public int getCHIDLEN() {
		return CHIDLEN;
	}

	public void setIDChan(Hashtable<Character, Integer> IDChan) {
		IDCHAN = IDChan;
	}

	public Hashtable<Character, Integer> getIDChan() {
		return IDCHAN;
	}

	public void setSTD(String STD) {
		this.STD = STD;
	}

	public String getSTD() {
		return STD;
	}

	public void setSilence(int silence) {
		SILENCE = silence;
	}

	public int getSilence() {
		return SILENCE;
	}

	public void setRFC2812(boolean RFC2812) {
		this.RFC2812 = RFC2812;
	}

	public boolean isRFC2812() {
		return RFC2812;
	}

	public void setPenalty(boolean penalty) {
		PENALTY = penalty;
	}

	public boolean isPenalty() {
		return PENALTY;
	}

	public void setFNC(boolean FNC) {
		this.FNC = FNC;
	}

	public boolean isFNC() {
		return FNC;
	}

	public void setSafeList(boolean safeList) {
		SAFELIST = safeList;
	}

	public boolean isSafeList() {
		return SAFELIST;
	}

	public void setAwayLen(int awayLen) {
		AWAYLEN = awayLen;
	}

	public int getAwayLen() {
		return AWAYLEN;
	}

	public void setNoQuit(boolean noQuit) {
		NOQUIT = noQuit;
	}

	public boolean isNoQuit() {
		return NOQUIT;
	}

	public void setUserIP(boolean userIP) {
		USERIP = userIP;
	}

	public boolean isUserIP() {
		return USERIP;
	}

	public void setCPrivMSG(boolean CPrivMSG) {
		CPRIVMSG = CPrivMSG;
	}

	public boolean isCPrivMSG() {
		return CPRIVMSG;
	}

	public void setCNotice(boolean CNotice) {
		CNOTICE = CNotice;
	}

	public boolean isCNotice() {
		return CNOTICE;
	}

	public void setMaxNickLen(int maxNickLen) {
		MAXNICKLEN = maxNickLen;
	}

	public int getMaxNickLen() {
		return MAXNICKLEN;
	}

	public void setMaxTargets(int maxTargets) {
		MAXTARGETS = maxTargets;
	}

	public int getMaxTargets() {
		return MAXTARGETS;
	}

	public void setKnock(boolean knock) {
		KNOCK = knock;
	}

	public boolean isKnock() {
		return KNOCK;
	}

	public void setVChans(boolean VChans) {
		VCHANS = VChans;
	}

	public boolean isVChans() {
		return VCHANS;
	}

	public void setWatch(int watch) {
		WATCH = watch;
	}

	public int getWatch() {
		return WATCH;
	}

	public void setWHOX(boolean WHOX) {
		this.WHOX = WHOX;
	}

	public boolean isWHOX() {
		return WHOX;
	}

	public void setCallerID(String callerID) {
		CALLERID = callerID;
	}

	public String isCallerID() {
		return CALLERID;
	}
	
	public void setAccept(boolean accept) {
		ACCEPT = accept;
	}

	public boolean isAccept() {
		return ACCEPT;
	}

	public void setLanguage(String language) {
		LANGUAGE = language;
	}

	public String getLanguage() {
		return LANGUAGE;
	}

	public void setNAMESX(boolean NAMESX) {
		this.NAMESX = NAMESX;
	}

	public boolean isNAMESX() {
		return NAMESX;
	}
	
	public void setMaxChannelLen(int maxChannelLen) {
		this.MAXCHANNELLEN = maxChannelLen;		
	}
	
	public int getMaxChannelLen() {
		return MAXCHANNELLEN;
	}
}
