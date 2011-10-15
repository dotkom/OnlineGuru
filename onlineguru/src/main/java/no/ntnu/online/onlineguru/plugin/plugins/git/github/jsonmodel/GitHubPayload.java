package no.ntnu.online.onlineguru.plugin.plugins.git.github.jsonmodel;


import no.ntnu.online.onlineguru.plugin.plugins.git.GitPayload;

import java.io.Serializable;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class GitHubPayload extends GitPayload implements Serializable{
    private String before;
    private Repository repository;
    private List<Commit> commits;
    private String after;
    private String ref;
    private String compare;

    public GitHubPayload() {}
    public GitHubPayload(String repositoryUrlWhichIsIdentifier) {
        setRepository(new Repository(repositoryUrlWhichIsIdentifier));
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

    @Override
    public String toString() {
        return "GitHubPayload{" +
                "before='" + before + '\'' +
                ", repository=" + repository +
                ", commits=" + commits +
                ", after='" + after + '\'' +
                ", ref='" + ref + '\'' +
                ", compare='" + compare + '\'' +
                '}';
    }

   @Override
    public String getIdentifier() {
        return repository.getUrl();
    }
}
