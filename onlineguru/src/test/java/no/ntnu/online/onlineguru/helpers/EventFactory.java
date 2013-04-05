package no.ntnu.online.onlineguru.helpers;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.command.*;
import no.fictive.irclib.model.network.Network;

/**
 * This helper class will produce Events which are true to the packets you would get in
 * a real connection.
 *
 * Feeding any of these methods with a Network = null will insert a generated network with alias "default".
 *
 * @author Håvard Slettvold
 */
public class EventFactory {

    private static Network createNetwork(String networkAlias) {
        Network network = new Network();
        network.setServerAlias(networkAlias);
        return network;
    }

    private static IRCEventPacket makePacket(String rawline) {
        return new IRCEventPacket(rawline);
    }

    /**
     * Creates a JoinEvent used for testing.
     *
     * Passing "network = null" creates the event on a network with alias "default"
     *
     * @param network Network to create event on. Can be null.
     * @param channel channel name for the event. Only string because IRCEventPacket doesn't handle Channel objects.
     * @param nick String for the nick that triggered the event. Only string because IRCEventPacket doesn't handle Nick objects.
     * @return JoinEvent
     */
    public static JoinEvent createJoinEvent(Network network, String channel, String nick) {
        Network nw = network == null ? createNetwork("default") : network;
        return new JoinEvent(makePacket(String.format(":%s!ident@unit.test.hostname JOIN %s", nick, channel)), network);
    }

    /**
     * Creates a PartEvent used for testing.
     *
     * Passing "network = null" creates the event on a network with alias "default"
     *
     * @param network Network to create event on. Can be null.
     * @param channel channel name for the event. Only string because IRCEventPacket doesn't handle Channel objects.
     * @param nick String for the nick that triggered the event. Only string because IRCEventPacket doesn't handle Nick objects.
     * @param message String message.
     * @return PartEvent
     */
    public static PartEvent createPartEvent(Network network, String channel, String nick, String message) {
        Network nw = network == null ? createNetwork("default") : network;
        return new PartEvent(makePacket(String.format(":%s!ident@unit.test.hostname PART %s :%s", nick, channel, message)), network);
    }

    /**
     * Creates a QuitEvent used for testing.
     *
     * Passing "network = null" creates the event on a network with alias "default"
     *
     * @param network Network to create event on. Can be null.
     * @param nick String for the nick that triggered the event. Only string because IRCEventPacket doesn't handle Nick objects.
     * @param message String message.
     * @return QuitEvent
     */
    public static QuitEvent createQuitEvent(Network network, String nick, String message) {
        Network nw = network == null ? createNetwork("default") : network;
        return new QuitEvent(makePacket(String.format(":%s!ident@unit.test.hostname QUIT :%s", nick, message)), network);
    }

    /**
     * Creates a NickEvent used for testing.
     *
     * Passing "network = null" creates the event on a network with alias "default"
     *
     * @param network Network to create event on. Can be null.
     * @param oldNick String for the nick that triggered the event. Only string because IRCEventPacket doesn't handle Nick objects.
     * @param newNick String for the nick that triggered the event. Only string because IRCEventPacket doesn't handle Nick objects.
     * @return NickEvent
     */
    public static NickEvent createNickEvent(Network network, String oldNick, String newNick) {
        Network nw = network == null ? createNetwork("default") : network;
        return new NickEvent(makePacket(String.format(":%s!ident@unit.test.hostname NICK :%s", oldNick, newNick)), network);
    }

    /**
     * Creates a PrivMsgEvent used for testing.
     *
     * Passing "network = null" creates the event on a network with alias "default"
     *
     * @param network Network to create event on. Can be null.
     * @param nick String for the nick that triggered the event. Only string because IRCEventPacket doesn't handle Nick objects.
     * @param target String containing channel name or nick for the event.
     * @param message String message.
     * @return PrivMsgEvent
     */
    public static PrivMsgEvent createPrivMsgEvent(Network network, String nick, String target, String message) {
        Network nw = network == null ? createNetwork("default") : network;
        return new PrivMsgEvent(makePacket(String.format(":%s!ident@unit.test.hostname PRIVMSG %s :%s", nick, target, message)), network);
    }

}
