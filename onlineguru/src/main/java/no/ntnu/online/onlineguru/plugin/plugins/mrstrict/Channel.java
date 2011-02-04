//package no.ntnu.online.onlineguru.plugin.plugins.mrstrict;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//class Channel {
//	private String channelName;
//	private ConcurrentHashMap<String, User> users;
//	
//	public Channel(String channelName) {
////		System.out.println("(MrStrict#Channel) Channel has been created!");
//		this.channelName = channelName;
//		this.users = new ConcurrentHashMap<String, User>();
//	}
//	
//	public String getChannelName() {
//		return channelName;
//	}
//	
//	public void addUser(User user) {
//		users.putIfAbsent(user.getNickname(), user);
//		user.addChannel(this);
////		System.out.println("(MrStrict#Channel) User '" + user.getNickname() + "' has been added to " + channelName);
//	}
//	
//	public void removeUser(String nick) {
//		users.remove(nick);
//	}
//	
//	public void changeNick(String oldNick, String newNick) {
//		User user = users.get(oldNick);
//		users.remove(oldNick);
//		users.put(newNick, user);
////		System.out.println("(MrStrict#Channel) User '" + oldNick + "' changed nick to '" + newNick + "' and was renewed in the list");
//	}
//	
//	public boolean isUserOnChannel(User user) {
//		return users.contains(user);
//	}
//	
//	public ConcurrentHashMap<String, User> getUsers() {
//		return users;
//	}
//}