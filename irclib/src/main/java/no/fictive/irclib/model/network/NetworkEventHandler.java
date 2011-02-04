package no.fictive.irclib.model.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import no.fictive.irclib.event.container.RPL.RPL_NAMREPLY;
import no.fictive.irclib.event.container.RPL.RPL_WHOREPLY;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.KickEvent;
import no.fictive.irclib.event.container.command.KillEvent;
import no.fictive.irclib.event.container.command.ModeEvent;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.PartEvent;
import no.fictive.irclib.event.container.command.QuitEvent;
import no.fictive.irclib.event.container.command.TopicEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.nick.Nick;
import no.fictive.irclib.model.nick.NickHandler;
import no.fictive.irclib.model.user.Profile;

/**
 * 
 * @author Espen Jacobsson
 *
 */
public class NetworkEventHandler {
	
	private Network network;
	private NickHandler nickHandler;
	private Profile profile;
	
	public NetworkEventHandler(Network network, NickHandler nickHandler, Profile profile) {
		this.network = network;
		this.nickHandler = nickHandler;
		this.profile = profile;
	}
	
	public void handleJoin(JoinEvent joinEvent) {
		Nick nick = getNick(joinEvent.getNick());
		if(nick == null) {
			nick = new Nick(joinEvent.getNick());
			nick.setHostname(joinEvent.getHostname());
			nick.setIdent(joinEvent.getIdent());
			nickHandler.addNick(nick);
		}
		
		Channel channel = getChannel(joinEvent.getChannel());
		if(channel == null) {
			channel = new Channel(joinEvent.getChannel());
			network.addChannel(joinEvent.getChannel());
		}
		channel.handleJoin(nick);
		nick.joinChannel(channel);
	}
	
	public void handleKick(KickEvent kickEvent) {
		Channel channel = getChannel(kickEvent.getChannel());
		if(channel != null) {
			channel.handleKick(kickEvent.getNickKicked());
			
			Nick nick = getNick(kickEvent.getNickKicked());
			if(nick != null) {
				nick.removeChannel(channel);
				
				//Ourselves
				if(nick.getNickname().equals(profile.getNickname())) {
					removeUsersNotVisible(channel.getNicks().values());
					network.removeChannel(channel.getChannelname());
					removeAllUsersFromChannel(channel);
				}
				//Other
				else if(!nick.isOnAnyChannels()) {
					nickHandler.removeNick(nick.getNickname());
				}
			}
		}
	}

	private void removeUsersNotVisible(Collection<Nick> values) {
		for(Nick nick : values) {
			if(!nick.isOnCommonChannel(profile.getNickname())) {
				nickHandler.removeNick(nick.getNickname());
			}
		}
	}
	
	private void removeAllUsersFromChannel(Channel channel) {
		for(Nick nick : channel.getNicks().values()) {
			nick.removeChannel(channel);
		}
	}

	public void handleKill(KillEvent killEvent) {
		
		//Ourselves
		if(killEvent.getKilled().equals(profile.getNickname())) {
			for(Nick nick : nickHandler.getNicks().values()) {
				nickHandler.removeNick(nick.getNickname());
			}
			for(Channel channel : network.getChannels()) {
				network.removeChannel(channel.getChannelname());
			}
		}
		//Other
		else {
			for(Channel channel : getChannels()) {
				channel.removeNick(killEvent.getKilled());
			}
			nickHandler.removeNick(killEvent.getKilled());
		}
	}
	
	public void handleMode(ModeEvent modeEvent) {
		switch(modeEvent.getModeType()) {
			case CHANNEL:
				Channel channel = getChannel(modeEvent.getChannel());
				if(channel != null) {
					channel.handleMode(modeEvent);
				}
				break;
		}
	}
	
	public void handleNickChange(NickEvent nickEvent) {
		Nick nick = getNick(nickEvent.getOldNick());
		if(nick != null) {
			nickHandler.replace(nickEvent.getOldNick(), nickEvent.getNewNick(), nick);
			nick.setNickname(nickEvent.getNewNick());
			
			for(Channel channel : nick.getChannels()) {
				channel.handleNickChange(nickEvent.getOldNick(), nickEvent.getNewNick(), nick);
			}
			if(nickEvent.getOldNick().equals(profile.getNickname())) {
				profile.setNickname(nickEvent.getNewNick());
			}
		}
	}
	
	public void handlePart(PartEvent partEvent) {
		Nick nick = getNick(partEvent.getNick());
		if(nick != null) {
			Channel channel = getChannel(partEvent.getChannel());
			channel.removeNick(partEvent.getNick());
			nick.removeChannel(channel);
			
			//Ourselves
			if(nick.getNickname().equals(profile.getNickname())) {
				removeUsersNotVisible(channel.getNicks().values());
				network.removeChannel(channel.getChannelname());
				removeAllUsersFromChannel(channel);
			}
			else {
				if(!nick.isOnAnyChannels()) {
					nickHandler.removeNick(nick.getNickname());
				}
			}
		}
	}
	
	public void handleQuit(QuitEvent quitEvent) {
		
		//Ourselves
		if(quitEvent.getNick().equals(profile.getNickname())) {
			for(Nick nick : nickHandler.getNicks().values()) {
				nickHandler.removeNick(nick.getNickname());
			}
			for(Channel channel : network.getChannels()) {
				network.removeChannel(channel.getChannelname());
			}
		}
		//Other
		else {
			for(Channel channel : getChannels()) {
				channel.removeNick(quitEvent.getNick());
			}
			nickHandler.removeNick(quitEvent.getNick());
		}
	}
	
	public void handleTopic(TopicEvent topicEvent) {
		Channel channel = getChannel(topicEvent.getChannel());
		if(channel != null) {
			channel.setTopic(topicEvent.getTopic());
			channel.setTopicSetBy(topicEvent.getChangedByNick());
			channel.setTopicSetTime(topicEvent.getTime());
		}
	}
	
	public void handleNamReply(RPL_NAMREPLY namreply) {
		
		
		Channel channel = getChannel(namreply.getChannel());
		Hashtable<Character, Character> nickPrefixes = network.getNetworkSettings().getPrefixes();
		
		if(channel != null) {
			
			ArrayList<String> nicknames = namreply.getNicks();
			Set<Character> foundPrefixes;
			
			for(String nickname : nicknames) {
				foundPrefixes = new HashSet<Character>();
				
				for(int i = 0; i < nickname.length(); i++) {
					char c = nickname.charAt(i);
					if(!nickPrefixes.containsKey(c)) {
						nickname = nickname.substring(i);
						break;
					}
					foundPrefixes.add(nickPrefixes.get(c));
				}
				
				Nick nick = nickHandler.getNick(nickname);
				
				if(nick == null) {
					nick = new Nick(nickname);
					nickHandler.addNick(nick);
				}
				
				channel.addNick(nick);
				nick.joinChannel(channel);
				
				for(char c : foundPrefixes) {
					nick.addMode(channel, c);
				}
			}
		}
	}
		
	public void handleWhoReply(RPL_WHOREPLY whoreply) {
		Channel channel = getChannel(whoreply.getChannel());
		if(channel != null) {
			Nick nick = nickHandler.getNick(whoreply.getNick());
			
			if(nick == null) {
				nick = new Nick(whoreply.getNick());
			}
			channel.addNick(nick);
			nick.joinChannel(channel);
			nick.setHostname(whoreply.getHostname());
			nick.setIdent(whoreply.getIdent());
			nick.setServer(whoreply.getServer());
			
			Hashtable<Character, Character> nickPrefixes = network.getNetworkSettings().getPrefixes();
			
			//Strip away H or G (Here/Gone)
			for(Character c : whoreply.getStatus().substring(1).toCharArray()) {
				if(nickPrefixes.containsKey(c)) {
					nick.addMode(channel, nickPrefixes.get(c));
				}
				if(c == '*') {
					nick.setIrcOp(true);
				}
			}
		}
	}
	
	private Nick getNick(String nickname) {
		return network.getNick(nickname);
	}
	
	private Channel getChannel(String channelname) {
		return network.getChannel(channelname);
	}
	
	private Collection<Channel> getChannels() {
		return network.getChannels();
	}
}
