package no.ntnu.online.onlineguru.plugin.plugins.flags;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.utils.Wand;

/**
 * @author Håvard Slettvold
 */
public class CommandHandler {

    private Wand wand;
    private FlagsPlugin flagsPlugin;

    public CommandHandler(FlagsPlugin flagsPlugin) {
        this.flagsPlugin = flagsPlugin;
    }

    public void setWand(Wand wand) {
        this.wand = wand;
    }

    public void handleCommand(PrivMsgEvent e) {
        String message = e.getMessage();
        String sender = e.getSender();
        String target = e.getTarget();

        if (message.matches("^" + wand.getMyNick(e.getNetwork()) + " flags")) {

        }

    }



}
