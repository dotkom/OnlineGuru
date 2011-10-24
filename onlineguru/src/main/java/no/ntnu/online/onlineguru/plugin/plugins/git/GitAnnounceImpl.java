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

                for (Map.Entry<String, List<ChannelAnnounce>> entry : ircAnnounce.getAnnounceToChannels().entrySet()) {
                    Network currentNetwork = wand.getNetworkByAlias(entry.getKey());

                    if (currentNetwork != null && (wand.getNetworkByAlias(currentNetwork.getServerAlias()) != null)) {
                        for (ChannelAnnounce announceToThisChannel : entry.getValue()) {
                            if (wand.amIOnChannel(currentNetwork, announceToThisChannel.getChannel())) {
                                List<String> messages = generateMessageForChannel(announceToThisChannel, ircAnnounce);

                                for (String announceText : messages) {
                                    wand.sendMessageToTarget(currentNetwork, announceToThisChannel.getChannel(), announceText);
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

    private List<String> generateMessageForChannel(ChannelAnnounce channelAnnounce, IRCAnnounce ircAnnounce) {
        List<String> messages = new ArrayList<String>();

        if (ircAnnounce.getGitPayload() instanceof GitHubPayload) {
            GitHubPayload gitHubPayload = (GitHubPayload) ircAnnounce.getGitPayload();

            if (gitHubPayload.getPullRequest() != null) {
                // pull request announce
                messages.add(String.format("[github][%s] %s - %s. %s -> %s %s",
                        gitHubPayload.getRepository().getName(),
                        gitHubPayload.getPullRequest().getTitle(),
                        gitHubPayload.getPullRequest().getUser().getLogin(),
                        gitHubPayload.getPullRequest().getBase().getLabel(),
                        gitHubPayload.getPullRequest().getHead().getLabel(),
                        gitHubPayload.getPullRequest().getHtmlUrl()
                        ));
            } else if (gitHubPayload.getIssue() != null) {
                // issue announce
                messages.add(String.format("[github][%s] %s - %s. %s",
                        gitHubPayload.getRepository().getName(),
                        gitHubPayload.getIssue().getTitle(),
                        gitHubPayload.getIssue().getUser().getLogin(),
                        gitHubPayload.getIssue().getHtmlUrl()
                        ));
            } else {
                // assume normal push

                String activeBranch = gitHubPayload.getRef().split("/")[2];
                messages.add(String.format("[scm][%s] %s new commits pushed to %s: %s",
                        gitHubPayload.getRepository().getName(),
                        gitHubPayload.getCommits().size(),
                        activeBranch,
                        gitHubPayload.getCompare()
                ));
                if (channelAnnounce.getVerboseLevel().ordinal() >= 2) { //VerboseLevel.EVERYTHING
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
                }
            }

        } else if (ircAnnounce.getGitPayload() instanceof RedminePayload) {
            RedminePayload redminePayload = (RedminePayload) ircAnnounce.getGitPayload();
            messages.add(String.format(announceFormat, redminePayload.getRepository(), redminePayload.getRepository(), redminePayload.getRef()));
        }
        return messages;
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

    private ConcurrentHashMap<String, List<ChannelAnnounce>> merge(ConcurrentHashMap<String, List<ChannelAnnounce>>... lists) {
        ConcurrentHashMap<String, List<ChannelAnnounce>> result = new ConcurrentHashMap<String, List<ChannelAnnounce>>();
        if (lists != null && lists.length > 0) {
            for (ConcurrentHashMap<String, List<ChannelAnnounce>> item : lists) {
                for (Map.Entry<String, List<ChannelAnnounce>> entry : item.entrySet()) {
                    if (result.containsKey(entry.getKey())) {
                        // merge the list's
                        List<ChannelAnnounce> mergeList = result.get(entry.getKey());
                        for (ChannelAnnounce entryValue : entry.getValue()) {
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
            ConcurrentHashMap<String, List<ChannelAnnounce>> announceNetworks = announceHashMap.get(repositoryIdentifier).getAnnounceToChannels();
            if (announceNetworks.containsKey(network)) {
                List<ChannelAnnounce> announceChannels = announceNetworks.get(network);
                for (ChannelAnnounce announceChannel : announceChannels) {
                    if (announceChannel.getChannel().equalsIgnoreCase(channel))
                        return announceChannels.remove(announceChannel);
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
