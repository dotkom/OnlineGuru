package no.ntnu.online.onlineguru.plugin.plugins.github.listeners;

import no.ntnu.online.onlineguru.plugin.plugins.github.model.GithubPayload;
import no.ntnu.online.onlineguru.utils.URLShortener;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Vector;

/**
 * @author Håvard Slettvold
 */
public class CallbackListener {

    static Logger logger = Logger.getLogger(CallbackListener.class);

    private final String channel;
    private Wand wand;

    private List<AnnounceSubscription> subscribers = new Vector<AnnounceSubscription>();


    public CallbackListener(Wand wand) {
        this.wand = wand;
        this.channel = "moo";
    }

    public void incomingPayload(GithubPayload githubPayload) {
        System.out.println("received");

        // Branch deleted
        if (githubPayload.isDeleted()) {
            announceDelete(githubPayload);
        }
        // Branch created
        else if (githubPayload.isCreated()) {
            announceCreate(githubPayload);
        }
        // New commits (push)
        else if (!githubPayload.getCommits().isEmpty()) {
            announceCommit(githubPayload);
        }
        // New issue / issue activity?
        else if (githubPayload.getIssue() != null) {
            announceIssue(githubPayload);
        }
        // New pull request / pull request activity?
        else if (githubPayload.getPullRequest() != null) {
            announcePullRequest(githubPayload);
        }
        // Used for debugging in case.
        else {
            logger.debug("Github payload matched none of the criteria.");
        }
    }

    public void announceDelete(GithubPayload githubPayload) {
        // Get the active branch.
        String activeBranch = githubPayload.getRef().split("/")[2];
        String message = String.format("[github][%s] Deleted branch '%s' (%s)",
                githubPayload.getRepository().getName(),
                activeBranch,
                githubPayload.getPusher().getName()
        );

        System.out.println(message);
    }

    public void announceCreate(GithubPayload githubPayload) {
        System.out.println("was created");
    }

    public void announceCommit(GithubPayload githubPayload) {
        // Get the active branch.
        String activeBranch = githubPayload.getRef().split("/")[2];

        // Attempt to shorten compare URL.
        String shortenedURL = URLShortener.bitlyfyLink(githubPayload.getCompare());
        String compareURL = shortenedURL.isEmpty() ? githubPayload.getCompare() : shortenedURL;

        String message = String.format("[scm][%s] %s new commits pushed to %s: %s (%s)",
                githubPayload.getRepository().getName(),
                githubPayload.getCommits().size(),
                activeBranch,
                compareURL,
                githubPayload.getPusher().getName()
        );

        System.out.println(message);
    }

    public void announceIssue(GithubPayload githubPayload) {
        String message = String.format("[github][%s/%s] %s - %s. %s",
                githubPayload.getRepository().getName(),
                githubPayload.getAction(),
                githubPayload.getIssue().getTitle(),
                githubPayload.getIssue().getUser().getLogin(),
                githubPayload.getIssue().getHtmlUrl()
        );

        System.out.println(message);
    }

    public void announcePullRequest(GithubPayload githubPayload) {
        System.out.println("was pull request");
    }

}
