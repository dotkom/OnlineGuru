package no.ntnu.online.onlineguru.plugin.plugins.git;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.git.github.jsonmodel.Commit;
import no.ntnu.online.onlineguru.plugin.plugins.git.github.jsonmodel.GitHubPayload;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.webserver.NanoHTTPD;
import no.ntnu.online.onlineguru.utils.webserver.Response;
import no.ntnu.online.onlineguru.utils.webserver.WebserverCallback;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static no.ntnu.online.onlineguru.utils.webserver.NanoHTTPD.MIME_PLAINTEXT;


/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:42
 */
public class GitAnnounceImpl implements GitAnnounce, WebserverCallback {

    static Logger logger = Logger.getLogger(GitAnnounceImpl.class);
    public static final String announceGitHubCommit = "[scm][%s/%s] %s (%s) [%sA/%sM/%sD]";
    private final String DEFAULT_DB_FILE_ANNOUNCES = Git.DB_FOLDER + "git-announces.db";
    private HashMap<String, IRCAnnounce> announceHashMap;
    private GitAnnouncementRepository announcementRepository;
    private Wand wand;
    //https://trac.online.ntnu.no/projects/onlineguru/repository/revisions/3ee0eea72999dc408a274c2b940e170a04efc806
    private final String announceFormat = "[scm] %s https://wiki.online.ntnu.no/projects/%s/repository/revisions/%s";
    private Gson gson;


    public GitAnnounceImpl(Wand wand) {
        announcementRepository = new SqliteGitAnnouncementPersister(DEFAULT_DB_FILE_ANNOUNCES);
        init(wand);
    }

    public GitAnnounceImpl(Wand wand, GitAnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
        init(wand);
    }

    public void init(Wand wand) {
        this.announceHashMap = announcementRepository.load();
        this.wand = wand;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .create();

    }


    public Boolean publishGitAnnounce(String repository, String ref) {
        RedminePayload payload = new RedminePayload(repository, ref);

        IRCAnnounce announce = announceHashMap.get(payload.getIdentifier());

// Clone the object, as we don't want to modify the orginal one in the announceHashMap
        IRCAnnounce toAnnounce = new IRCAnnounce(payload, announce.getAnnounceToChannels());

        return announceToIRC(toAnnounce);

    }

    protected Boolean announceToIRC(IRCAnnounce ircAnnounce) {
        if (wand != null) {
            if (ircAnnounce != null) {
                List<String> messages = new ArrayList<String>();

                if (ircAnnounce.getGitPayload() instanceof GitHubPayload) {
                    GitHubPayload gitHubPayload = (GitHubPayload) ircAnnounce.getGitPayload();
                    String activeBranch = gitHubPayload.getRef().split("/")[2];
                    messages.add(String.format("[scm][%s] %s new commits pushed to %s: %s",
                            gitHubPayload.getRepository().getName(),
                            gitHubPayload.getCommits().size(),
                            activeBranch,
                            gitHubPayload.getCompare()
                    ));
                    for (Commit commit : gitHubPayload.getCommits()) {
                        messages.add(String.format(announceGitHubCommit,
                                gitHubPayload.getRepository().getName(),
                                activeBranch,
                                commit.getMessage().replaceAll("\n", ", "),
                                commit.getAuthor().getName(),
                                commit.getAdded().size(),
                                commit.getModified().size(),
                                commit.getRemoved().size()
                        ));
                    }

                } else if (ircAnnounce.getGitPayload() instanceof RedminePayload) {
                    RedminePayload redminePayload = (RedminePayload) ircAnnounce.getGitPayload();
                    messages.add(String.format(announceFormat, redminePayload.getRepository(), redminePayload.getRepository(), redminePayload.getRef()));
                }


                Iterator iterator = ircAnnounce.getAnnounceToChannels().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) iterator.next();

                    Network currentNetwork = wand.getNetworkByAlias(entry.getKey());

                    if (currentNetwork != null && (wand.getNetworkByAlias(currentNetwork.getServerAlias()) != null ? true : false)) {
                        for (String announceToThisChannel : entry.getValue()) {
                            if (wand.amIOnChannel(currentNetwork, announceToThisChannel)) {
                                for (String announceText : messages) {
                                    wand.sendMessageToTarget(currentNetwork, announceToThisChannel, announceText);
                                    logger.debug(String.format("Sending to network %s , channel: %s  with text: %s", currentNetwork.getServerAlias(), announceToThisChannel, announceText));
                                }
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
        String key = announce.getGitPayload().getIdentifier();
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

    public Boolean removeAnnounce(String repositoryIdentifier, String network, String channel) {
        if (announceHashMap.containsKey(repositoryIdentifier)) {
            ConcurrentHashMap<String, List<String>> announceNetworks = announceHashMap.get(repositoryIdentifier).getAnnounceToChannels();
            if (announceNetworks.containsKey(network)) {
                List<String> announceChannels = announceNetworks.get(network);
                if (announceChannels.contains(channel)) {
                    return announceChannels.remove(channel);
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean removeAnnounce(String announceLookup) {
        if (announceHashMap.containsKey(announceLookup)) {
            announceHashMap.remove(announceLookup);
            announcementRepository.save(announceHashMap);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        // make payload to get IRCAnnounce
        logger.info(String.format("uri: %s, method: %s, header: %s, parms: %s, files: %s", uri, method, header, parms, files));

        GitHubPayload payload = null;
        if (method.equalsIgnoreCase("POST") && parms.containsKey("payload")) {
            payload = gson.fromJson(parms.getProperty("payload"), GitHubPayload.class);
        }

        if (payload != null) {
            System.out.println(payload);
            IRCAnnounce announce = announceHashMap.get(payload.getIdentifier());
            IRCAnnounce toAnnounce = new IRCAnnounce(payload, announce.getAnnounceToChannels());
            announceToIRC(toAnnounce);
        }
        return new Response(NanoHTTPD.HTTP_OK, MIME_PLAINTEXT, "OK");


        //IRCAnnounce announce = announceHashMap.get(payload.getIdentifier());

// Clone the object, as we don't want to modify the orginal one in the announceHashMap
        //IRCAnnounce toAnnounce = new IRCAnnounce(payload, announce.getAnnounceToChannels());

        //return announceToIRC(toAnnounce);
    }

    public void httpdServerShutdown(String message) {
        logger.error(message);
    }
}
