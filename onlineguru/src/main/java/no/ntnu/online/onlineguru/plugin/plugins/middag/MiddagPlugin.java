package no.ntnu.online.onlineguru.plugin.plugins.middag;

import java.util.regex.*;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.help.HelpPlugin;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;

/**
 * @author Håvard Slettvold
 */

public class MiddagPlugin implements PluginWithDependencies {

    private Wand wand;


    private DateTimeFormatter day = DateTimeFormat.forPattern("e");
    private String[] strDays = {"", "mandag", "tirsdag", "onsdag", "torsdag", "fredag", "lørdag", "søndag",};

    private DateTime cache;
    private PrivMsgEvent eventRunAfterUpdate = null;
    private DateTime lastPublicTrigger = new DateTime().withYear(2012);

    private String hangaren = "";
    private String realfag = "";
    private int finishedUpdating = 0;
    private final int totalMenues = 2;

    static Logger logger = Logger.getLogger(MiddagPlugin.class);

    private final Pattern triggerPattern = Pattern.compile("^!middag ?(update)?$", Pattern.CASE_INSENSITIVE);

    public MiddagPlugin() {
        updateMenu();
    }

    public void setRealfag(String realfag) {
        this.realfag = realfag;
        finishedUpdating++;
        if (finishedUpdating == totalMenues) {
            incomingEvent(eventRunAfterUpdate);
        }
    }

    public void setHangaren(String hangaren) {
        this.hangaren = hangaren;
        finishedUpdating++;
        if (finishedUpdating == totalMenues) {
            incomingEvent(eventRunAfterUpdate);
        }
    }

    /*
     * Metoder som arves fra PluginWithDependencies
     */

    public String getDescription() {
        return "This Plugin fetches the dinner-menues from Realfagskantina and Hangaren at NTNU Gløshaugen.";
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG:
                handlePrivMsgEvent((PrivMsgEvent) e);
                break;
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

    public String[] getDependencies() {
        return new String[]{"HelpPlugin",};
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof HelpPlugin) {
            HelpPlugin help = (HelpPlugin) plugin;
            help.addHelp(
                    "!middag",
                    Flag.ANYONE,
                    "!middag [update] - Display dinner menu for the canteens Hangaren and Realfag.",
                    "Optional argument [update] tries to update the cached menu."
            );
        }
    }

	/*
     * Triggerprosessering
	 */

    private void handlePrivMsgEvent(PrivMsgEvent pme) {
        if (finishedUpdating == totalMenues) {
            Matcher triggerMatcher = triggerPattern.matcher(pme.getMessage());

            if (triggerMatcher.find()) {
                int duration = new Duration(cache, new DateTime()).toStandardSeconds().getSeconds();

                if (triggerMatcher.group(1) != null) {
                    if (triggerMatcher.group(1).equalsIgnoreCase("update")) {
                        // Only allow updates if cache is 15 minutes old. Spam prevention.
                        if (duration < 900) {
                            sendPrivateMessage(pme, "Current data is " + (duration / 60) + " minutes old. Update not allowed before 15 minutes.");
                        }
                        else {
                            updateMenu();
                        }
                    }
                }
                else {
                    // Update automatically if cache is 1 hour old.
                    if (duration > 3600) {
                        eventRunAfterUpdate = pme;
                        updateMenu();
                    }
                    else {
                        // If they are empty or equal, that means there's no menu,
                        // or at least the message can be displayed once, instead of twice.
                        if (!hangaren.isEmpty() && !realfag.equals(hangaren)) {
                            sendChannelMessage(pme, "Hangaren: " + hangaren);
                            sendChannelMessage(pme, "Realfag: " + realfag);
                        }
                        else {
                            sendChannelMessage(pme, "Hangaren & Realfag: " + realfag);
                        }
                    }
                }
            }
        }
    }

    private void sendPrivateMessage(PrivMsgEvent pme, String message) {
        wand.sendMessageToTarget(pme.getNetwork(), pme.getSender(), "[middag] " + message);
    }

    private void sendChannelMessage(PrivMsgEvent pme, String message) {
        Network network = pme.getNetwork();
        String target = pme.getTarget();
        String sender = pme.getSender();

        // Check how long since last public triggering of !middag
        // Notice if it wasn't very long ago (5 minutes)
        int duration = new Duration(lastPublicTrigger, new DateTime()).toStandardSeconds().getSeconds();

        if (duration > 0 && duration < 300) {
            wand.sendMessageToTarget(network, sender, "[middag] " + message);
        }
        else {
            wand.sendMessageToTarget(network, target, "[middag] " + message);
            lastPublicTrigger = new DateTime();
        }
    }

    private String getStrDayOfWeek() {
        return strDays[Integer.parseInt(day.print(new DateTime()))];
    }

    private void updateMenu() {
        if (eventRunAfterUpdate != null) {
            eventRunAfterUpdate = null;
        }
        cache = new DateTime();
        finishedUpdating = 0;

        String currentDay = getStrDayOfWeek();

        if (currentDay.equals("lørdag") || currentDay.equals("søndag")) {
            setHangaren("No serving in the weekends");
            setRealfag("No serving in the weekends");

            finishedUpdating = totalMenues;
        }
        else {
            new UpdateMenu(this, "HANGAREN");
            new UpdateMenu(this, "REALFAG");
        }
    }

}