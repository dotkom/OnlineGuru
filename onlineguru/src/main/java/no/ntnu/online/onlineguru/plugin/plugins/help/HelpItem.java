package no.ntnu.online.onlineguru.plugin.plugins.help;

import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;

/**
 * @author Håvard Slettvold
 */
public class HelpItem {

    private String trigger;
    private String[] helpText;
    private Flag flagRequired;

    public HelpItem(String trigger, Flag flagRequired, String[] helpText) {
        this.trigger = trigger;
        this.helpText = helpText;
        this.flagRequired = flagRequired;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String[] getHelpText() {
        return helpText;
    }

    public void setHelpText(String[] helpText) {
        this.helpText = helpText;
    }

    public Flag getFlagRequired() {
        return flagRequired;
    }

    public void setFlagRequired(Flag flagRequired) {
        this.flagRequired = flagRequired;
    }

    /*
     * If two help items have the same trigger, they should be considered equal.
     * This distinction is needed when considering ArrayList.contains(..), since it uses the
     * equals method to determine if any object in the list matches.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof HelpItem) {
            return this.trigger.equals(((HelpItem)o).getTrigger());
        }
        else {
            return false;
        }
    }


}
