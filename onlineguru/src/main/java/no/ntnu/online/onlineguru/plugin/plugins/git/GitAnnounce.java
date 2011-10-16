package no.ntnu.online.onlineguru.plugin.plugins.git;

/**
 * Interface used for XMLRPCServer to listen for incoming XMLRPC calls and create RedminePayload's at our side.
 * Note: Didn't figure out how to pass RedminePayload objects directly from python's xmlrpclib, so we're
 * cheating and taking RedminePayload's constructor arguments and we'll recreate the object at our side .. sighs.
 *
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 */
public interface GitAnnounce {
    Boolean publishGitAnnounce(String repository, String ref);
}
