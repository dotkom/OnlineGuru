package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.model;

/**
 * @author Håvard Slettvold
 */
public class Mail {

    private String to;
    private String from;
    private String subject;
    private String mailinglist;
    private String mailinglistAlias;

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getMailinglist() {
        return mailinglist;
    }

    public void setMailinglistAlias(String alias) {
        this.mailinglistAlias = alias;
    }

    public String getLookupValue() {
        if (mailinglistAlias != null) return mailinglistAlias;
        else if (mailinglist != null) return mailinglist;
        else return to;
    }
}
