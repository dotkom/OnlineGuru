package no.ntnu.online.onlineguru.plugin.plugins.git;

/**
 * @author Roy Sindre Norangshol
 */
public class RedminePayload implements GitPayload {
    private String repository;
    private String ref;

    public RedminePayload(String repository, String ref) {
        this.repository = repository;
        this.ref = ref;
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
    public String toString() {
        return "RedminePayload{" +
                "repository='" + repository + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }
}
