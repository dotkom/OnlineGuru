package no.ntnu.online.onlineguru.plugin.plugins.flags.model;

/**
 * @author Håvard Slettvold
 */
public enum Flag {

    ANYONE (""),  // This flag is never given, only passed to allow anyone
    B ("B"),  // can unban
    b ("b"),  // can ban
    f ("f"),  // can change channel flags
    i ("i"),  // allows invites from the bot
    K ("K"),  // can kick
    L ("L"),  // can set channel limit
    m ("m"),  // can moderate
    o ("o"),  // can op
    O ("O"),  // can deop
    t ("t"),  // can use topic
    v ("v"),  // can voice
    V ("V");  // can unvoice

    private final String value;

    Flag(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

}
