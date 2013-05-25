package no.ntnu.online.onlineguru.plugin.plugins.history;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.*;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.nick.Nick;
import no.ntnu.online.onlineguru.OnlineGuru;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.service.services.history.History;
import org.apache.log4j.Logger;


public class HistoryPlugin implements Plugin {
    private History history;
    private static final String DESCRIPTION = "History plugin which keeps a record of events that users do that OnlineGuru picks up";
    static Logger logger = Logger.getLogger(HistoryPlugin.class);

    public HistoryPlugin() {
        history = OnlineGuru.serviceLocator.getInstance(History.class);
    }

    public History getHistory() {
        return history;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case JOIN:
                JoinEvent joinEvent = (JoinEvent) e;
                history.appendHistory(new Nick(joinEvent.getNick()), e);
                break;
            case PART:
                PartEvent partEvent = (PartEvent) e;
                history.appendHistory(new Nick(partEvent.getNick()), e);
                break;
            case KICK:
                KickEvent kickEvent = (KickEvent) e;
                history.appendHistory(new Nick(kickEvent.getNickKicking()), e);
                history.appendHistory(new Nick(kickEvent.getNickKicked()), e);
                break;
            case MODE:
                ModeEvent modeEvent = (ModeEvent) e;
                history.appendHistory(new Nick(modeEvent.getNick()), e);
                break;
            case NICK:
                NickEvent nickEvent = (NickEvent) e;
                history.nickChangeHistory(nickEvent); // it also appends history!
                break;
            case TOPIC:
                TopicEvent topicEvent = (TopicEvent) e;
                history.appendHistory(new Nick(topicEvent.getChangedByNick()), e);
                break;
            case QUIT:
                QuitEvent quitEvent = (QuitEvent) e;
                history.appendHistory(new Nick(quitEvent.getNick()), e);
                break;
            case PRIVMSG:
                PrivMsgEvent privMsgEvent = (PrivMsgEvent)e;
                if (privMsgEvent.isChannelMessage()) {
                    history.appendHistory(new Nick(privMsgEvent.getSender()), e);
                    history.appendChannelHistory(new Channel(privMsgEvent.getChannel()), privMsgEvent);
                }
                break;
            case NOTICE:
                NoticeEvent noticeEvent = (NoticeEvent)e;
                history.appendHistory(new Nick(noticeEvent.getSender()),e);
                break;
            default:
                logger.error("unknown event received, not recorded in history");
        }
    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.JOIN);
        eventDistributor.addListener(this, EventType.PART);
        eventDistributor.addListener(this, EventType.KICK);
        eventDistributor.addListener(this, EventType.MODE);
        eventDistributor.addListener(this, EventType.NICK);
        eventDistributor.addListener(this, EventType.TOPIC);
        eventDistributor.addListener(this, EventType.QUIT);
        eventDistributor.addListener(this, EventType.PRIVMSG);
        eventDistributor.addListener(this, EventType.NOTICE);
    }

    @Override
    public void addWand(Wand wand) {
        // no need for wand.
    }
}
