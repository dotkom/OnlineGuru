package no.ntnu.online.onlineguru.plugin.plugins.peak;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.event.container.command.NumericEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
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
            enabled = true;
            verifyMapIntegrity(peaks);
        } catch (IOException e) {
            e.printStackTrace();
            enabled = false;
        }
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
			help.addTrigger("!peak", Flag.ANYONE);
			help.addHelp("!peak", "!peak [channel] - Display the peak user count for the current channel, or specify a [channel].", Flag.ANYONE);
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

    protected String channelIdentifier(Network network, Channel channel) {
        return String.format("%s@%s", channel.getChannelname(), network.getServerAlias());
    }

    protected String channelIdentifier(Network network, String channel) {
        return String.format("%s@%s", channel, network.getServerAlias());
    }

    /**
     * Checks if the channel already has a count, and adds it if it doesn't.
     *
     * @param e {@link JoinEvent} to be investigated.
     */
    private void handleJoinEvent(JoinEvent e) {
        String count = peaks.get(channelIdentifier(e.getNetwork(), e.getChannel()));
        if (count == null) {
            peaks.put(e.getChannel(),""+0);
        }
    }

    private void handleNumericEvent(NumericEvent ne) {
        // Numeric 366 is end of names list. It appears automatically when you join channels, or if triggered.
        // irclib uses the names list to get the number of people in a channel, so when end of names comes,
        // we should get an accurate number.
        if (ne.getNumeric() == 366) {
            Channel channel = ne.getNetwork().getChannel(ne.getParamaters().get(1));

            if (updatePeakForChannel(ne.getNetwork(), channel)) {
                try {
                    SimpleIO.saveConfig(database_file, peaks);
                } catch (IOException e) {
                    logger.error("Failed to store peak after update to %s.", e.getCause());
                }
            }
        }
    }

    protected boolean updatePeakForChannel(Network network, Channel channel) {
        int count = -1;

        try {
            count = Integer.parseInt(peaks.get(channelIdentifier(network, channel)));
        } catch (NumberFormatException nfe) {
            logger.error("Malformed data for Peak plugin stored in peaks. Deactivating plugin.", nfe.getCause());
            this.enabled = false;
        }

        int numberOnChannel = channel.getNicks().size();

        if (count < numberOnChannel) {
            peaks.put(channel.getChannelname(), ""+numberOnChannel);
            return true;
        }

        return false;
    }

    private void handlePrivMsgEvent(PrivMsgEvent e) {
        String[] message = e.getMessage().split(" ");
        String target = e.getTarget();
        String countTarget;
        String count;

        if (message[0].equals("!peak")) {
            if (message.length == 1) {
                if (e.isChannelMessage()) {
                    count = peaks.get(channelIdentifier(e.getNetwork(), target));
                    wand.sendMessageToTarget(e.getNetwork(), target, "Peak usercount for "+target+": "+count);
                }
                else {
                    wand.sendMessageToTarget(e.getNetwork(), target, "[Error] You need to specify a channel to check.");
                }
            }
            else if (message.length == 2) {
                if (message[1].startsWith("#")) {
                    count = peaks.get(channelIdentifier(e.getNetwork(), message[1]));
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
