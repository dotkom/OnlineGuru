package no.ntnu.online.onlineguru.plugin.plugins.github.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Roy Sindre Norangshol
 */
public class Issue implements Serializable{
    private int number;
    private String state;
    private String title;
    private String body;
    @SerializedName("html_url")
    private String htmlUrl;
    private User user;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "number=" + number +
                ", state='" + state + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", user=" + user +
                '}';
    }
}
