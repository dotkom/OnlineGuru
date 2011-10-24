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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelAnnounce)) return false;

        ChannelAnnounce that = (ChannelAnnounce) o;

        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        if (verboseLevel != that.verboseLevel) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = channel != null ? channel.hashCode() : 0;
        result = 31 * result + (verboseLevel != null ? verboseLevel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChannelAnnounce{" +
                "channel='" + channel + '\'' +
                ", verboseLevel=" + verboseLevel +
                '}';
    }
}
