package no.ntnu.online.onlineguru.plugin.plugins.karma.model;

import java.util.HashMap;

/**
 * @author Håvard Slettvold
 */
public class KarmaList {

    private HashMap<String, Integer> karma = new HashMap<String, Integer>();

    protected int changeKarma(String nick, int amount) {
        if (karma.containsKey(nick)) {
            int k = karma.get(nick);
            k += amount;
            karma.put(nick, k);
            return k;
        }
        else {
            karma.put(nick, amount);
            return amount;
        }
    }

    protected int getKarma(String nick) {
        if (karma.containsKey(nick)) {
            return karma.get(nick);
        }
        return 0;
    }
}
