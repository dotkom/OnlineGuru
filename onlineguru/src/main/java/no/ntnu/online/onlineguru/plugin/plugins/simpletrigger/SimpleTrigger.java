package no.ntnu.online.onlineguru.plugin.plugins.simpletrigger;

import java.util.HashMap;
import java.util.Map;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;

public class SimpleTrigger implements PluginWithDependencies {

    private Map<String, String> triggers = new HashMap<String, String>();
    private SimpleTriggerSettings simpleTriggerSettings;
    private Wand wand;
    private String[] dependencies = new String[]{"FlagsPlugin"};
    private FlagsPlugin flagsPlugin;

    private final static String ADD_KEYWORD = "add trigger";
    private final static String DELETE_KEYWORD = "delete trigger";
    private final Flag CONTROL_FLAG = Flag.a;

    public SimpleTrigger() {
        simpleTriggerSettings = new SimpleTriggerSettings(this);
        simpleTriggerSettings.initiate();
    }

    protected void setTriggers(Map<String, String> triggers) {
        this.triggers = triggers;
    }

    public SimpleTriggerSettings getSettings() {
        return simpleTriggerSettings;
    }

    public String getTrigger(String trigger) {
        return triggers.get(trigger);
    }

    public Map<String, String> getTriggers() {
        return triggers;
    }

    public String addNewTrigger(String trigger, String value) {
        if (!triggers.containsKey(trigger)) {
            triggers.put(trigger, value);
            simpleTriggerSettings.saveConfig(triggers);
            return "Trigger was added.";
        }
        return "That trigger already exists.";
    }

    public void clearTriggers() {
        triggers.clear();
    }

    public String deleteTrigger(String trigger) {
        if (triggers.containsKey(trigger)) {
            triggers.remove(trigger);
            simpleTriggerSettings.saveConfig(triggers);
            return "Trigger was deleted.";
        }
        return "Trigger does not exist.";
    }

    public String getDescription() {
        return "Makes it easy to create triggers with one-liner outputs.";
    }

    public void incomingEvent(Event e) {
        PrivMsgEvent pme = (PrivMsgEvent) e;

        String message = pme.getMessage();
        String sender = pme.getSender();
        String target = pme.getTarget();

        if (message.startsWith(ADD_KEYWORD) && message.length() > ADD_KEYWORD.length() + 1) {
            if (flagsPlugin.hasFlag(pme.getNetwork(), pme.getChannel(), pme.getSender(), CONTROL_FLAG)) {
                String triggerLine = message.substring(ADD_KEYWORD.length() + 1);
                String[] triggerSplit = triggerLine.split("\\s+");

                if (triggerSplit.length >= 2) {
                    String trigger = triggerSplit[0];
                    String value = triggerLine.substring(trigger.length()).trim();
                    wand.sendMessageToTarget(e.getNetwork(), sender, addNewTrigger(trigger, value));
                }
                else {
                    wand.sendMessageToTarget(e.getNetwork(), sender, "Incorrect syntax. Correct syntax: add trigger <trigger> <value>");
                }
            }
            else {
                wand.sendMessageToTarget(e.getNetwork(), sender, "You do not have permission to do that.");
            }
        }
        else if (message.startsWith(DELETE_KEYWORD) && message.length() > DELETE_KEYWORD.length() + 1) {
            if (flagsPlugin.hasFlag(pme.getNetwork(), pme.getChannel(), pme.getSender(), CONTROL_FLAG)) {
                String triggerLine = message.substring(DELETE_KEYWORD.length() + 1);
                String[] triggerSplit = triggerLine.split("\\s+");

                if (triggerSplit.length == 1) {
                    String trigger = triggerSplit[0];
                    wand.sendMessageToTarget(e.getNetwork(), sender, deleteTrigger(trigger));
                }
                else {
                    wand.sendMessageToTarget(e.getNetwork(), sender, "Incorrect syntax. Correct syntax: delete trigger <trigger>");
                }
            }
            else {
                wand.sendMessageToTarget(e.getNetwork(), sender, "You do not have permissions to do that.");
            }
        }
        else {
            String[] messageSplit = message.split("\\s+");
            if (messageSplit.length == 1) {
                if (triggers.containsKey(messageSplit[0])) {
                    wand.sendMessageToTarget(e.getNetwork(), target, triggers.get(messageSplit[0]));
                }
            }
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;

    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) {
            this.flagsPlugin = (FlagsPlugin) plugin;
        }
    }
}
