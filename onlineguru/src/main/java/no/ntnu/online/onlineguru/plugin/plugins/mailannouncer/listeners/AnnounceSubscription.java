package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

import no.fictive.irclib.model.network.Network;

import java.util.Objects;

/**
 * @author Håvard Slettvold
 */
public class AnnounceSubscription {

    private Network network;
    private String channel;

    public AnnounceSubscription(Network network, String channel) {
        this.network = network;
        this.channel = channel;
    }

    public Network getNetwork() {
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
