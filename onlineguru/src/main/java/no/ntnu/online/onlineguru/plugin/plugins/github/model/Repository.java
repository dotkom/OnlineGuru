package no.ntnu.online.onlineguru.plugin.plugins.github.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Roy Sindre Norangshol
 */
public class Repository implements Serializable {
    private String url;
    @SerializedName("html_url")
    private String htmlUrl;
    private String name;
    private String description;
    private int watchers;
    private int forks;

    private User owner;

    public Repository() {}

    public Repository(String repositoryUrlWhichIsIdentifier) {
        this.url = repositoryUrlWhichIsIdentifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", watchers=" + watchers +
                ", forks=" + forks +
                ", owner=" + owner +
                '}';
    }
}
