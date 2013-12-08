package no.ntnu.online.onlineguru.plugin.plugins.karma;

/**
 * @author Håvard Slettvold
 */
public class ResponseKarmaFind {

    public String sender;
    public String target;
    public int amount;

    public ResponseKarmaFind(String sender, String target, int amount) {
        this.sender = sender;
        this.target = target;
        this.amount = amount;
    }

}