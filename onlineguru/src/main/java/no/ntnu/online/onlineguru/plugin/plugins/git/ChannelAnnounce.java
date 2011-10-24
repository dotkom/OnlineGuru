package no.ntnu.online.onlineguru.plugin.plugins.git;

/**
 * @author Roy Sindre Norangshol
 */
public class ChannelAnnounce {
    private String channel;
    private VerboseLevel verboseLevel;

    public ChannelAnnounce(String channel, VerboseLevel verboseLevel) {
        this.channel = channel;
        this.verboseLevel = verboseLevel;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public VerboseLevel getVerboseLevel() {
        return verboseLevel;
    }

    public void setVerboseLevel(VerboseLevel verboseLevel) {
        this.verboseLevel = verboseLevel;
    }

    @Override
    public String toString() {
        return "ChannelAnnounce{" +
                "channel='" + channel + '\'' +
                ", verboseLevel=" + verboseLevel +
                '}';
    }
}
