package no.ntnu.online.onlineguru.plugin.plugins.karma.model;

import no.fictive.irclib.model.network.Network;

import java.util.HashMap;

/**
 * @author Håvard Slettvold
 */
public class NetworkList {

    private HashMap<String, ChannelList> networks = new HashMap<String, ChannelList>();

    public int changeKarma(Network network, String channel, String nick, int amount) {
        if (networks.containsKey(network)) {
            return networks.get(network.getServerAlias()).changeKarma(channel, nick, amount);
        }
        else {
            ChannelList cl = new ChannelList();
            networks.put(network.getServerAlias(), cl);
            return cl.changeKarma(channel, nick, amount);
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
