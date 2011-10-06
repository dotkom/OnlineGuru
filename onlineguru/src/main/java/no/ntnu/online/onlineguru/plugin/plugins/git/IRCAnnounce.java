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

    public IRCAnnounce(GitPayload gitPayload, ConcurrentHashMap<String, List<String>> announceToChannels) {
        this.gitPayload = gitPayload;
        this.announceToChannels = announceToChannels;
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
}
