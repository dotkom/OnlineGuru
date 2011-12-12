package no.ntnu.online.onlineguru.plugin.plugins.busbuddy;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.norrs.busbuddy.pub.api.BusBuddyAPIServiceController;
import no.norrs.busbuddy.pub.api.model.*;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;
import java.util.*;
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
        init(apiKey);
    }
    public BusBuddyPlugin(String apiKey) {
        init(apiKey);
    }

    private void init(String apiKey) {
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
                    int direction = -1;
                    if (splitQueryIntoWords.length > 0) {
                        // index 1 should be a number, 1 is towards Trondheim, 0 is the other way
                        direction = Integer.parseInt(splitQueryIntoWords[0]);
                    }

                    if (splitQueryIntoWords.length > 1) {
                        try {
                            int cacheLookupFromEarlierSearch = Integer.parseInt(splitQueryIntoWords[1]);
                            announceRealTime(privMsgEvent, searchStops.get(cacheLookupFromEarlierSearch-1), direction);
                        } catch (NumberFormatException nfe) {
                            // Assume a search is wanted ;-)

                            String searchTerms = privMsgEvent.getMessage().substring(triggerLength + 3).trim();
                            logger.info("BusBuddy lookup search for " + searchTerms);
                            BusStopContainer searchAnswer = busBuddyAPIServiceController.getBusStopsBySearchTerms(searchTerms);
                            if (searchAnswer != null && searchAnswer.getBusStops() != null && searchAnswer.getBusStops().size() > 0)
                                announceRealTime(privMsgEvent, searchAnswer.getBusStops().get(0), direction);
                            else
                                wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] Ingen søkeresultater for '%s'", searchTerms));
                        } catch(IndexOutOfBoundsException aiofbe) {
                            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), "[BusBuddy-ERR] Ugyldig indeks på mellomlager, din dust!");
                        }
                    } else {
                        announceRealTime(privMsgEvent, searchStops.get(0), direction);
                    }
                } catch (NumberFormatException nfe) {
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), "[BusBuddy-ERR] Ugyldig retning, 1 mot Trondheim sentrum, 0 for den andre veien ;-)");
                } catch (IOException ioException) {
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy-ERR] IOException: %s", ioException.getMessage()));
                }
            }
        };
        newAsyncQuery.run();
    }

    private void announceRealTime(PrivMsgEvent privMsgEvent, BusStop busStop, int direction) throws IOException {
        String locationId = busStop.getLocationId();
        if ((int) locationId.charAt(4) != direction)
            locationId = new StringBuilder(locationId).replace(4, 5, String.valueOf(direction)).toString();
        wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] Sanntid for '%s' %s Trondheim sentrum..", busStop.getName(), (direction == 1 ? "mot" : "bort fra")));

        DepartureContainer departeResult = busBuddyAPIServiceController.getBusStopForecasts(locationId);
        String departuresPrettyLine = Arrays.toString(prettyPrintDepartures(departeResult.getDepartures()).toArray());
        departuresPrettyLine = departuresPrettyLine.substring(1, departuresPrettyLine.length()-1).replaceAll(",",""); // HACKS, DON'T LIKE IT? TROLOLOLO
        wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] %s", departuresPrettyLine));
    }

    private List<String> prettyPrintDepartures(List<Departure> departures) {
        List<String> pretty = new ArrayList<String>();
        for (Departure forecast : departures) {
            LocalDateTime registeredDepartureTime = forecast.getRegisteredDepartureTime();
            LocalDateTime currentTimestamp = new LocalDateTime();
            LocalDateTime scheduledDepartureTime = forecast.getScheduledDepartureTime().withDate(currentTimestamp.getYear(), currentTimestamp.getMonthOfYear(), currentTimestamp.getDayOfMonth());

            String departureTime;
            if (forecast.isRealtimeData()) {
                departureTime = getPeriodInStringFormat(registeredDepartureTime.toDateTime());
            } else {
                departureTime = String.format("ca. %s", getPeriodInStringFormat(scheduledDepartureTime.toDateTime()));
            }
            departureTime = departureTime.trim();
            
            if (departureTime.equalsIgnoreCase("") || departureTime.equalsIgnoreCase("ca.")) {
                if (forecast.isRealtimeData()) {
                    departureTime = String.format("< 1 %s", "minutt");
                } else {
                    departureTime = String.format("< ca. 1 %s", "minutt");
                }
            } else {
                if (departureTime.contains("-")) {
                    departureTime = scheduledDepartureTime.toString("hh:mm");
                } else {
                    departureTime = departureTime;
                }
            }
            pretty.add(String.format("[%s] %s -> %s.", forecast.getLine(), forecast.getDestination(), departureTime));
        }
        return pretty;
    }

    private void handleOracleRequest(final PrivMsgEvent privMsgEvent, final int triggerLength) {
        Runnable newAsyncQuery = new Runnable() {
            public void run() {
                Oracle answer;
                try {
                    logger.debug(String.format("BusBuddy asking '%s'", privMsgEvent.getMessage().toLowerCase().substring(triggerLength + 1).trim()));
                    answer = handleBusBuddyOracleRequest(privMsgEvent.getMessage().toLowerCase().substring(triggerLength + 1).trim());
                    wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), String.format("[BusBuddy] %s", answer.getAnswer().trim().replaceAll("\\r\\n|\\r|\\n", " ")));
                    System.out.println(answer);
                    System.out.println(answer.getDestinationFrom());
                    if (answer.getDestinationFrom() != null) {
                        announceRealtimeAd(privMsgEvent, answer.getDestinationFrom());
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
                    searchAnswers = busBuddyAPIServiceController.getBusStopsBySearchTerms(URIUtil.encodeQuery(travelDestinationFrom));
                    searchStops = searchAnswers.getBusStops();
                    if (searchStops != null && searchStops.size() > 0) {
                        searchStops = removeDuplicateBusStops(searchStops);
                        String otherStops = "";
                        for(int i=0; i < searchStops.size(); i++) 
                            otherStops += String.format("%s (#%s), ", searchStops.get(i).getName(), i+1);
                        
                        wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(),
                                String.format("[BusBuddy] Hent sanntid for %s ved å skrive !sanntid <retning> (1 mot sentrum, 0 andre veien..). Feil startplass? Prøv !sanntid <retning> <stedsnavn_nummer> gitt her: %s",
                                        searchStops.get(0).getName(),
                                        otherStops
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

    public List<BusStop> removeDuplicateBusStops(List<BusStop> searchStops) {
        Set<String> s = new HashSet<String>();
        List<BusStop> m = new ArrayList<BusStop>();
        for (BusStop busStop : searchStops) {
            if (s.contains(busStop.getName().trim()))
                continue;
            s.add(busStop.getName());
            m.add(busStop);
        }
        return m;
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

    private String getPeriodInStringFormat(DateTime localTime) {
        DateTime timestamp = localTime;
        DateTime now = new DateTime();
        Period period = new Period(now, timestamp);
        String appendSuffixFormat = " %s ";
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendYears().appendSuffix(String.format(appendSuffixFormat, "år"))
                .appendMonths().appendSuffix(String.format(appendSuffixFormat, "måneder"))
                .appendWeeks().appendSuffix(String.format(appendSuffixFormat, "uker"))
                .appendDays().appendSuffix(String.format(appendSuffixFormat, "dager"))
                .appendHours().appendSuffix(String.format(appendSuffixFormat, "timer"))
                .appendMinutes().appendSuffix(String.format(appendSuffixFormat, "minutter"))
                        //.appendSeconds().appendSuffix(" seconds ")

                .printZeroNever()
                .toFormatter();

        String elapsed = formatter.print(period);
        return elapsed;

    }
}
