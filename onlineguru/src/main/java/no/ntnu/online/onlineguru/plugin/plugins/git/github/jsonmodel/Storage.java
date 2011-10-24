package no.ntnu.online.onlineguru.plugin.plugins.git.github.jsonmodel;

import java.io.Serializable;

/**
 * @author Roy Sindre Norangshol
 */
public class Storage implements Serializable {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "label='" + label + '\'' +
                '}';
    }
}
