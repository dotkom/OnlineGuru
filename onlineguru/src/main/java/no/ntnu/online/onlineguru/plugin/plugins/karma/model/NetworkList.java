package no.ntnu.online.onlineguru.plugin.plugins.karma.model;

import no.fictive.irclib.model.network.Network;

import java.util.HashMap;

/**
 * @author Håvard Slettvold
 */
public class NetworkList {

    private HashMap<String, ChannelList> networks = new HashMap<String, ChannelList>();

    public int increaseKarma(Network network, String channel, String nick) {
        return increaseKarma(network.getServerAlias(), channel, nick, 1);
    }

    public int increaseKarma(Network network, String channel, String nick, int amount) {
        return increaseKarma(network.getServerAlias(), channel, nick, amount);
    }

    private int increaseKarma(String network, String channel, String nick, int amount) {
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
        return decreaseKarma(network.getServerAlias(), channel, nick, 1);
    }

    public int decreaseKarma(Network network, String channel, String nick, int amount) {
        return decreaseKarma(network.getServerAlias(), channel, nick, amount);
    }

    protected int decreaseKarma(String network, String channel, String nick, int amount) {
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
        if (networks.containsKey(network.getServerAlias())) {
            return networks.get(network.getServerAlias()).getKarma(channel, nick);
        }
        else {
            return 0;
        }
    }

}
