package no.ntnu.online.onlineguru.plugin.plugins.git;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:54
 */
public class IRCAnnounce implements Serializable {
    private String repository;
    private String ref;

    private ConcurrentHashMap<String, List<String>> announceToChannels;

    public IRCAnnounce(String repository, String ref, ConcurrentHashMap<String, List<String>> announceToChannels) {
        this.repository = repository;
        this.ref = ref;
        this.announceToChannels = announceToChannels;
    }

    public IRCAnnounce(String repository, ConcurrentHashMap<String, List<String>> announceToChannels) {
        this.repository = repository;
        this.announceToChannels = announceToChannels;
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

    public ConcurrentHashMap<String, List<String>> getAnnounceToChannels() {
        return announceToChannels;
    }

    public void setAnnounceToChannels(ConcurrentHashMap<String, List<String>> announceToChannels) {
        this.announceToChannels = announceToChannels;
    }

    @Override
    public String toString() {
        return "IRCAnnounce{" +
                "announceToChannels=" + announceToChannels +
                ", ref='" + ref + '\'' +
                ", repository='" + repository + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IRCAnnounce that = (IRCAnnounce) o;

        if (!repository.equals(that.repository)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return repository.hashCode();
    }
}
