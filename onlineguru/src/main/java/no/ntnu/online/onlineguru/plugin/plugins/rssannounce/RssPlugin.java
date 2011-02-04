package no.ntnu.online.onlineguru.plugin.plugins.rssannounce;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.EmailImpl;
import no.ntnu.online.onlineguru.utils.WandRepository;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.webserver.WebServer;

import java.util.List;

/**
 * Roy Sindre Norangshol
 * http://www.roysindre.no
 * <p/>
 * Date: 11/30/10
 * Time: 4:14 PM
 */
public class RssPlugin implements Plugin {
    static Logger logger = Logger.getLogger(RssPlugin.class);

    private static final String DESCRIPTION = "Announces new RSS entries to the IRC channel";
    private static final char CHANNEL_TRIGGER = '!';
    private static final String TRIGGER = "rss";
    protected static final String DB_FOLDER = "database/";
    private RssChecker rssChecker;

    private WandRepository wandRepository;

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                PrivMsgEvent privMsgEvent = ((PrivMsgEvent) e);
                if (privMsgEvent.isChannelMessage()) {
                    handleChannelMessage(privMsgEvent);
                }
                break;
            }
        }

    }

    private void handleChannelMessage(PrivMsgEvent privMsgEvent) {

    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        //eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(WandRepository wandRepository) {
        this.wandRepository = wandRepository;
        rssChecker = new RssChecker(wandRepository);
    }


}
