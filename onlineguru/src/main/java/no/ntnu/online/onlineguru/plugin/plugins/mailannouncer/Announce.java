package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data class Announce
 */
public class Announce implements Serializable {
    // <Network, List<Channel>> :-)
    private ConcurrentHashMap<String, List<String>> announceToChannels;
    private String fromEmail;
    private String toEmail;
    private String announceTag;
    private String listId;
    private String subject;

    public Announce(String toEmail, String fromEmail, ConcurrentHashMap<String, List<String>> announceToChannels) {
        this.toEmail = toEmail;
        this.fromEmail = fromEmail;
        this.announceToChannels = announceToChannels;
    }

    public Announce(String announceTag, String toEmail, String fromEmail, ConcurrentHashMap<String, List<String>> announceToChannels) {
        this.announceTag = announceTag;
        this.toEmail = toEmail;
        this.fromEmail = fromEmail;
        this.announceToChannels = announceToChannels;
    }

    public Announce(String announceTag, String toEmail, String fromEmail, ConcurrentHashMap<String, List<String>> announceToChannels , String listId) {
        this.announceToChannels = announceToChannels;
        this.announceTag = announceTag;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.listId = listId;
    }

    public ConcurrentHashMap<String, List<String>> getAnnounceToChannels() {
        return announceToChannels;
    }

    public String getToEmail() {
        return toEmail;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getAnnounceTag() {
        return announceTag;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public void setAnnounceTag(String announceTag) {
        this.announceTag = announceTag;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Announce{" +
                "announceToChannels=" + announceToChannels +
                ", fromEmail='" + fromEmail + '\'' +
                ", toEmail='" + toEmail + '\'' +
                ", announceTag='" + announceTag + '\'' +
                ", listId='" + listId + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
