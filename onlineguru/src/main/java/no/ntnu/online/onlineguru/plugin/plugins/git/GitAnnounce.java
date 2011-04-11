package no.ntnu.online.onlineguru.plugin.plugins.git;

/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:41
 */
public interface GitAnnounce {
    Boolean publishGitAnnounce(String repository, String changeset);
}
