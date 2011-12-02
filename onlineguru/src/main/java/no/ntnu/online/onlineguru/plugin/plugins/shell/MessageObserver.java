package no.ntnu.online.onlineguru.plugin.plugins.shell;

import no.fictive.irclib.model.network.Network;

public interface MessageObserver {
    public void deliverMessage(Network network, String target, String message);
}
