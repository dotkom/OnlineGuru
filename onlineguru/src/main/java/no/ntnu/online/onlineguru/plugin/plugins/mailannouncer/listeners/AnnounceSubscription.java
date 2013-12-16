package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

/**
 * @author Håvard Slettvold
 */
public class AnnounceSubscription {

    private String network;
    private String channel;

    public AnnounceSubscription() {
    }

    public AnnounceSubscription(String network, String channel) {
        this.network = network;
        this.channel = channel;
    }

    public String getNetwork() {
        return network;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AnnounceSubscription)) return false;
        AnnounceSubscription announceSubscription = (AnnounceSubscription)o;

        return this.network.equals(announceSubscription.getNetwork()) &&
               this.channel.equals(announceSubscription.getChannel());
    }

}
