package no.ntnu.online.onlineguru.plugin.plugins.flags.handler;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.ntnu.online.onlineguru.utils.Wand;

/**
 * @author Håvard Slettvold
 */
public class CommandHandler {

    private Wand wand;

    public CommandHandler() {
    }

    public void setWand(Wand wand) {
        this.wand = wand;
    }

    public void handleCommand(PrivMsgEvent e) {


    }

}
