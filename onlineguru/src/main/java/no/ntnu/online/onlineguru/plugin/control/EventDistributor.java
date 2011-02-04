package no.ntnu.online.onlineguru.plugin.control;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.RPL.RPL_NAMREPLY;
import no.fictive.irclib.event.container.RPL.RPL_WHOREPLY;
import no.fictive.irclib.event.container.command.CTCPEvent;
import no.fictive.irclib.event.container.command.ConnectEvent;
import no.fictive.irclib.event.container.command.ErrorEvent;
import no.fictive.irclib.event.container.command.InviteEvent;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.KickEvent;
import no.fictive.irclib.event.container.command.KillEvent;
import no.fictive.irclib.event.container.command.ModeEvent;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.NoticeEvent;
import no.fictive.irclib.event.container.command.NumericEvent;
import no.fictive.irclib.event.container.command.PartEvent;
import no.fictive.irclib.event.container.command.PingEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.container.command.QuitEvent;
import no.fictive.irclib.event.container.command.TopicEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.model.Plugin;

public class EventDistributor {

	static Logger logger = Logger.getLogger(EventDistributor.class);
	
	private HashMap<EventType, ArrayList<Plugin>> listCollection = new HashMap<EventType, ArrayList<Plugin>>(); 
	
	private ArrayList<Plugin> connectListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> ctcpListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> errorListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> inviteListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> joinListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> kickListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> killListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> modeListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> namesReplyListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> nickListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> noticeListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> numericListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> partListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> pingListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> privMsgListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> quitListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> topicListeners = new ArrayList<Plugin>();
	private ArrayList<Plugin> whoReplyListeners = new ArrayList<Plugin>();
	
	public EventDistributor() {		
		listCollection.put(EventType.CONNECT, connectListeners);
		listCollection.put(EventType.CTCP, ctcpListeners);
		listCollection.put(EventType.ERROR, errorListeners);
		listCollection.put(EventType.INVITE, inviteListeners);
		listCollection.put(EventType.JOIN, joinListeners);
		listCollection.put(EventType.KICK, kickListeners);
		listCollection.put(EventType.KILL, kickListeners);
		listCollection.put(EventType.MODE, modeListeners);
		listCollection.put(EventType.RPL_NAMREPLY, namesReplyListeners);
		listCollection.put(EventType.NICK, nickListeners);
		listCollection.put(EventType.NOTICE, noticeListeners);
		listCollection.put(EventType.NUMERIC, numericListeners);
		listCollection.put(EventType.PART, partListeners);
		listCollection.put(EventType.PING, pingListeners);
		listCollection.put(EventType.PRIVMSG, privMsgListeners);
		listCollection.put(EventType.QUIT, quitListeners);
		listCollection.put(EventType.TOPIC, topicListeners);
		listCollection.put(EventType.RPL_WHOREPLY, whoReplyListeners);		
	}
	
	public void addListener(Plugin plugin, EventType eventType) {
		this.listCollection.get(eventType).add(plugin);
	}
	
	public void removeListener(Plugin plugin, EventType eventType) {
		this.listCollection.get(eventType).remove(plugin);
	}
	
	public synchronized void handleEvent(Event event) {
		switch (event.getEventType()) {
			case CONNECT:
	        	fireEvent((ConnectEvent) event, connectListeners);
	        	break;
	        case CTCP:
		    	fireEvent((CTCPEvent) event, ctcpListeners);
		    	break;
	        case ERROR:
	        	fireEvent((ErrorEvent) event, errorListeners);
	        	break;
	        case INVITE:
            	fireEvent((InviteEvent) event, inviteListeners);
            	break;
            case JOIN:
            	fireEvent((JoinEvent) event, joinListeners);
            	break;
            case KICK:
            	fireEvent((KickEvent) event, kickListeners);
            	break;
            case KILL:
            	fireEvent((KillEvent) event, killListeners);
            	break;
            case MODE:
            	fireEvent((ModeEvent) event, modeListeners);
            	break;
            case NICK:
            	fireEvent((NickEvent) event, nickListeners);
            	break;
            case NOTICE:
            	fireEvent((NoticeEvent) event, noticeListeners);
            	break;
            case NUMERIC:
            	fireEvent((NumericEvent) event, numericListeners);
            	break;
            case PART:
            	fireEvent((PartEvent) event, partListeners);
            	break;
            case PING:
            	fireEvent((PingEvent) event, pingListeners);
            	break;
            case PRIVMSG:
	        	fireEvent((PrivMsgEvent) event, privMsgListeners);
	        	break;
            case QUIT:
            	fireEvent((QuitEvent) event, quitListeners);
            	break;
            case TOPIC:
            	fireEvent((TopicEvent) event, topicListeners);
            	break;
            case RPL_NAMREPLY:
            	fireEvent((RPL_NAMREPLY) event, namesReplyListeners);
            	break;
            case RPL_WHOREPLY:
            	fireEvent((RPL_WHOREPLY) event, whoReplyListeners);
            	break;
            default: {
                logger.error("Registered unknown event: " + event.getRawData());
                return;
            }
        }
	}
	
	synchronized void fireEvent(Event e, ArrayList<Plugin> listeners) {
		if (!listeners.isEmpty()) { 
			for(Plugin plugin : listeners) {
				plugin.incomingEvent(e);
			}
		}
	}
}
