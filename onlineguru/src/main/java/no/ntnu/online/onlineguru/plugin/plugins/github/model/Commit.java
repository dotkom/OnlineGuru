package no.ntnu.online.onlineguru.plugin.plugins.github.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class Commit implements Serializable {
    private String id;
    private String url;
    private User author;
    private String message;
    private String timestamp; // fix jodatime

    private List<String> added;
    private List<String> removed;
    private List<String> modified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getAdded() {
        return added;
    }

    public void setAdded(List<String> added) {
        this.added = added;
    }

    public List<String> getRemoved() {
        return removed;
    }

    public void setRemoved(List<String> removed) {
        this.removed = removed;
    }

    public List<String> getModified() {
        return modified;
    }

    public void setModified(List<String> modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", author=" + author +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", added=" + added +
                ", removed=" + removed +
                ", modified=" + modified +
                '}';
    }
}
