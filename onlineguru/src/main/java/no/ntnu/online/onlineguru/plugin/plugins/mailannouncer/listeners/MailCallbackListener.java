package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.listeners;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.MailCallback;
import no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.model.Mail;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Håvard Slettvold
 */
public class MailCallbackListener {

    private List<AnnounceSubscription> announceSubscriptions = new ArrayList<AnnounceSubscription>();

    public void incomingMail(MailCallback mailCallback, Mail mail) {
        for (AnnounceSubscription announceSubscription : announceSubscriptions) {
            mailCallback.announceToIRC(announceSubscription.getNetwork(), announceSubscription.getChannel(),
                    String.format("[mail][%s] %s - %s",
                    mail.getMailinglist(),
                    mail.getFrom(),
                    mail.getSubject()
            ));
        }
    }

    public boolean deleteSubscription(Network network, String channel) {
        AnnounceSubscription announceSubscription = getSubscription(network, channel);
        if (announceSubscription != null) {
            announceSubscriptions.remove(announceSubscription);
            return true;
        }
        return false;
    }

    public boolean createSubscription(Network network, String channel) {
        AnnounceSubscription announceSubscription = getSubscription(network, channel);
        if (announceSubscription == null) {
            announceSubscription = new AnnounceSubscription(network.getServerAlias(), channel);
            announceSubscriptions.add(announceSubscription);
            return true;
        }
        return false;
    }

    public boolean isSubscribed(Network network, String channel) {
        AnnounceSubscription announceSubscription = getSubscription(network, channel);
        if (announceSubscription == null) {
            return false;
        }
        return true;
    }

    private AnnounceSubscription getSubscription(Network network, String channel) {
        AnnounceSubscription announceSubscription = null;
        for (AnnounceSubscription as : announceSubscriptions) {
            if (as.getNetwork().equalsIgnoreCase(network.getServerAlias()) && as.getChannel().equalsIgnoreCase(channel)) {
                announceSubscription = as;
                break;
            }
        }

        return announceSubscription;
    }

}
