package no.ntnu.online.onlineguru.plugin.plugins.nickserv;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.*;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of authentication usernames for nicks on Networks that the bot attends.
 *
 * @author Håvard Slettvold
 */
public class NickServ implements Plugin {

    private Wand wand;

    private Map<Network, AuthHandler> authHandlers;

    static Logger logger = Logger.getLogger(NickServ.class);


    public NickServ() {
        authHandlers = new HashMap<Network, AuthHandler>();
    }

    public String getDescription() {
        return "Keeps track of all logged in nicks that share channels with OnlineGuru.";
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case CONNECT:
                handleConnectEvent((ConnectEvent)e);
                break;
            case JOIN:
                handleJoinEvent((JoinEvent)e);
                break;
            case NICK:
                handleNickEvent((NickEvent)e);
                break;
            case NUMERIC:
                handleNumericEvent((NumericEvent)e);
                break;
            case PART:
                handlePartEvent((PartEvent)e);
                break;
            case PRIVMSG:
                handlePrivMsgEvent((PrivMsgEvent)e);
                break;
            case QUIT:
                handleQuitEvent((QuitEvent) e);
                break;
        }
    }

    /**
     * If connected, add new authHandler for that Network.
     *
     * @param e{@link ConnectEvent} that triggered.
     */
    private void handleConnectEvent(ConnectEvent e) {
        authHandlers.put(e.getNetwork(), new AuthHandler());
    }

    /**
     * Someone joined a channel.
     * If it was the bot, /who the whole channel.
     * Otherwise /who the person that joined.
     * This will provoke 354 {@link EventType} NUMERIC events, which contain nick and its registered username.
     *
     * @param e {@link JoinEvent} to be investigated.
     */
    private void handleJoinEvent(JoinEvent e) {
        if (e.getNick().equals(wand.getMyNick(e.getNetwork()))) {
            wand.sendServerMessage(e.getNetwork(), "WHO "+ e.getChannel() +" %na");
        }
        else {
            wand.sendServerMessage(e.getNetwork(), "WHO "+ e.getNick() +" %na");
        }
    }

    /**
     * If a user changes nick, their new nick should be updated in the authList.
     *
     * @param e {@link NickEvent} to be investigated.
     */
    private void handleNickEvent(NickEvent e) {
         authHandlers.get(e.getNetwork())
                 .updateNick(e.getOldNick(), e.getNewNick());
    }

    /**
     * Syntax on NUMERIC 354;
     * :<server> 354 <calling nick> <nick> <nick's username>
     *
     * If the nick is not registered, <nick's username> will be "0"
     *
     * @param e {@link NumericEvent} to be investigated.
     */
    private void handleNumericEvent(NumericEvent e) {
        if (e.getNumeric() == 354) {
            authHandlers.get(e.getNetwork())
                    .addNick(e.getParamaters().get(1), e.getParamaters().get(2));

            //logger.debug("Added '"+e.getParamaters().get(1)+ "' with auth; "+e.getParamaters().get(2));
        }
    }

    /**
     * When a nick parts a channel, the nick should be removed from the
     * authList if that was the last common channel with the bot.
     *
     * @param e {@link PartEvent} to be investigated.
     */
    private void handlePartEvent(PartEvent e) {
        if (e.getNetwork().commonChannels(e.getNick()).size() < 1) {
            authHandlers.get(e.getNetwork())
                    .removeNick(e.getNick());
        }
    }

    /**
     * Handles commands in channel and private.
     *
     * @param e {@link PrivMsgEvent} to be used.
     */
    private void  handlePrivMsgEvent(PrivMsgEvent e) {
        String[] message = e.getMessage().split(" ");

        if (message[0].equals("!whois") || message[0].equals("!who")) {
            AuthHandler ah = authHandlers.get(e.getNetwork());

            if (ah.getUsername(e.getSender()).equals("0")) {
                wand.sendMessageToTarget(e.getNetwork(), e.getTarget(), "[whois] "+ message[1] +" is not registered with NickServ on "+ e.getNetwork().getServerAlias());
            }
            else {
                wand.sendMessageToTarget(e.getNetwork(), e.getTarget(), "[whois] "+ message[1] +" is registered as '"+ ah.getUsername(e.getSender()) +"' on "+ e.getNetwork().getServerAlias());
            }
        }
    }

    /**
     * If a user quits, their nick will be removed from the authList.
     *
     * @param e {@link QuitEvent} to be investigated.
     */
    private void handleQuitEvent(QuitEvent e) {
        authHandlers.get(e.getNetwork())
                .removeNick(e.getNick());
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.CONNECT);
        eventDistributor.addListener(this, EventType.JOIN);
        eventDistributor.addListener(this, EventType.NICK);
        eventDistributor.addListener(this, EventType.NUMERIC);
        eventDistributor.addListener(this, EventType.PART);
        eventDistributor.addListener(this, EventType.PRIVMSG);
        eventDistributor.addListener(this, EventType.QUIT);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

    /*
     * Public methods for NickServ.
     */

    /**
     * Checks if nick is authed with a service.
     *
     * @param network Network to search on.
     * @param nick String nickname to be checked.
     * @return boolean True is authed with a service.
     */
    public boolean isAuthed(Network network, String nick) {
        return authHandlers.get(network)
                .isAuthed(nick);
    }

    /**
     * Retrieves the username for a nick.
     *
     * @param network Network to search on.
     * @param nick String nickname to find username for.
     * @return String with nick's username
     */
    public String getUsername(Network network, String nick) {
        return authHandlers.get(network)
                .getUsername(nick);
    }
}
