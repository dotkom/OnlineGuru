package no.ntnu.online.onlineguru.plugin.plugins.nickserv;

import java.util.HashMap;
import java.util.Map;

/**
 * This holds the authentication usernames for nicks on a network.
 * It is used with {@link NickServ}, which links a Network with an AuthHandler
 *
 * @author Håvard Slettvold
 */


public class AuthHandler {

    private Map<String, String> authList;

    public AuthHandler() {
        authList = new HashMap<String, String>();
    }

    protected void updateNick(String oldNick, String newNick) {
        authList.put(newNick, authList.get(oldNick));
        authList.remove(oldNick);
    }

    protected void addNick(String nick, String username) {
        authList.put(nick, username);
    }

    protected void removeNick(String nick) {
        authList.remove(nick);
    }

    /**
     * Checks if nick is authed with a service.
     * If the username is "1", it means the bot itself has authenticated the user
     *
     * @param nick Nickname to be checked.
     * @return boolean True is authed with a service.
     */
    protected boolean isAuthed(String nick) {
        String username = authList.get(nick);

        if (username != null) {
            if (!authList.get(nick).equals("0")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves the username for a nick.
     *
     * @param nick Nickname to find username for.
     * @return String - Contains username, or null if no username found.
     */
    protected String getUsername(String nick) {
        String username = authList.get(nick);

        if (username != null) {
            if (!username.equals("0")) {
                return username;
            }
        }

        return null;
    }

}
