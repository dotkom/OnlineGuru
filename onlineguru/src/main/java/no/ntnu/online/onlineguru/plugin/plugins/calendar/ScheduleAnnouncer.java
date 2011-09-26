package no.ntnu.online.onlineguru.plugin.plugins.calendar;


import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.*;

/**
 * @author Roy Sindre Norangshol
 */
public class ScheduleAnnouncer {
    public static final int MAX_EVENT_LOAD_TRIES = 5;
    Timer timer;
    private GoogleCalendar calendar;
    private Map<Event.Type, List<Event>> eventsMap;

    private boolean hasAnnouncedGoodMorning = false;
    private DateTime currentDay;
    private Wand wand;


    static Logger logger = Logger.getLogger(ScheduleAnnouncer.class);
    private static final long LOAD_SLEEP = 5000;

    public ScheduleAnnouncer(GoogleCalendar calendar) {
        init(calendar);
    }

    public ScheduleAnnouncer(Wand wand, GoogleCalendar calendar) {
        this.wand = wand;

        init(calendar);
    }

    private void init(GoogleCalendar calendar) {
        this.calendar = calendar;
        this.eventsMap = new HashMap<Event.Type, List<Event>>();
        this.currentDay = new DateTime();
        timer = new Timer(true);


        loadEvents();

        try {
            logger.info("init calendar plugin, sleeping for 30 seconds..");
            Thread.sleep(30000L);
            logger.info("calendar init sleep done...");
            if (isGoodmorningTime() && !hasAnnouncedGoodMorning) {

                if (isItFriday())
                    sendMessageToOnline("Godmorgen #Online ! Endelig er det fredag!");
                else
                    sendMessageToOnline("Godmorgen #Online !");

                List<String> kontorvakter = getEventTitles(Event.Type.KONTORVAKT);
                if (kontorvakter.size() > 0)
                    sendMessageToOnline(String.format("I dag får vi besøk av %s kontorvakter %s",
                            eventsMap.get(Event.Type.KONTORVAKT).size(),
                            Arrays.toString(kontorvakter.toArray())
                    ));
                else
                    sendMessageToOnline("I dag vil du ikke finne noen kontorvakter på kontoret.. kanskje fordi det er helg?");

                List<String> onlineEvents = getEventTitles(Event.Type.ONLINECALENDAR);
                if (onlineEvents.size() > 0)
                    sendMessageToOnline(String.format("%s aktivitet(er) skjer i dag %s",
                            eventsMap.get(Event.Type.ONLINECALENDAR).size(),
                            Arrays.toString(onlineEvents.toArray())));
                else
                    sendMessageToOnline("Ingen offisielle aktiviteter i dag..");

                hasAnnouncedGoodMorning = true;
            }

            //doHourlyAnnounces();
            startPreschedulerForHourlyScheduler();
        } catch (InterruptedException e) {
            logger.warn(e);
        }


    }


    private void loadEvents() {
        logger.info(" loading calendar events");

        //today = today.withDate(2011, 9, 28);

        // ime(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond)

        Thread loadEventsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                eventsMap.put(Event.Type.KONTORVAKT,
                        fetchCalendar(Event.Type.KONTORVAKT, (eventsMap.containsKey(Event.Type.KONTORVAKT) ? eventsMap.get(Event.Type.KONTORVAKT) : new ArrayList<Event>()))
                );

                eventsMap.put(Event.Type.ONLINECALENDAR,
                        fetchCalendar(Event.Type.ONLINECALENDAR, (eventsMap.containsKey(Event.Type.ONLINECALENDAR) ? eventsMap.get(Event.Type.ONLINECALENDAR) : new ArrayList<Event>()))
                );

            }
        });
        loadEventsThread.run();

    }

    private List<Event> fetchCalendar(Event.Type eventType, List<Event> orignalFallbackEvents) {
        final DateTime today = new DateTime();

        int eventsFailLoadingCounter = 0;

        while (eventsFailLoadingCounter < MAX_EVENT_LOAD_TRIES) {
            try {
                List<Event> events = calendar.getEvent(eventType, today.withTime(0, 0, 0, 0), today.withTime(23, 59, 59, 0));
                sleepAWhile();
                return events;
            } catch (GoogleException e) {
                logger.warn(e);
                eventsFailLoadingCounter++;
                sleepAWhile();
            }
        }
        sendMessageToOnline(String.format("[google internal error] Vi klarte ikke å lese kalenderen til %s - google gir oss server internal error :-(", eventType.toString()));

        return orignalFallbackEvents;
    }

    private void sleepAWhile() {
        try {
            Thread.sleep(LOAD_SLEEP);
        } catch (InterruptedException e) {
            logger.warn(e);
        }
    }

    private void startPreschedulerForHourlyScheduler() {
        logger.info("calendar: loading prescheduler");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("calendar: preschedular launching scheduler!");
                startHourlyScheduler();
            }
        }, getSecondsLeftForNewHour() * 1000);
    }

    private void startHourlyScheduler() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info(" running hourly cronjob from calendar plugin");
                checkForNewDay();
                doHourlyAnnounces();

            }
        }, 0, (60 * 60) * 1000); // repeat every hour

    }

    private void doHourlyAnnounces() {
        loadEvents();
        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        now = now.withTime(now.getHourOfDay(), 0, 0, 0); // removes minutes,seconds

        List<Event> kontorVakter = eventsMap.get(Event.Type.KONTORVAKT);
        for (Event kontorVakt : kontorVakter) {
            Duration durationOnTimestamps;
            if (now.isBefore(kontorVakt.getStartDate())) {
                durationOnTimestamps = new Duration(now, kontorVakt.getStartDate());


                if (durationOnTimestamps.isShorterThan(Duration.standardHours(1))) {
                    sendMessageToOnline(String.format("[Kontorvakt] %s skal nå være kontorvakt!", kontorVakt.getTitle()));
                }

                if ((durationOnTimestamps.isEqual(Duration.standardHours(1)) || durationOnTimestamps.isLongerThan(Duration.standardHours(1))) && durationOnTimestamps.isShorterThan(Duration.standardHours(2))) {
                    sendMessageToOnline(String.format("[Kontorvakt ETA] %stil %s har kontorvakt!", getPeriodInStringFormat(durationOnTimestamps.toPeriod()), kontorVakt.getTitle()));
                }
            }
        }

        // Denne er ikke helt riktig, men riktig nok for klokketimer for no :p
        List<Event> onlineEvents = eventsMap.get(Event.Type.ONLINECALENDAR);
        for (Event onlineEvent : onlineEvents) {
            Duration durationOnTimestamps;
            if (now.isBefore(onlineEvent.getStartDate())) {
                durationOnTimestamps = new Duration(now, onlineEvent.getStartDate());


                if (durationOnTimestamps.isShorterThan(Duration.standardHours(1))) {
                    sendMessageToOnline(String.format("[Event start] %s", onlineEvent.getTitle()));
                }

                if ((durationOnTimestamps.isEqual(Duration.standardHours(1)) || durationOnTimestamps.isLongerThan(Duration.standardHours(1))) && durationOnTimestamps.isShorterThan(Duration.standardHours(4))) {
                    sendMessageToOnline(String.format("[Event ETA] %stil %s starter!", getPeriodInStringFormat(durationOnTimestamps.toPeriod()), onlineEvent.getTitle()));
                }
            }
        }
    }

    private String getPeriodInStringFormat(Period period) {
        String appendSuffixFormat = " %s ";


        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendYears().appendSuffix(String.format(appendSuffixFormat, "år"))
                .appendMonths().appendSuffix(String.format(appendSuffixFormat, "måned"))
                .appendWeeks().appendSuffix(String.format(appendSuffixFormat, "uker"))
                .appendDays().appendSuffix(String.format(appendSuffixFormat, "dager"))
                .appendHours().appendSuffix(String.format(appendSuffixFormat, "time(r)"))
                .appendMinutes().appendSuffix(String.format(appendSuffixFormat, "minutter"))
                        //.appendSeconds().appendSuffix(" seconds ")

                .printZeroNever()
                .toFormatter();

        String elapsed = formatter.print(period);
        return elapsed;

    }


    private void checkForNewDay() {
        DateTime now = new DateTime();
        if (now.getDayOfMonth() != currentDay.getDayOfMonth()) {
            currentDay = now;
            hasAnnouncedGoodMorning = false;
        }
    }

    private List<String> getEventTitles(Event.Type type) {
        List<String> titles = new ArrayList<String>();
        if (eventsMap.containsKey(type)) {
            Iterator<Event> iterator = eventsMap.get(type).iterator();
            while (iterator.hasNext()) {
                Event event = iterator.next();
                titles.add(event.getTitle());
            }
        }
        return titles;
    }

    private int getSecondsLeftForNewHour() {
        DateTime now = new DateTime();
        return (60 - now.getMinuteOfHour()) * 60;
    }

    private boolean isGoodmorningTime() {
        DateTime now = new DateTime();
        return now.getHourOfDay() == 8;
    }

    private boolean isItFriday() {
        DateTime now = new DateTime();
        return now.dayOfWeek().get() == 5;
    }

    private void sendMessageToOnline(String message) {
        logger.debug(message);
        if (wand.amIOnChannel(wand.getNetworkByAlias("freenode"), "#online"))
            wand.sendMessageToTarget(wand.getNetworkByAlias("freenode"), "#online", message);
    }

    public static void main(String[] args) {
        new ScheduleAnnouncer(new GoogleCalendar());
    }
}
