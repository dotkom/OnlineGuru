package no.ntnu.online.onlineguru.plugin.plugins.seen;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.history.HistoryPlugin;
import no.ntnu.online.onlineguru.utils.Wand;


public class SeenPlugin implements PluginWithDependencies {
    private HistoryPlugin historyPlugin;
    private static final String DESCRIPTION = "Find the last activity of persons";
    private Wand wand;
    private static final String TRIGGER = "!seen";

    @Override
    public String[] getDependencies() {
        return new String[]{"HistoryPlugin"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof HistoryPlugin) {
            historyPlugin = (HistoryPlugin) plugin;
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
                     handleSeenQuery(privMsgEvent.getMessage().split("\\s+")[0]);
                }
                break;
        }
    }

    private void handleSeenQuery(String nick) {
        // implement
    }

    private boolean isMessageForPlugin(PrivMsgEvent privMsgEvent) {
        return privMsgEvent.isPrivateMessage() && privMsgEvent.getMessage().startsWith(TRIGGER);
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
