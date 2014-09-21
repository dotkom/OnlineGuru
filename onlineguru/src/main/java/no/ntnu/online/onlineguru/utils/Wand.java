package no.ntnu.online.onlineguru.utils;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.model.Plugin;

import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: rockj
 * Date: 11/15/10
 * Time: 5:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Wand {
    Plugin getPlugin(String pluginClassName);

    Network getNetworkByAlias(String networkalias);

    Enumeration<Network> getNetworks();

    void op(Network network, String user, String channel);

    void deop(Network network, String user, String channel);

    void voice(Network network, String user, String channel);

    void devoice(Network network, String user, String channel);

    void join(Network network, String channel);

    void join(Network network, String channel, String password);

    void part(Network network, String channel);

    void kick(Network network, String user, String channel);

    void kick(Network network, String user, String channel, String reason);

    void ban(Network network, String mask, String channel);

    void unban(Network network, String mask, String channel);

    void kickban(Network network, String user, String channel);

    void kickban(Network network, String user, String channel, String reason);

    void mute(Network network, String channel);

    void unmute(Network network, String channel);

    void setChannelPassword(Network network, String channel, String password);

    void removeChannelPassword(Network network, String channel);

    void setTopic(Network network, String channel, String message);

    void sendMessageToTarget(Network network, String target, String message);

    void sendServerMessage(Network network, String message);

    void sendNoticeToTarget(Network network, String target, String message);

    void sendCTCPToTarget(Network network, String target, String command, String value);

    void sendCTCPReplyToTarget(Network network, String target, String command, String value);

    String getMyNick(Network network);

    boolean isMe(Network network, String nickname);

    boolean amIOp(Network network, String channel);

    boolean amIOnChannel(Network network, String channel);

    boolean isUserVisible(Network network, String nickname);

    void quit(Network network);
}
