package no.ntnu.online.onlineguru.plugin.plugins.github.listeners;

import no.ntnu.online.onlineguru.plugin.plugins.github.GithubCallback;
import no.ntnu.online.onlineguru.plugin.plugins.github.model.GithubPayload;
import no.ntnu.online.onlineguru.utils.URLShortener;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Håvard Slettvold
 */
public class CallbackListener {

    static Logger logger = Logger.getLogger(CallbackListener.class);

    private List<AnnounceSubscription> announceSubscriptions = new ArrayList<AnnounceSubscription>();

    public void incomingPayload(GithubCallback gc, GithubPayload githubPayload) {
        String network = "";
        String channel = "";
        String output = "";

        // Branch deleted
        if (githubPayload.isDeleted()) {
            output = announceDelete(githubPayload);
            for (AnnounceSubscription as : announceSubscriptions) {
                if (as.wants_branches()) {
                    network = as.getNetwork();
                    channel = as.getChannel();
                }
            }
        }
        // Branch created
        else if (githubPayload.isCreated()) {
            output = announceCreate(githubPayload);
            for (AnnounceSubscription as : announceSubscriptions) {
                if (as.wants_branches()) {
                    network = as.getNetwork();
                    channel = as.getChannel();
                }
            }
        }
        // New commits (push)
        else if (githubPayload.getCommits() != null && !githubPayload.getCommits().isEmpty()) {
            output = announceCommit(githubPayload);
            for (AnnounceSubscription as : announceSubscriptions) {
                if (as.wants_commits()) {
                    network = as.getNetwork();
                    channel = as.getChannel();
                }
            }
        }
        // New issue / issue activity?
        else if (githubPayload.getIssue() != null) {
            output = announceIssue(githubPayload);
            for (AnnounceSubscription as : announceSubscriptions) {
                if (as.wants_issues()) {
                    network = as.getNetwork();
                    channel = as.getChannel();
                }
            }
        }
        // New pull request / pull request activity?
        else if (githubPayload.getPullRequest() != null) {
            output = announcePullRequest(githubPayload);
            for (AnnounceSubscription as : announceSubscriptions) {
                if (as.wants_pull_requests()) {
                    network = as.getNetwork();
                    channel = as.getChannel();
                }
            }
        }
        // Used for debugging in case.
        else {
            logger.debug("Github payload matched none of the criteria.");
            logger.debug(githubPayload.toString());
        }

        if (!network.isEmpty() && !channel.isEmpty()) {
            gc.announceToIRC(network, channel, output);
        }
    }

    protected String announceDelete(GithubPayload githubPayload) {
        // Get the active branch.
        String activeBranch = githubPayload.getRef().split("/")[2];
        String message = String.format("[github][%s] Deleted branch '%s' (%s)",
                githubPayload.getRepository().getName(),
                activeBranch,
                githubPayload.getPusher().getName()
        );

        return message;
    }

    protected String announceCreate(GithubPayload githubPayload) {
        // Get the active branch.
        String activeBranch = githubPayload.getRef().split("/")[2];
        String message = String.format("[github][%s] Created branch '%s' (%s)",
                githubPayload.getRepository().getName(),
                activeBranch,
                githubPayload.getPusher().getName()
        );

        return message;
    }

    protected String announceCommit(GithubPayload githubPayload) {
        // Get the active branch.
        String activeBranch = githubPayload.getRef().split("/")[2];

        // Attempt to shorten compare URL.
        String shortenedURL = URLShortener.bitlyfyLink(githubPayload.getCompare());
        String compareURL = shortenedURL.isEmpty() ? githubPayload.getCompare() : shortenedURL;

        String message = String.format("[github][%s/%s] %s new commits - %s (%s)",
                githubPayload.getRepository().getName(),
                activeBranch,
                githubPayload.getCommits().size(),
                compareURL,
                githubPayload.getPusher().getName()
        );

        return message;
    }

    protected String announceIssue(GithubPayload githubPayload) {
        // Attempt to shorten compare URL.
        String shortenedURL = URLShortener.bitlyfyLink(githubPayload.getIssue().getHtmlUrl());
        String issueURL = shortenedURL.isEmpty() ? githubPayload.getIssue().getHtmlUrl() : shortenedURL;

        String message = String.format("[github][%s][issue/%s] %s - %s (%s)",
                githubPayload.getRepository().getName(),
                githubPayload.getAction(),
                githubPayload.getIssue().getTitle(),
                issueURL,
                githubPayload.getIssue().getUser().getLogin()
        );

        return message;
    }

    protected String announcePullRequest(GithubPayload githubPayload) {
        // Attempt to shorten compare URL.
        String shortenedURL = URLShortener.bitlyfyLink(githubPayload.getPullRequest().getHtmlUrl());
        String issueURL = shortenedURL.isEmpty() ? githubPayload.getPullRequest().getHtmlUrl() : shortenedURL;

        String message = String.format("[github][%s][pull request/%s] %s - %s (%s)",
                githubPayload.getRepository().getName(),
                githubPayload.getAction(),
                githubPayload.getPullRequest().getTitle(),
                issueURL,
                githubPayload.getPullRequest().getUser().getLogin()
        );

        return message;
    }

    public AnnounceSubscription getOrCreateSubscription(String network, String channel) {
        AnnounceSubscription announceSubscription = null;
        for (AnnounceSubscription as : announceSubscriptions) {
            if (as.getNetwork().equals(network) && as.getChannel().equalsIgnoreCase(channel)) {
                announceSubscription = as;
                break;
            }
        }

        if (announceSubscription == null) {
            announceSubscription = new AnnounceSubscription(network, channel);
            announceSubscriptions.add(announceSubscription);
        }

        return announceSubscription;
    }

}
