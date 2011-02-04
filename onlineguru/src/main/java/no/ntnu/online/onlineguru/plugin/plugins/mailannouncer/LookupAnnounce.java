package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

/**
 * Static helper method for giving you proper lookup value for
 * a {@link no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.Announce} in {@link no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.EmailImpl}
 *
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class LookupAnnounce  {
    private String toEmail;
    private String listId;

    protected static String getLookup(String toEmail, String listId) {
        if (listId != null && !"".equalsIgnoreCase(listId.trim())) {
            return listId.trim();
        }
        return toEmail;
    }

}
