package no.ntnu.online.onlineguru.plugin.plugins.nickserv;

import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.commons.lang.NotImplementedException;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FakeWand suited for the EmailimplTest
 *
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class FakeWand implements Wand {
    private ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
    private ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

    public FakeWand(ConcurrentHashMap<String, Network> networks, ConcurrentHashMap<String, Channel> channels) {
        this.networks = networks;
        this.channels = channels;
    }

    public Plugin getPlugin(String pluginClassName) {
        throw new NotImplementedException();
    }

    public Network getNetworkByAlias(String networkalias) {
        return networks.get(networkalias);
    }

    public Enumeration<Network> getNetworks() {
        return networks.elements();
    }

    public void op(Network network, String user, String channel) {
        // ignore
    }

    public void deop(Network network, String user, String channel) {
        // ignore
    }

    public void voice(Network network, String user, String channel) {
        // ignore
    }

    public void devoice(Network network, String user, String channel) {
        // ignore
    }

    public void join(Network network, String channel) {
        // ignore
    }

    public void join(Network network, String channel, String password) {
        // ignore
    }

    public void part(Network network, String channel) {
        // ignore
    }

    public void kick(Network network, String user, String channel) {
        // ignore
    }

    public void kick(Network network, String user, String channel, String reason) {
        // ignore
    }

    public void ban(Network network, String mask, String channel) {
        // ignore
    }

    public void unban(Network network, String mask, String channel) {
        // ignore
    }

    public void kickban(Network network, String user, String channel) {
        // ignore
    }

    public void kickban(Network network, String user, String channel, String reason) {
        // ignore
    }

    public void mute(Network network, String channel) {
        // ignore
    }

    public void unmute(Network network, String channel) {
        // ignore
    }

    public void setChannelPassword(Network network, String channel, String password) {
        // ignore
    }

    public void removeChannelPassword(Network network, String channel) {
        // ignore
    }

    public void setTopic(Network network, String channel, String message) {
        // ignore
    }

    public void sendMessageToTarget(Network network, String target, String message) {
        // ignore
    }

    public void sendServerMessage(Network network, String message) {
        // ignore
    }

    public void sendNoticeToTarget(Network network, String target, String message) {
        // ignore
    }

    public void sendCTCPToTarget(Network network, String target, String message) {
        // ignore
    }

    public String getMyNick(Network network) {
        return nick;
    }

    public boolean isMe(Network network, String nickname) {
        if (nick.equals(nickname)) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean amIOp(Network network, String channel) {
        throw new NotImplementedException();
    }

    public boolean amIOnChannel(Network network, String channel) {
        if (networks.containsValue(network) && channels.containsKey(channel)) {
            return true;
        }
        return false;
    }

    public boolean isUserVisible(Network network, String nickname) {
        throw new NotImplementedException();
    }

    public void quit(Network network) {
        // ignore
    }


    /*
        Test specific methods
     */

    private String nick;

    public void setNick(String nick) {
        this.nick = nick;
    }
}
