//package no.ntnu.online.onlineguru.plugin.plugins.mrstrict;
//
//import java.util.Stack;
//import java.util.concurrent.ConcurrentHashMap;
//
//import no.ntnu.online.onlineguru.utils.Timer;
//
//public class User {
//	private String nickname;
//	private String hostname;
//	private String ident;
//	private MrStrict mrStrict;
//	private boolean whitelisted;
//	
//	//Fields for control over flooding
//	private int floodLimitNumberOfLines = 7;
//	private long floodLimitTime = 3000;
//	
//	private ConcurrentHashMap<String, Stack<Long>> lastLinesQueue;
//	private ConcurrentHashMap<String, Channel> channels;
//	
//	public User(String nickname, String hostname, String ident, MrStrict mrStrict) {
////		System.out.println("(MrStrict#User) User has been created! " + nickname + ", " + hostname + ", " + ident);
//		this.nickname = nickname;
//		this.hostname = hostname;
//		this.ident = ident;
//		this.mrStrict = mrStrict;
//		lastLinesQueue = new ConcurrentHashMap<String, Stack<Long>>();
//		channels = new ConcurrentHashMap<String, Channel>();
//	}
//	
//	public String getNickname() {
//		return nickname;
//	}
//	
//	public void setNickname(String newNick) {
////		System.out.println("(MrStrict#User) " + nickname + " changed nick to " + newNick);  
//		nickname = newNick;
//	}
//	
//	public String getHostname() {
//		return hostname;
//	}
//	
//	public String getIdent() {
//		return ident;
//	}
//	
//	public boolean isWhitelisted() {
//		return whitelisted;
//	}
//	
//	public void addChannel(Channel channel) {
//		channels.putIfAbsent(channel.getChannelName(), channel);
//		channel = channels.get(channel.getChannelName());
//		if(channel != null) {
//			lastLinesQueue.putIfAbsent(channel.getChannelName(), new Stack<Long>());
//			
//		}
////		System.out.println("(MrStrict#User) A new channel, " + channel.getChannelName() + ", was added for user " + nickname);
//	}
//	
//	public void removeChannel(Channel channel) {
//		channels.remove(channel.getChannelName());
//		lastLinesQueue.remove(channel.getChannelName());
////		System.out.println("(MrStrict#User) A channel, " + channel.getChannelName() + ", was removed for user " + nickname);
//	}
//	
//	public boolean onAnyChannels() {
//		return channels.size() != 0;
//	}
//	
//	public boolean onChannel(String channel) {
//		return channels.containsKey(channel);
//	}
//	
//	public ConcurrentHashMap<String, Channel> getChannels() {
//		return channels;
//	}
//	
//	public void setStateNewlyJoined() {
//		floodLimitNumberOfLines = 4;
//		floodLimitTime = 10000;
//		new Timer(this, "newlyJoinedStateOver", 120000, false).start();
//		/* TODO
//		 * bug her, hvis de parter før timern går ut, så får du vel en error?
//		 */
//	}
//	
//	public void newlyJoinedStateOver() {
//		floodLimitNumberOfLines = 7;
//		floodLimitTime = 3000;
//	}
//	
//	public void runFloodControl(String channelName) {
////		System.out.println("(MrStrict#User) Running flood control for user " + nickname);
//		Stack<Long> stack = lastLinesQueue.get(channelName);
//		stack.push(System.currentTimeMillis());
//		
//		if(stack.size() >= floodLimitNumberOfLines) {
//			long average = 0;
//			
//			for(int i = 0; i < (stack.size() - floodLimitNumberOfLines); i++) {
//				average += stack.pop();
//			}
//			
//			while(!stack.empty()) {
//				stack.pop();
//			}
//			
//			if(average/floodLimitNumberOfLines <= floodLimitTime) {
//				String reason = "Flooding! (More than " + floodLimitNumberOfLines + " in " + (floodLimitTime/1000) + " seconds)";
////				mrStrict.kickUser(nickname, channelName, reason);
//			}
//		}
//	}
//	
//	public void setWhiteListed(boolean whitelisted) {
//		this.whitelisted = whitelisted;
//	}
//}
