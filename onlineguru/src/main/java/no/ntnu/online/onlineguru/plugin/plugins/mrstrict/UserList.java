//package no.ntnu.online.onlineguru.plugin.plugins.mrstrict;
//
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.concurrent.ConcurrentHashMap;
//
//import no.ntnu.online.onlineguru.Settings;
//
//class Userlist {
//	private ConcurrentHashMap<String, User> users;
//	private MrStrict mrStrict;
//	
//	public Userlist(MrStrict mrStrict) {
////		System.out.println("(MrStrict#Userlist) Userlist was created.");
//		this.mrStrict = mrStrict;
//		users = new ConcurrentHashMap<String, User>();
//	}
//	
//	public User addUser(String nick, String hostname, String ident) {
//		User user = users.putIfAbsent(nick, new User(nick, hostname, ident, mrStrict));
//		if(user == null) {
//			user = users.get(nick);
////			System.out.println("(MrStrict#Userlist) A user with nick '" + nick + "' has been added to the userlist.");
//		} else {
////			System.out.println("(MrStrict#Userlist) A user with nick '" + nick + "' was already created.");
//		}
//		return user;
//	}
//	
//	public void removeUser(String nick) {
//		users.remove(nick);
////		System.out.println("(MrStrict#Userlist) A user with nick '" + nick + "' has been removed from the userlist.");
//	}
//	
//	public User getUser(String nick) {
//		return users.get(nick);
//	}
//	
//	public void userChangedNick(String oldNick, String newNick) {
//		User user = users.get(oldNick);
//		user.setNickname(newNick);
//		users.remove(oldNick);
//		users.put(newNick, user);
//		
////		System.out.println("(MrStrict#Userlist) A user changed nick, replaced old nick with new.");
//		
//		Enumeration<Channel> channelEnumerator = user.getChannels().elements();
//		while(channelEnumerator.hasMoreElements()) {
//			channelEnumerator.nextElement().changeNick(oldNick, newNick);
//		}
//	}
//	
//	public void removeUsersNotVisibleToMe() {
//		User me = users.get(wand.myNick);
//		Enumeration<Channel> myChannels = me.getChannels().elements();
//		
//		ArrayList<User> usersVisibleToMe = new ArrayList<User>();
//		
//		while(myChannels.hasMoreElements()) {
//			Enumeration<User> myVisibleUsers = myChannels.nextElement().getUsers().elements();
//			while(myVisibleUsers.hasMoreElements()) {
//				User user = myVisibleUsers.nextElement();
//				if(!usersVisibleToMe.contains(user)) {
//					usersVisibleToMe.add(user);
//				}
//			}
//		}
//		
//		Enumeration<User> userListEnumerator = users.elements();
//		
//		while(userListEnumerator.hasMoreElements()) {
//			User user = userListEnumerator.nextElement();
//			if(!usersVisibleToMe.contains(user)) {
//				users.remove(user.getNickname());
//			}
//		}
////		System.out.println("(MrStrict#Userlist) All users not visible to me has been removed!");
//	}
//	
//	public void removeAllUsers() {
//		users.clear();
//	}
//}