package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

/**
 * Email interface
 * Used for exporting the Email service in our XML-RPC server
 *
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public interface Email {
    public Boolean announceEmail(String toEmail, String fromEmail, String subject, String listId);
}
