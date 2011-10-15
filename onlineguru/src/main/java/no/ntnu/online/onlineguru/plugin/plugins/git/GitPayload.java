package no.ntnu.online.onlineguru.plugin.plugins.git;

/**
 * @author Roy Sindre Norangshol
 */
public abstract class GitPayload {
    public String getType() {
        return this.getClass().getName();
    }
    public abstract String getIdentifier(); // github repository link / internal git link
}
