package no.ntnu.online.onlineguru.plugin.plugins.peak;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.NumericEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author melwil
 */

public class Peak implements PluginWithDependencies {

    private Wand wand;
    private Help help;

    static Logger logger = Logger.getLogger(Peak.class);

    private final String database_folder = "database/";
	private final String database_file = database_folder + "peak.db";

    private Map<String, String> peaks = new HashMap<String, String>();
    private boolean enabled;

    public Peak() {
        initiate();
    }

    private void initiate() {
        try {
            SimpleIO.createFolder(database_folder);
            SimpleIO.createFile(database_file);
            peaks = SimpleIO.loadConfig(database_file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        enabled = true;
        verifyMapIntegrity(peaks);
    }

    /**
     * This method verifies that the database loaded fills the requirement
     * Map<String, Integer>.
     *
     * @param peaks Map to be checked
     */
    private void verifyMapIntegrity(Map<String, String> peaks) {
        for (String key : peaks.keySet()) {
            try {
                Integer.parseInt(peaks.get(key));
            } catch (NumberFormatException e) {
                logger.error("Malformed database for Peak plugin. Deactivating plugin.");
                this.enabled = false;
            }
        }
    }

    /*
     * Metoder som arves fra PluginWithDependencies
     */

    public String[] getDependencies() {
        return new String[] {"Help", };
    }

    public void loadDependency(Plugin plugin) {
		if (plugin instanceof Help) {
			this.help = (Help)plugin;
			help.addPublicTrigger("!peak");
			help.addPublicHelp("!peak", "!peak [channel] - Display the peak user count for the current channel, or specify a [channel].");
		}
	}

    public String getDescription() {
        return "Keeps track of user counts on channels that OnlineGuru attends.";
    }

    public void incomingEvent(Event e) {
        if (enabled) {
            switch (e.getEventType()) {
                case JOIN:
                    handleJoinEvent((JoinEvent)e);
                    break;
                case NUMERIC:
                    handleNumericEvent((NumericEvent)e);
                    break;
                case PRIVMSG:
                    handlePrivMsgEvent((PrivMsgEvent)e);
                    break;
            }
        }
    }

    /**
     * Checks if the channel already has a count, and adds it if it doesn't.
     *
     * @param e {@link JoinEvent} to be investigated.
     */
    private void handleJoinEvent(JoinEvent e) {
        String count = peaks.get(e.getChannel());
        if (count == null) {
            peaks.put(e.getChannel(),""+0);
        }
    }

    private void handleNumericEvent(NumericEvent e) {
        if (e.getNumeric() == 366) {
            int count = -1;

            String channel = e.getParamaters().get(1);

            try {
                count = Integer.parseInt(peaks.get(channel));
            } catch (NumberFormatException ex) {
                logger.error("Malformed data for Peak plugin stored in peaks. Deactivating plugin.");
                this.enabled = false;
            }

            int numberOnChannel = e.getNetwork().getChannel(channel).getNicks().size();
            if (count < numberOnChannel) {
                peaks.put(channel, ""+numberOnChannel);
                try {
                    SimpleIO.saveConfig(database_file, peaks);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void handlePrivMsgEvent(PrivMsgEvent e) {
        String[] message = e.getMessage().split(" ");
        String target = e.getTarget();
        String countTarget;
        String count;

        if (message[0].equals("!peak")) {
            if (message.length == 1) {
                if (e.isChannelMessage()) {
                    count = peaks.get(target);
                    wand.sendMessageToTarget(e.getNetwork(), target, "Peak usercount for "+target+": "+peaks.get(target));
                }
                else {
                    wand.sendMessageToTarget(e.getNetwork(), target, "[Error] You need to specify a channel to check.");
                }
            }
            else if (message.length == 2) {
                if (message[1].startsWith("#")) {
                    count = peaks.get(message[1]);
                    countTarget = message[1];
                    if (count != null) {
                        wand.sendMessageToTarget(e.getNetwork(), target, "Peak usercount for "+countTarget+": "+count);
                    }
                    else {
                        wand.sendMessageToTarget(e.getNetwork(), target, "[Error] No such channel registered; '"+countTarget+"'.");
                    }
                }
                if (message[1].equals("enable")) {
                    enabled = true;
                    verifyMapIntegrity(peaks);
                    if (enabled) {
                        wand.sendMessageToTarget(e.getNetwork(), target, "Successfully enabled Peak.");
                    }
                    else {
                        wand.sendMessageToTarget(e.getNetwork(), target, "[Error] Failed to enable Peak. Malformed databse.");
                    }
                }
            }
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.JOIN);
        eventDistributor.addListener(this, EventType.NUMERIC);
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
