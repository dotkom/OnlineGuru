package no.ntnu.online.onlineguru.utils;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.PluginManager;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import java.util.Enumeration;

public class IrcWand implements Wand {
	
	private OnlineGuru onlineguru;
	private PluginManager pluginManager;
	
	public IrcWand(OnlineGuru onlineguru, PluginManager pluginManager) {
		this.onlineguru = onlineguru;
		this.pluginManager = pluginManager;
	}
	
	public Plugin getPlugin(String pluginClassName) {
		return pluginManager.getPlugin(pluginClassName);
	}
	
	public Network getNetworkByAlias(String networkalias) {
		return onlineguru.getNetworkByAlias(networkalias);
	}

    /**
     * Retreives all networks the bot is connected to
     * @return List of Network's
     */
    public Enumeration<Network> getNetworks() {
        return onlineguru.getNetworks();
    }
	
	public void op(Network network, String user, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " +o " + user);
	}
	
	public void deop(Network network, String user, String channel) {
		onlineguru.sendMessageToServer(network, "MODE "  + channel + " -o " + user);
	}
	
	public void voice(Network network, String user, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " +v " + user);
	}
	
	public void devoice(Network network, String user, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " -v " + user);
	}
	
	public void join(Network network, String channel) {
		onlineguru.sendMessageToServer(network, "JOIN " + channel);
	}
	
	public void join(Network network, String channel, String password) {
		onlineguru.sendMessageToServer(network, "JOIN " + channel + " " + password);
	}
	
	public void part(Network network, String channel) {
		onlineguru.sendMessageToServer(network, "PART " + channel);
	}
	
	public void kick(Network network, String user, String channel) {
		onlineguru.sendMessageToServer(network, "KICK " + channel + " " + user);
	}
	
	public void kick(Network network, String user, String channel, String reason) {
		onlineguru.sendMessageToServer(network, "KICK " + channel + " " + user + " :" + reason);
	}
	
	public void ban(Network network, String mask, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " +b " + mask);
	}
	
	public void unban(Network network, String mask, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " -b " + mask);
	}
	
	public void kickban(Network network, String user, String channel) {
		kick(network, user, channel);
		ban(network, user, channel);
	}
	
	public void kickban(Network network, String user, String channel, String reason) {
		kick(network, user, channel, reason);
		ban(network, user, channel);
	}
	
	public void mute(Network network, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " +m");
	}
	
	public void unmute(Network network, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " -m");
	}
	
	public void setChannelPassword(Network network, String channel, String password) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " +k " + password);
	}
	
	public void removeChannelPassword(Network network, String channel) {
		onlineguru.sendMessageToServer(network, "MODE " + channel + " -k");
	}
	
	public void setTopic(Network network, String channel, String message) {
		onlineguru.sendMessageToServer(network, "TOPIC " + channel + " :" + message);
	}
	
	public void sendMessageToTarget(Network network, String target, String message) {
		onlineguru.sendMessageToServer(network, "PRIVMSG " + target + " :" + message);
	}
	
	public void sendServerMessage(Network network, String message) {
		onlineguru.sendMessageToServer(network, message);
	}
	
	public void sendNoticeToTarget(Network network, String target, String message) {
		onlineguru.sendMessageToServer(network, "NOTICE " + target + " :" + message);
	}

    public void sendCTCPToTarget(Network network, String target, String command, String value) {
        onlineguru.sendMessageToServer(network, "PRIVMSG " + target + " :" + (char)1 + command + " " + value + (char)1);
    }

    public void sendCTCPReplyToTarget(Network network, String target, String command, String value) {
        onlineguru.sendMessageToServer(network, "NOTICE " + target + " :" + (char)1 + command + " " + value + (char)1);
    }
	
	public String getMyNick(Network network) {
		return network.getProfile().getCurrentNickname();
	}
	
	public boolean isMe(Network network, String nickname) {
		return network.getProfile().getCurrentNickname().equals(nickname);
	}
	
	public boolean amIOp(Network network, String channel) {
		return network.getNick(network.getProfile().getNickname()).isOp(channel);
	}
	
	public boolean amIOnChannel(Network network, String channel) {
		return network.getChannel(channel) != null;
	}
	
	public boolean isUserVisible(Network network, String nickname) {
		return network.getNick(nickname) != null;
	}

    public String getTarget(PrivMsgEvent event) {
        return event.getTarget().equals(getMyNick(event.getNetwork())) ? event.getSender() : event.getChannel();
    }
	
	public void quit(Network network) {
		network.disconnect();
	}
}
