package no.ntnu.online.onlineguru.plugin.plugins.github.jsonmodel;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roy Sindre Norangshol
 */
public class Repository {
    private String url;
    private String name;
    private String description;
    private int watchers;
    private int forks;
    @SerializedName("private")
    private int privates;
    private User owner;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getPrivates() {
        return privates;
    }

    public void setPrivates(int privates) {
        this.privates = privates;
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
                ", privates=" + privates +
                ", owner=" + owner +
                '}';
    }
}
