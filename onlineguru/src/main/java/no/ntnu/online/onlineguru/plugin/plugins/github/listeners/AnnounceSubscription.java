package no.ntnu.online.onlineguru.plugin.plugins.github.listeners;

/**
 * @author Håvard Slettvold
 */
public class AnnounceSubscription {

    private String network;
    private String channel;

    private boolean wants_issues = false;
    private boolean wants_commits = false;
    private boolean wants_pull_requests = false;
    private boolean wants_branches = false;

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

    public boolean wants_branches() {
        return wants_branches;
    }

    public boolean setWants_branches(boolean wants_branches) {
        if (this.wants_branches ^ wants_branches) {
            this.wants_branches = wants_branches;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean wants_commits() {
        return wants_commits;
    }

    public boolean setWants_commits(boolean wants_commits) {
        if (this.wants_commits ^ wants_commits) {
            this.wants_commits = wants_commits;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean wants_issues() {
        return wants_issues;
    }

    public boolean setWants_issues(boolean wants_issues) {
        if (this.wants_issues ^ wants_issues) {
            this.wants_issues = wants_issues;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean wants_pull_requests() {
        return wants_pull_requests;
    }

    public boolean setWants_pull_requests(boolean wants_pull_requests) {
        if (this.wants_pull_requests ^ wants_pull_requests) {
            this.wants_pull_requests = wants_pull_requests;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        String output = "";
        if (wants_branches) output += "branches";
        if (wants_commits) output += " commits";
        if (wants_issues) output += " issues";
        if (wants_pull_requests) output += " pull_requests";

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
