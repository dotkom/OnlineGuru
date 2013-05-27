package no.ntnu.online.onlineguru.plugin.plugins.github.model;


import com.google.gson.annotations.SerializedName;
import no.ntnu.online.onlineguru.plugin.plugins.git.GitPayload;

import java.io.Serializable;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class GithubPayload extends GitPayload implements Serializable{
    private String action;
    @SerializedName("pull_request")
    private PullRequest pullRequest;
    private Issue issue;

    private String before;
    private Repository repository;
    private List<Commit> commits;
    private String after;
    private String ref;
    private String compare;
    private User pusher;

    public GithubPayload() {}

    public GithubPayload(String repositoryUrlWhichIsIdentifier) {
        setRepository(new Repository(repositoryUrlWhichIsIdentifier));
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getCompare() {
        return compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public User getPusher() {
        return pusher;
    }

    public void setPusher(User pusher) {
        this.pusher = pusher;
    }

    @Override
    public String toString() {
        return "GitHubPayload{" +
                "action='" + action + '\'' +
                ", pullRequest=" + pullRequest +
                ", issue=" + issue +
                ", before='" + before + '\'' +
                ", repository=" + repository +
                ", commits=" + commits +
                ", after='" + after + '\'' +
                ", ref='" + ref + '\'' +
                ", compare='" + compare + '\'' +
                ", pusher=" + pusher +
                '}';
    }

    @Override
    public String getIdentifier() {
        return repository.getUrl();
    }
}
