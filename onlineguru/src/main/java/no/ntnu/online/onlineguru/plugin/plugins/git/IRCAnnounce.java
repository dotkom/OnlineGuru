package no.ntnu.online.onlineguru.plugin.plugins.git;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:54
 */
public class IRCAnnounce implements Serializable {


    public static final String LIST_ANNOUNCE_FORMAT = "[%s/%s] %s";
    private ConcurrentHashMap<String, List<ChannelAnnounce>> announceToChannels;
    private GitPayload gitPayload;

    public IRCAnnounce(GitPayload gitPayload, ConcurrentHashMap<String, List<ChannelAnnounce>> announceToChannels) {
        this.gitPayload = gitPayload;
        this.announceToChannels = announceToChannels;
    }

    public ConcurrentHashMap<String, List<ChannelAnnounce>> getAnnounceToChannels() {
        return announceToChannels;
    }

    public void setAnnounceToChannels(ConcurrentHashMap<String, List<ChannelAnnounce>> announceToChannels) {
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

    @Override
    public String toString() {
        return "IRCAnnounce{" +
                "announceToChannels=" + announceToChannels +
                ", gitPayload=" + gitPayload +
                '}';
    }

    public String listIrcAnnounces() {
        String prettiFyAnnounceChannels = "";
        for (Map.Entry<String, List<ChannelAnnounce>> networkWithChannels : announceToChannels.entrySet()) {
            String channelWithAnnounceLevel = "";

            for (ChannelAnnounce channelAnnounce : networkWithChannels.getValue()) {
                channelWithAnnounceLevel += String.format("%s(%s), ", channelAnnounce.getChannel(), channelAnnounce.getVerboseLevel());
            }
            channelWithAnnounceLevel = channelWithAnnounceLevel.substring(0, channelWithAnnounceLevel.length() - 2); // removes last whitespace + ,

            prettiFyAnnounceChannels += String.format("%s {%s}, ", networkWithChannels.getKey(), channelWithAnnounceLevel);
        }
        prettiFyAnnounceChannels = prettiFyAnnounceChannels.substring(0, prettiFyAnnounceChannels.length() - 2); // removes last whitespace + ,

        return String.format(LIST_ANNOUNCE_FORMAT, gitPayload.getType().substring(gitPayload.getType().lastIndexOf(".")+1, gitPayload.getType().length()), gitPayload.getIdentifier(), prettiFyAnnounceChannels);
    }
}
