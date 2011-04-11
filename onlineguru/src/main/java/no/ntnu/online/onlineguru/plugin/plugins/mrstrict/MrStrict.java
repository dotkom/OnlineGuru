//package no.ntnu.online.onlineguru.plugin.plugins.mrstrict;
//
//import java.util.Enumeration;
//import java.util.EventListener;
//import java.util.concurrent.ConcurrentHashMap;
//
//import no.fictive.irclib.event.container.Event;
//import no.fictive.irclib.event.model.EventType;
//import no.ntnu.online.onlineguru.Settings;
//import no.ntnu.online.onlineguru.plugin.model.Plugin;
//import no.ntnu.online.onlineguru.utils.IrcWand;
//
//public class MrStrict implements Plugin {
//	
//	private EventListener eventListener;
//	private IrcWand wand;
//	private Userlist userlist;
//	private ConcurrentHashMap<String, Channel> channels;
//	
//	public MrStrict() {
////		System.out.println("(MrStrict) MrStrict created");
//		userlist = new Userlist(this);
//		userlist.addUser(Settings.myNick, null, null);
//		channels = new ConcurrentHashMap<String, Channel>();
//	}
//	
//	public String getDescription() {
//		return "Channel protection plugin.";
//	}
//
//	public void incomingEvent(Event e) {
//		switch(e.getEventType()) {
//			case JOIN:
//				JoinEvent joinEvent = (JoinEvent)e;
//				
//				//If it is ourself, add a new channel if it does not exist 
//				if(joinEvent.getNick().equalsIgnoreCase((Settings.myNick))) {
//					if(!channels.contains(joinEvent.getChannel())) {
//						channels.put(joinEvent.getChannel(), new Channel(joinEvent.getChannel()));
//						Channel channel = channels.get(joinEvent.getChannel());
//						channel.addUser(userlist.getUser(Settings.myNick));
////						System.out.println("(MrStrict) Added a new channel for me: " + channel.getChannelName());
//					}
//				}
//				//If it is someone else, add the user to the channel
//				else {
//					Channel channel = channels.get(joinEvent.getChannel());
//					if(channel != null) {
//						User user = userlist.addUser(joinEvent.getNick(), joinEvent.getHostname(), joinEvent.getIdent());
//						channel.addUser(user);
//						user.setStateNewlyJoined();
////						System.out.println("(MrStrict) Added a new channel, " + channel.getChannelName() + ", for " + user.getNickname());
//					}
//				}
//				break;
//			case PART:
//				PartEvent partEvent = (PartEvent)e;
//				
//				//If it is ourself, remove the channel, and remove all users that are not on any common channels
//				if(partEvent.getNick().equalsIgnoreCase((Settings.myNick))) {
//					if(channels.contains(partEvent.getChannel())) {
//						channels.remove(partEvent.getChannel());
////						System.out.println("(MrStrict) A channel was removed for me: " + partEvent.getChannel());
//					}
//					userlist.removeUsersNotVisibleToMe();
//				}
//				//If it is someone else, remove the user from the channel
//				//Also, if the user is not on any channels anymore, remove him/her from the userlist
//				else {
//					if(userlist.getUser(partEvent.getNick()) != null) {
//						Channel channel = channels.get(partEvent.getChannel());
//						if(channel != null) {
//							channel.removeUser(partEvent.getNick());
////							System.out.println("(MrStrict) Removed channel '" + channel.getChannelName() + "' for user: " + partEvent.getNick());
//						}
//						if(!userlist.getUser(partEvent.getNick()).onAnyChannels()) {
//							userlist.removeUser(partEvent.getNick());
////							System.out.println("(MrStrict) The user '" + partEvent.getNick() + " was not found on any channels, removing it from the userlist completely.");
//						}
//					}
//				}
//				break;
//			case QUIT:
//				QuitEvent quitEvent = (QuitEvent)e;
//				
//				//If it is ourself, empy both the channels list and the userlist.
//				if(quitEvent.getNick().equalsIgnoreCase(Settings.myNick)) {
//					channels.clear();
//					userlist.removeAllUsers();
////					System.out.println("(MrStrict) We have quit amagads, and removed all channels and all users.");
//				}
//				//If it is another user, remove it from any channels it was on, and remove it from the userlist
//				else {
//					if(userlist.getUser(quitEvent.getNick()) != null) {
//						Enumeration<Channel> en = channels.elements();
//						
//						while(en.hasMoreElements()) {
//							en.nextElement().removeUser(quitEvent.getNick());
//						}
//						
//						if(!userlist.getUser(quitEvent.getNick()).onAnyChannels()) {
//							userlist.removeUser(quitEvent.getNick());
//						}
////						System.out.println("(MrStrict) A user has quit! " + quitEvent.getNick() + " was removed from all channels and the userlist.");
//					}
//				}
//				break;
//			case NICK:
//				//Changing of nicks.
//				NickEvent nickEvent = (NickEvent)e;
////				System.out.println("(MrStrict) A user is changing nicks.");
//				userlist.userChangedNick(nickEvent.getOldNick(), nickEvent.getNewNick());
//				break;
//			case PRIVMSG:
//				//Disregard flood checking for private messages, only run diagnostics on channel messages
//				PrivmsgEvent privMsgEvent = (PrivmsgEvent)e;
//				User user = userlist.getUser(privMsgEvent.getSender());
//				
//				
//				if(privMsgEvent.isChannelMessage()) {
//					if(user == null) {
//						user = userlist.addUser(privMsgEvent.getSender(), privMsgEvent.getHostname(), privMsgEvent.getIdent());
//					}
//					if(!user.onChannel(privMsgEvent.getChannel())) {
//						channels.get(privMsgEvent.getChannel()).addUser(user);
//					}
//					if(!userlist.getUser(privMsgEvent.getSender()).isWhitelisted()) {
////						System.out.println("(MrStrict) PrivMsg received, and user was not whitelisted: Running flood protection.");
//						user.runFloodControl(privMsgEvent.getChannel());
//					}
//				}
//				break;
//		}
//	}
//
//	public void addEventListener(EventListener eventListener) {
//		eventListener.addListener(this, EventType.CHANNEL_MESSAGE);
//		eventListener.addListener(this, EventType.PRIVATE_MESSAGE);
//	}
//
//	public void addWand(IrcWand wand) {
//		this.wand = wand;
//	}
//	
//	public String[] getDependencies() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public void loadDependency(Plugin plugin) {
//		// TODO Auto-generated method stub
//	}
//	
//	public void kickUser(String nickname, String channel, String reason) {
//		wand.kick(nickname, channel, reason);
//	}
//	
//	public void banUser(String banmask, String channel) {
//		wand.ban(banmask, channel);
//	}
//
//}
