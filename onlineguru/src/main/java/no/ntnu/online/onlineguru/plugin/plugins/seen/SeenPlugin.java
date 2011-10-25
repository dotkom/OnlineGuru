package no.ntnu.online.onlineguru.plugin.plugins.seen;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.*;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.nick.Nick;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.history.HistoryPlugin;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.history.History;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;


public class SeenPlugin implements PluginWithDependencies {
    private History historyPlugin;
    private static final String DESCRIPTION = "Find the last activity of persons";
    private Wand wand;
    private static final String TRIGGER = "!seen";
    private Logger logger = Logger.getLogger(SeenPlugin.class);

    public SeenPlugin() {}

    public SeenPlugin(Wand fakeWand, History history) {
        this.wand = fakeWand;
        this.historyPlugin = history;
    }

    @Override
    public String[] getDependencies() {
        return new String[]{"HistoryPlugin"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof HistoryPlugin) {
            historyPlugin = ((HistoryPlugin) plugin).getHistory();
        }
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG:
                PrivMsgEvent privMsgEvent = (PrivMsgEvent)e;
                if (isMessageForPlugin(privMsgEvent)) {
                     sendReply(handleSeenQuery(privMsgEvent.getMessage().split("\\s+")[1]), privMsgEvent);
                }
                break;
        }
    }

    protected void sendReply(String message, PrivMsgEvent target) {
        if (message != null && !message.isEmpty())
            wand.sendMessageToTarget(target.getNetwork(), target.getTarget(), message);
        else
            logger.error("message to sent was empty, something is clearly wrong");
    }

    protected String handleSeenQuery(String nick) {
        List<Event> lastEvents = historyPlugin.getLastEvents(new Nick(nick));
        if (lastEvents.size()>0) {
            return generateReplyString(lastEvents.get(0));
        } else
            return "Can't find any history on "+ nick;
    }

    protected String generateReplyString(Event lastEvent) {
        switch (lastEvent.getEventType()) {
            case JOIN:
                JoinEvent joinEvent = (JoinEvent) lastEvent;
                return String.format("%s joined channel %s", joinEvent.getNick(), joinEvent.getChannel());

            case PART:
                PartEvent partEvent = (PartEvent) lastEvent;
                return String.format("%s parted channel %s", partEvent.getNick(), partEvent.getChannel());

            case KICK:
                KickEvent kickEvent = (KickEvent) lastEvent;
                return String.format("%s kicked %s on %s with message: %s",
                        kickEvent.getNickKicking(),
                        kickEvent.getNickKicking(),
                        kickEvent.getChannel(),
                        kickEvent.getReason()
                        );

            case MODE:
                ModeEvent modeEvent = (ModeEvent) lastEvent;
                return String.format("%s sets mode %s on %s",
                        modeEvent.getNick(),
                        Arrays.toString(modeEvent.getModes().toArray()),
                        modeEvent.getChannel()
                        );

            case NICK:
                NickEvent nickEvent = (NickEvent) lastEvent;
                return String.format("%s changed nick from %s",
                        nickEvent.getNewNick(),
                        nickEvent.getOldNick());

            case TOPIC:
                TopicEvent topicEvent = (TopicEvent) lastEvent;
                return String.format("%s updated topic on channel %s with %s",
                        topicEvent.getChangedByNick(),
                        topicEvent.getChannel(),
                        topicEvent.getTopic()
                        );
            case QUIT:
                QuitEvent quitEvent = (QuitEvent) lastEvent;
                return String.format("%s quit irc on network %s with message: %s",
                        quitEvent.getNick(),
                        quitEvent.getNetwork().getServerAlias(),
                        quitEvent.getQuitmessage()
                        );
            case PRIVMSG:
                PrivMsgEvent privMsgEvent = (PrivMsgEvent)lastEvent;
                // only channel messages should be stored in history!
                assert(privMsgEvent.isChannelMessage());
                return String.format("%s sent a message to %s and said: %s",
                        privMsgEvent.getSender(),
                        privMsgEvent.getChannel(),
                        privMsgEvent.getMessage());
            case NOTICE:
                NoticeEvent noticeEvent = (NoticeEvent)lastEvent;
                return String.format("%s sent a notice to %s with message %s",
                         noticeEvent.getSender(),
                        noticeEvent.getTarget(),
                        noticeEvent.getNotice()
                        );
            default:
                return "unknown event recorded .. ";
        }
    }

    protected boolean isMessageForPlugin(PrivMsgEvent privMsgEvent) {
        return privMsgEvent.getMessage().startsWith(TRIGGER);
    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
