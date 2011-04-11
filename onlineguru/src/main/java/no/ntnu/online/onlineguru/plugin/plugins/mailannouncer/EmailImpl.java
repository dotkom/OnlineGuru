package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.LookupAnnounce.*;

/**
 * Email Implementation
 * Object get initiated for every email we retrieve over XML-RPC and calls
 * method Boolean announceEmail(String toEmail, String fromEmail, String subject, String listId)
 *
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class EmailImpl implements Email {
    static Logger logger = Logger.getLogger(EmailImpl.class);
    private final String DEFAULT_DB_FILE_ANNOUNCES = MailAnnouncer.DB_FOLDER + "mailannouncer-announces.db";
    private HashMap<String, Announce> announceHashMap;
    private AnnouncementRepository announcementRepository;
    private Wand wand;

    public EmailImpl(Wand wand) {
        announcementRepository = new SqliteAnnouncementPersister(DEFAULT_DB_FILE_ANNOUNCES);
        announceHashMap = announcementRepository.load();
        this.wand = wand;
    }

    public EmailImpl(Wand wand, AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
        this.announceHashMap = announcementRepository.load();
        this.wand = wand;
    }


    /**
     * Method for client to execute for announcing email's on IRC.
     * Will ignore email announce if it is not stored in our Announce list (announceHashMap)
     *
     * @param toEmail   Email TO/RCPT address
     * @param fromEmail Email from address
     * @param subject   Email subject
     * @param listId    Mailing list id if it exists
     * @return <code>true</code> if it announced to any IRC channels
     */
    public Boolean announceEmail(String toEmail, String fromEmail, String subject, String listId) {
        logger.debug(String.format("Received email to %s, from %s with subject %s on listId: %s", toEmail, fromEmail, subject, listId));
        logger.debug(announceHashMap.size());
        if (announceHashMap.containsKey(getLookup(toEmail, listId))) {
            // Looked up Announce from either toEmail or by listId  (see LookupAnnounce to see #getLookup(toEmail, listId)
            Announce announce = announceHashMap.get(getLookup(toEmail, listId));


            // Clone the object, as we don't want to modify the orginal one in the announceHashMap
            Announce toAnnounce = new Announce(announce.getAnnounceTag(), announce.getToEmail(), announce.getToEmail(), announce.getAnnounceToChannels(), announce.getListId());

            // Since we fetch the announce object that is stored in our data storage, we do not have fromEmail, subject.
            toAnnounce.setFromEmail(fromEmail); // not stored when saving settings..
            toAnnounce.setSubject(subject); // not stored when saving settings..
            return announceToIRC(toAnnounce);
        } else {
            EmailImpl.logger.warn("Unknown announce, skipping this email");
            ConcurrentHashMap<String, List<String>> debug = new ConcurrentHashMap<String, List<String>>();
            List<String> debugChannel = new ArrayList<String>();
            debugChannel.add("#online.dotkom");
            debug.put("freenode", debugChannel);
            Announce debugAnnounce = new Announce(toEmail, fromEmail, debug);
            debugAnnounce.setAnnounceTag("DEBUG");
            debugAnnounce.setFromEmail(fromEmail);
            debugAnnounce.setSubject(subject);
            debugAnnounce.setListId(listId);
            announceToIRC(debugAnnounce);
            return false;
        }
    }

    protected Boolean announceToIRC(Announce announce) {
        if (wand != null) {
            if (announce != null) {
                String announceText = String.format("[Mail-%s] %s - %s", (announce.getAnnounceTag() != null ? announce.getAnnounceTag() : ""), announce.getFromEmail(), announce.getSubject());
                Iterator iterator = announce.getAnnounceToChannels().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) iterator.next();

                    Network currentNetwork = wand.getNetworkByAlias(entry.getKey());

                    if (currentNetwork != null && (wand.getNetworkByAlias(currentNetwork.getServerAlias()) != null ? true : false)) {
                        for (String announceToThisChannel : entry.getValue()) {
                            if (wand.amIOnChannel(currentNetwork, announceToThisChannel)) {
                                wand.sendMessageToTarget(currentNetwork, announceToThisChannel, announceText);
                                logger.debug(String.format("Sending to network %s , channel: %s  with text: %s", currentNetwork.getServerAlias(), announceToThisChannel, announceText));
                            }
                        }
                    }

                }
                return Boolean.TRUE;
            } else {
                logger.warn("Unknown announce, skipping this email");
            }

        } else {
            logger.error("IrcWand is null :-(");
        }
        return Boolean.FALSE;
    }


    /**
     * Save an Announce with lookup-key either being toEmail or listId. Defined by LookupAnnounce#getLookup(toEmail, listId) method..
     *
     * @param announce Announce object to save
     * @see no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.LookupAnnounce#getLookup(String, String)
     */
    public Boolean addAnnounce(Announce announce) {
        String key = getLookup(announce.getToEmail(), announce.getListId());
        if (announceHashMap.containsKey(key)) {
            logger.debug("Entry already exists, skipping");
            return Boolean.FALSE;
        } else {
            announceHashMap.put(key, announce);
            announcementRepository.save(announceHashMap);
            return Boolean.TRUE;
        }
    }

    /**
     * Retreives announces we have stored
     *
     * @return Itereator over announces.
     */
    public Iterator<Announce> getAnnounces() {
        return announceHashMap.values().iterator();
    }

    /**
     * Remove announce from our storage.
     * Remember to use LookupAnnounce#getLookup(String, String) ...
     *
     * @param announceLookup Key to remove.
     * @see no.ntnu.online.onlineguru.plugin.plugins.mailannouncer.LookupAnnounce#getLookup(String, String)
     */
    public Boolean removeAnnounce(String announceLookup) {
        if (announceHashMap.containsKey(announceLookup)) {
            announceHashMap.remove(announceLookup);
            announcementRepository.save(announceHashMap);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean setAnnounceTag(String announceLookup, String tag) {
        if (announceHashMap.containsKey(announceLookup)) {
            Announce announceToEdit = announceHashMap.get(announceLookup);
            announceToEdit.setAnnounceTag(tag);
            announcementRepository.save(announceHashMap);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }

}
