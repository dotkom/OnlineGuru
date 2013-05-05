package no.ntnu.online.onlineguru.plugin.plugins.flags.model;

/**
 * This enum is the collection of all flags that are available.
 *
 * If a new flag is needed, all you have to do is add it to the list. Superusers will
 * automatically have all flags, but you can start using the new flag immediately.
 *
 * Flags need to consist of one character.
 *
 * @author Håvard Slettvold
 */
public enum Flag {

    ANYONE (""),  // This flag is never given, only passed to allow anyone
    a ("a"),  // small administrative tasks
    A ("A"),  // large administrative tasks
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
