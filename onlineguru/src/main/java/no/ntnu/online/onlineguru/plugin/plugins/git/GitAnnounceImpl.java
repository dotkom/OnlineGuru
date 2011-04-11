package no.ntnu.online.onlineguru.plugin.plugins.git;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:42
 */
public class GitAnnounceImpl implements GitAnnounce {

    static Logger logger = Logger.getLogger(GitAnnounceImpl.class);
    private final String DEFAULT_DB_FILE_ANNOUNCES = Git.DB_FOLDER + "git-announces.db";
    private HashMap<String, IRCAnnounce> announceHashMap;
    private GitAnnouncementRepository announcementRepository;
    private Wand wand;
    //https://trac.online.ntnu.no/projects/onlineguru/repository/revisions/3ee0eea72999dc408a274c2b940e170a04efc806
    private final String announceFormat = "[scm] %s https://trac.online.ntnu.no/projects/%s/repository/revisions/%s";

    public GitAnnounceImpl(Wand wand) {
        announcementRepository = new SqliteGitAnnouncementPersister(DEFAULT_DB_FILE_ANNOUNCES);
        announceHashMap = announcementRepository.load();
        this.wand = wand;
    }

    public GitAnnounceImpl(Wand wand, GitAnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
        this.announceHashMap = announcementRepository.load();
        this.wand = wand;
    }


    public Boolean publishGitAnnounce(String repository, String changeset) {
        IRCAnnounce announce = announceHashMap.get(repository);

        // Clone the object, as we don't want to modify the orginal one in the announceHashMap
        IRCAnnounce toAnnounce = new IRCAnnounce(announce.getRepository(), announce.getRef(), announce.getAnnounceToChannels());

        toAnnounce.setRef(changeset); // not saved in settings
        return announceToIRC(toAnnounce);

    }


    protected Boolean announceToIRC(IRCAnnounce ircAnnounce) {
        if (wand != null) {
            if (ircAnnounce != null) {
                String announceText = String.format(announceFormat, ircAnnounce.getRepository(), ircAnnounce.getRepository(), ircAnnounce.getRef());

                Iterator iterator = ircAnnounce.getAnnounceToChannels().entrySet().iterator();
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
                logger.warn("Unknown announce, skipping this");
            }

        } else {
            logger.error("IrcWand is null :-(");
        }
        return Boolean.FALSE;
    }


    public Boolean addAnnounce(IRCAnnounce announce) {
        String key = announce.getRepository();
        if (announceHashMap.containsKey(key)) {
            IRCAnnounce settingsSaved = announceHashMap.get(key);
            settingsSaved.setAnnounceToChannels(merge(settingsSaved.getAnnounceToChannels(), announce.getAnnounceToChannels()));
            announcementRepository.save(announceHashMap);
            return Boolean.TRUE;
        } else {
            announceHashMap.put(key, announce);
            announcementRepository.save(announceHashMap);
            return Boolean.TRUE;
        }
    }

    private ConcurrentHashMap<String, List<String>> merge(ConcurrentHashMap<String, List<String>>... lists) {
        ConcurrentHashMap<String, List<String>> result = new ConcurrentHashMap<String, List<String>>();
        if (lists != null && lists.length > 0) {
            for (ConcurrentHashMap<String, List<String>> item : lists) {
                Iterator<Map.Entry<String, List<String>>> data = item.entrySet().iterator();
                while (data.hasNext()) {
                    Map.Entry<String, List<String>> entry = data.next();
                    if (result.containsKey(entry.getKey())) {
                        // merge the list's
                        List<String> mergeList = result.get(entry.getKey());
                        for (String entryValue : entry.getValue()) {
                            // add entry value to already existing list if it doesn't contain entryValue
                            if (!mergeList.contains(entryValue)) {
                                mergeList.add(entryValue);
                            }
                        }
                    } else {
                        // no such key, just put the whole list then..
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return result;
    }


    public Iterator<IRCAnnounce> getAnnounces() {
        return announceHashMap.values().iterator();
    }


    public Boolean removeAnnounce(String announceLookup) {
        if (announceHashMap.containsKey(announceLookup)) {
            announceHashMap.remove(announceLookup);
            announcementRepository.save(announceHashMap);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


}
