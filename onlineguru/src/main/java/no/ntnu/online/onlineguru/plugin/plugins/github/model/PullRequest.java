package no.ntnu.online.onlineguru.plugin.plugins.github.model;

import com.google.gson.annotations.SerializedName;
import no.ntnu.online.onlineguru.plugin.plugins.git.github.jsonmodel.Storage;

import java.io.Serializable;

/**
 * @author Roy Sindre Norangshol
 */
public class PullRequest implements Serializable {
    private int number;
    private int commits;
    private String state;
    private String title;
    private String body;
    private User user;
    private Storage head;
    private Storage base;
    @SerializedName("html_url")
    private String htmlUrl;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCommits() {
        return commits;
    }

    public void setCommits(int commits) {
        this.commits = commits;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Storage getHead() {
        return head;
    }

    public void setHead(Storage head) {
        this.head = head;
    }

    public Storage getBase() {
        return base;
    }

    public void setBase(Storage base) {
        this.base = base;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    @Override
    public String toString() {
        return "PullRequest{" +
                "number=" + number +
                ", commits=" + commits +
                ", state='" + state + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user +
                ", head=" + head +
                ", base=" + base +
                ", htmlUrl='" + htmlUrl + '\'' +
                '}';
    }
}
