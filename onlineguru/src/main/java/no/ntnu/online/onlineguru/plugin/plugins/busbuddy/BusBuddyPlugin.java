package no.ntnu.online.onlineguru.plugin.plugins.busbuddy;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.norrs.busbuddy.pub.api.BusBuddyAPIServiceController;
import no.norrs.busbuddy.pub.api.model.BusStop;
import no.norrs.busbuddy.pub.api.model.BusStopContainer;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;
import no.norrs.busbuddy.pub.api.model.Oracle;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Roy Sindre Norangshol
 */
public class BusBuddyPlugin implements Plugin {
    private static final String DESCRIPTION = "BusBuddy (Public transportation plugin for users in Trondheim, Norway)";
    private final String TRIGGER_BB = "!bb";
    private final String TRIGGER_BUSS = "!buss";
    private final String TRIGGER_SANNTID = "!sanntid";
    private final String SETTINGS_FOLDER = "settings/";
    private final String SETTINGS_FILE = SETTINGS_FOLDER + "busbuddy.conf";
    private Wand wand;
    BusBuddyAPIServiceController busBuddyAPIServiceController;
    static Logger logger = Logger.getLogger(BusBuddyPlugin.class);
    private Pattern pattern1;
    private List<BusStop> searchStops;


    public BusBuddyPlugin() {
        String apiKey = null;
        try {
            apiKey = SimpleIO.loadConfig(SETTINGS_FILE).get("apikey");
            if (apiKey == null)
                apiKey = "busbuddytoll";

        } catch (IOException e) {
            logger.warn("No api key found for busbuddy, only oracle support provided.");
        }
        busBuddyAPIServiceController = new BusBuddyAPIServiceController(apiKey);
        pattern1 = Pattern.compile("(?:Holdeplassen nærmest (?:\\w.+?) er|Buss? \\d+ (?:passerer|går fra|goes from)) (\\w.+?)(?:\\.| (?:kl|at))");
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                PrivMsgEvent privMsgEvent = (PrivMsgEvent) e;
                if (privMsgEvent.getMessage().toLowerCase().startsWith(TRIGGER_BB))
                    handleOracleRequest(privMsgEvent, TRIGGER_BB.length());
                else if (privMsgEvent.getMessage().toLowerCase().startsWith(TRIGGER_BUSS))
                    handleOracleRequest(privMsgEvent, TRIGGER_BUSS.length());
                else if (privMsgEvent.getMessage().toLowerCase().startsWith(TRIGGER_SANNTID))
                    handleRealTimeRequest(privMsgEvent, TRIGGER_SANNTID.length());
            }
        }
    }

    private void handleRealTimeRequest(final PrivMsgEvent privMsgEvent, final int triggerLength) {
        Runnable newAsyncQuery = new Runnable() {
            public void run() {
                try {
                    String[] splitQueryIntoWords = privMsgEvent.getMessage().substring(triggerLength + 1).trim().split("\\s+");
                    int direction;
                    if (splitQueryIntoWords.length > 0) {
                        // index 1 should be a number, 1 is towards Trondheim, 0 is the other way
                        direction = Integer.parseInt(splitQueryIntoWords[1]);
                    }

                    if (splitQueryIntoWords.length > 1) {
                        try {
                            int cacheLookupFromEarlierSearch = Integer.parseInt(splitQueryIntoWords[2]);
                        } catch (NumberFormatException nfe) {
                            // Assume a search is wanted ;-)

                            String searchTerms = privMsgEvent.getMessage().substring(triggerLength + 3).trim();
                            BusStopContainer searchAnswer = busBuddyAPIServiceController.getBusStopForecastsByBusStopSearch(searchTerms);
                            if (searchAnswer != null && searchAnswer.getBusStops() != null && searchAnswer.getBusStops().size() > 0)
                                announceRealTime(privMsgEvent, searchAnswer.getBusStops().get(0));
                            else
                                wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] No search results for '%s'", searchTerms));
                        }
                    } else {
                        announceRealTime(privMsgEvent, searchStops.get(0));
                    }
                } catch (NumberFormatException nfe) {
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), "[BusBuddy-ERR] Invalid direction, 1 towards Trondheim centrum, 0 the other way ;-)");
                } catch (IOException ioException) {
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy-ERR] IOException: %s", ioException.getMessage()));
                }
            }
        };
        newAsyncQuery.run();
    }

    private void announceRealTime(PrivMsgEvent privMsgEvent, BusStop busStop) throws IOException {
        DepartureContainer departeResult = busBuddyAPIServiceController.getBusStopForecasts(busStop.getLocationId());
        wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] Sanntid for '%s' with direction %s Trondheim centrum", busStop.getName(), (busStop.isGoingTowardsCentrum() ? "to" : "away from")));
        wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] %s", Arrays.toString(departeResult.getDepartures().toArray())));
    }

    private void handleOracleRequest(final PrivMsgEvent privMsgEvent, final int triggerLength) {
        Runnable newAsyncQuery = new Runnable() {
            public void run() {
                Oracle answer;
                try {
                    logger.debug(String.format("BusBuddy asking '%s'", privMsgEvent.getMessage().toLowerCase().substring(triggerLength + 1).trim()));
                    answer = handleBusBuddyOracleRequest(privMsgEvent.getMessage().toLowerCase().substring(triggerLength + 1).trim());
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] %s", answer.getAnswer().trim().replaceAll("\\r\\n|\\r|\\n", " ")));
                    String travelDestinationFrom = getTravelDestinationFrom(answer.getAnswer().trim());
                    if (travelDestinationFrom != null) {
                        announceRealtimeAd(privMsgEvent, travelDestinationFrom);
                    }
                } catch (IOException e1) {
                    logger.error(String.format("Error while asking busbuddy oracle: %s", e1));
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy-ERR] IOException: %s", e1.getMessage()));
                }

            }
        };
        newAsyncQuery.run();
    }

    private void announceRealtimeAd(final PrivMsgEvent privMsgEvent, final String travelDestinationFrom) {
        Runnable newAsyncSearchQuery = new Runnable() {
            public void run() {
                BusStopContainer searchAnswers = null;
                try {
                    searchAnswers = busBuddyAPIServiceController.getBusStopForecastsByBusStopSearch(travelDestinationFrom);
                    searchStops = searchAnswers.getBusStops();
                    if (searchStops != null && searchStops.size() > 0) {
                        wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(),
                                String.format("[BusBuddy] Hent sanntid for %s ved å skrive !sanntid <retning> (1 mot sentrum, 0 andre veien..). Feil startplass? Prøv !sanntid <retning> <stedsnavn_nummer> gitt her: %s",
                                        searchStops.get(0).getName(),
                                        String.format("%s (#1), %s (#2), %s (#3), %s (#4), %s (#5), %s (#6)",
                                                searchStops.get(1).getName(),
                                                searchStops.get(2).getName(),
                                                searchStops.get(3).getName(),
                                                searchStops.get(4).getName(),
                                                searchStops.get(5).getName(),
                                                searchStops.get(6).getName()
                                        )
                                ));
                    }
                } catch (IOException e) {
                    logger.error(String.format("Error while doing search on busbuddy api: %s", e));
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy-ERR] IOException: %s", e.getMessage()));
                }
            }
        };
        newAsyncSearchQuery.run();
    }

    private Oracle handleBusBuddyOracleRequest(String question) throws IOException {
        return busBuddyAPIServiceController.askOracle(new Oracle(question));
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

    private String getTravelDestinationFrom(String oracleQuery) {
        return pattern1.matcher(oracleQuery).group(1);
    }
}
