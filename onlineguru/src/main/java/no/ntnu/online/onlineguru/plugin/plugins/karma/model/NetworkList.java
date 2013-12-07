package no.ntnu.online.onlineguru.plugin.plugins.karma.model;

import no.fictive.irclib.model.network.Network;

import java.util.HashMap;

/**
 * @author Håvard Slettvold
 */
public class NetworkList {

    private HashMap<Network, ChannelList> networks = new HashMap<Network, ChannelList>();

    public int increaseKarma(Network network, String channel, String nick) {
        return increaseKarma(network, channel, nick, 1);
    }

    public int increaseKarma(Network network, String channel, String nick, int amount) {
        if (networks.containsKey(network)) {
            return networks.get(network).increaseKarma(channel, nick, amount);
        }
        else {
            ChannelList cl = new ChannelList();
            networks.put(network, cl);
            return cl.increaseKarma(channel, nick, amount);
        }
    }

    public int decreaseKarma(Network network, String channel, String nick) {
        return decreaseKarma(network, channel, nick, 1);
    }

    public int decreaseKarma(Network network, String channel, String nick, int amount) {
        if (networks.containsKey(network)) {
            return networks.get(network).decreaseKarma(channel, nick, amount);
        }
        else {
            ChannelList cl = new ChannelList();
            networks.put(network, cl);
            return cl.decreaseKarma(channel, nick, amount);
        }
    }

    public int getKarma(Network network, String channel, String nick) {
        if (networks.containsKey(network)) {
            return networks.get(network).getKarma(channel, nick);
        }
        else {
            return 0;
        }
    }

}
