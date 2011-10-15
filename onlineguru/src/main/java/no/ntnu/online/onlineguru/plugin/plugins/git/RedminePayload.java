package no.ntnu.online.onlineguru.plugin.plugins.git;

import java.io.Serializable;

/**
 * @author Roy Sindre Norangshol
 */
public class RedminePayload extends GitPayload implements Serializable {
    private String repository;
    private String ref;

    public RedminePayload() {
    }

    public RedminePayload(String repository) {
        this.repository = repository;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return RedminePayload.class.getName();
    }

    @Override
    public String getIdentifier() {
        return repository;
    }

    @Override
    public String toString() {
        return "RedminePayload{" +
                "repository='" + repository + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }
}
