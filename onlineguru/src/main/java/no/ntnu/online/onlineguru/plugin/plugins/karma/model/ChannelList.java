package no.ntnu.online.onlineguru.plugin.plugins.karma.model;

import java.util.HashMap;

/**
 * @author Håvard Slettvold
 */
public class ChannelList {

    private HashMap<String, KarmaList> channels = new HashMap<String, KarmaList>();

    protected int changeKarma(String channel, String nick, int amount) {
        if (channels.containsKey(channel)) {
            return channels.get(channel).changeKarma(nick, amount);
        }
        else {
            KarmaList kl = new KarmaList();
            channels.put(channel, kl);
            return kl.changeKarma(nick, amount);
        }
    }

    protected int getKarma(String channel, String nick) {
        if (channels.containsKey(channel)) {
            return channels.get(channel).getKarma(nick);
        }
        else {
            return 0;
        }
    }

}
