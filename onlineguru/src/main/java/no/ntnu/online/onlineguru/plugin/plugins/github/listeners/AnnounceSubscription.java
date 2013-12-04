package no.ntnu.online.onlineguru.plugin.plugins.github.listeners;

/**
 * @author Håvard Slettvold
 */
public class AnnounceSubscription {

    private String network;
    private String channel;

    private boolean wantsIssues = false;
    private boolean wantsCommits = false;
    private boolean wantsPullRequests = false;
    private boolean wantsBranches = false;

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

    public boolean wantsBranches() {
        return wantsBranches;
    }

    public boolean setWantsBranches(boolean wants_branches) {
        if (this.wantsBranches ^ wants_branches) {
            this.wantsBranches = wants_branches;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean wantsCommits() {
        return wantsCommits;
    }

    public boolean setWantsCommits(boolean wants_commits) {
        if (this.wantsCommits ^ wants_commits) {
            this.wantsCommits = wants_commits;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean wantsIssues() {
        return wantsIssues;
    }

    public boolean setWantsIssues(boolean wants_issues) {
        if (this.wantsIssues ^ wants_issues) {
            this.wantsIssues = wants_issues;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean wantsPullRequests() {
        return wantsPullRequests;
    }

    public boolean setWantsPullRequests(boolean wants_pull_requests) {
        if (this.wantsPullRequests ^ wants_pull_requests) {
            this.wantsPullRequests = wants_pull_requests;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        String output = "";
        if (wantsBranches) output += "branches";
        if (wantsCommits) output += " commits";
        if (wantsIssues) output += " issues";
        if (wantsPullRequests) output += " pull_requests";

        if (output.isEmpty()) {
            output += "none";
        }
        else {
            output = output.trim();
            output = output.replaceAll(" ", ", ");
            output = output.replace('_', ' ');
        }

        return output;
    }
}
