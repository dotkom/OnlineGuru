package no.ntnu.online.onlineguru.plugin.plugins.git;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:54
 */
public class IRCAnnounce implements Serializable {


    private ConcurrentHashMap<String, List<String>> announceToChannels;
    private GitPayload gitPayload;
    private VerboseLevel announceLevel;

    public IRCAnnounce(GitPayload gitPayload, ConcurrentHashMap<String, List<String>> announceToChannels, VerboseLevel announceLevel) {
        this.gitPayload = gitPayload;
        this.announceToChannels = announceToChannels;
        this.announceLevel = announceLevel;
    }

    public ConcurrentHashMap<String, List<String>> getAnnounceToChannels() {
        return announceToChannels;
    }

    public void setAnnounceToChannels(ConcurrentHashMap<String, List<String>> announceToChannels) {
        this.announceToChannels = announceToChannels;
    }

    public GitPayload getGitPayload() {
        return gitPayload;
    }

    public void setGitPayload(GitPayload gitPayload) {
        this.gitPayload = gitPayload;
    }

    public VerboseLevel getAnnounceLevel() {
        return announceLevel;
    }

    public void setAnnounceLevel(VerboseLevel announceLevel) {
        this.announceLevel = announceLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IRCAnnounce that = (IRCAnnounce) o;

        if (!gitPayload.equals(that.getGitPayload())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return gitPayload.getType().hashCode();
    }

    @Override
    public String toString() {
        return "IRCAnnounce{" +
                "announceToChannels=" + announceToChannels +
                ", gitPayload=" + gitPayload +
                ", announceLevel=" + announceLevel +
                '}';
    }
}
