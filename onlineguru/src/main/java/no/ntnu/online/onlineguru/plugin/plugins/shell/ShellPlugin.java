package no.ntnu.online.onlineguru.plugin.plugins.shell;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.Arrays;


public class ShellPlugin implements Plugin, MessageObserver {
    private static final String DESCRIPTION = "Plugin which allows to run shell commands";
    private static final String TRIGGER = "!shell";
    public static final String ADMIN_CHANNEL = "#online.dotkom";
    private Wand wand;
    private MessageObserver messageObserver;
    static Logger logger = Logger.getLogger(ShellPlugin.class);

    public ShellPlugin() {
        this.messageObserver = this;
    }
    public ShellPlugin(Wand wand,MessageObserver messageObserver) {
        this.wand = wand;
        this.messageObserver = messageObserver;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                PrivMsgEvent event = (PrivMsgEvent) e;
                if (event.isChannelMessage() && event.getChannel().equalsIgnoreCase(ADMIN_CHANNEL)) {
                    runShellCommand(event.getNetwork(), event.getChannel(), event.getMessage().substring(TRIGGER.length(), event.getMessage().length()));
                }
            }
        }
    }

    private int runShellCommand(Network network, String target, String shellCommand) {
        shellCommand = shellCommand.trim();
        logger.info(shellCommand);
        try {
            String osName = System.getProperty("os.name");
            String[] cmd = new String[3];
            if (osName.equals("Windows NT")) {
                cmd[0] = "cmd.exe";
                cmd[1] = "/C";
                cmd[2] = shellCommand;
            } else if (osName.equals("Windows 95")) {
                cmd[0] = "command.com";
                cmd[1] = "/C";
                cmd[2] = shellCommand;
            } else
                cmd = shellCommand.split("\\s+");



            Runtime rt = Runtime.getRuntime();
            logger.info(Arrays.toString(cmd));

            Process proc = rt.exec(cmd);

            // any error message?
            StreamGobbler errorGobbler = new
                    StreamGobbler(proc.getErrorStream(), "ERROR", messageObserver, network, target);

            // any output?
            StreamGobbler outputGobbler = new
                    StreamGobbler(proc.getInputStream(), "OUTPUT", messageObserver, network, target);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();


            // any error???
            int exitVal = proc.waitFor();
            return exitVal;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return -1;
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }


    public void deliverMessage(Network network, String target, String message) {
        wand.sendMessageToTarget(network, target, message);
    }
}
