package no.ntnu.online.onlineguru.plugin.plugins.karma.model;

import java.util.HashMap;

/**
 * @author Håvard Slettvold
 */
public class ChannelList {

    private HashMap<String, KarmaList> channels = new HashMap<String, KarmaList>();

    protected int increaseKarma(String channel, String nick, int amount) {
        if (channels.containsKey(channel)) {
            return channels.get(channel).increaseKarma(nick, amount);
        }
        else {
            KarmaList kl = new KarmaList();
            channels.put(channel, kl);
            return kl.increaseKarma(nick, amount);
        }
    }

    protected int decreaseKarma(String channel, String nick, int amount) {
        if (channels.containsKey(channel)) {
            return channels.get(channel).decreaseKarma(nick, amount);
        }
        else {
            KarmaList kl = new KarmaList();
            channels.put(channel, kl);
            return kl.decreaseKarma(nick, amount);
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
