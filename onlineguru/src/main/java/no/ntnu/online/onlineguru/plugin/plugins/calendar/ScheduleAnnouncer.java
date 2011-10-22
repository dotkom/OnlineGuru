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
    public static final String OFFICEHOUR_NOW = "[Kontorvakt] %s skal nå være kontorvakt!";
    public static final String OFFICEHOUR_ETA = "[Kontorvakt ETA] %stil %s har kontorvakt!";
    public static final String EVENT_NOW = "[Event start] %s  (%s)";
    public static final String EVENT_ETA = "[Event ETA] %stil %s starter!";
    Timer timer;
    private GoogleCalendar calendar;
    private Map<Event.Type, List<Event>> eventsMap;

    private boolean hasAnnouncedGoodMorning = false;
    private DateTime currentDay;
    private Wand wand;


    static Logger logger = Logger.getLogger(ScheduleAnnouncer.class);

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


            if (isGoodmorningTime() && !hasAnnouncedGoodMorning) {
                // sleep since it may take some time before onlineguru actaully gets his ass online
                // but chances is low that the bot starts at 08:XX tho, but you never know!
                Thread.sleep(30000L);
                getGoodmorningAnnounces();
            }

            //getHourlyAnnounces();
            startPreschedulerForHourlyScheduler();
        } catch (InterruptedException e) {
            logger.warn(e);
        }


    }

    private List<String> getGoodmorningAnnounces() {
        List<String> messages = new ArrayList<String>();
        if (isItFriday())
            messages.add("Godmorgen #Online ! Endelig er det fredag!");
        else
            messages.add("Godmorgen #Online !");

        List<String> kontorvakter = getEventTitles(Event.Type.KONTORVAKT);
        if (kontorvakter.size() > 0)
            messages.add(String.format("I dag får vi besøk av %s kontorvakter %s",
                    eventsMap.get(Event.Type.KONTORVAKT).size(),
                    Arrays.toString(kontorvakter.toArray())
            ));
        else
            messages.add("I dag vil du ikke finne noen kontorvakter på kontoret.. kanskje fordi det er helg?");

        List<String> onlineEvents = getEventTitles(Event.Type.ONLINECALENDAR);
        if (onlineEvents.size() > 0)
            messages.add(String.format("%s aktivitet(er) skjer i dag %s",
                    eventsMap.get(Event.Type.ONLINECALENDAR).size(),
                    Arrays.toString(onlineEvents.toArray())));
        else
            messages.add("Ingen offisielle aktiviteter i dag..");

        hasAnnouncedGoodMorning = true;

        return messages;
    }


    private void loadEvents() {
        logger.info(" loading calendar events");

        //today = today.withDate(2011, 9, 28);

        // ime(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond)

        Thread loadEventsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                eventsMap.put(Event.Type.KONTORVAKT,
                        calendar.fetchCalendar(Event.Type.KONTORVAKT, (eventsMap.containsKey(Event.Type.KONTORVAKT) ? eventsMap.get(Event.Type.KONTORVAKT) : new ArrayList<Event>()))
                );

                eventsMap.put(Event.Type.ONLINECALENDAR,
                        calendar.fetchCalendar(Event.Type.ONLINECALENDAR, (eventsMap.containsKey(Event.Type.ONLINECALENDAR) ? eventsMap.get(Event.Type.ONLINECALENDAR) : new ArrayList<Event>()))
                );

            }
        });
        loadEventsThread.run();

    }

    private void startPreschedulerForHourlyScheduler() {
        logger.info("calendar: loading prescheduler");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("calendar: preschedular launching scheduler!");
                startHourlyScheduler();
                checkForNewDay();
            }
        }, getSecondsLeftForNewHour() * 1000);
    }

    private void startHourlyScheduler() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info(" running hourly cronjob from calendar plugin");
                checkForNewDay();
                sendMessageToOnline(getHourlyAnnounces());

            }
        }, 0, (60 * 60) * 1000); // repeat every hour

    }

    public List<String> getHourlyAnnounces() {
        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        return getHourlyAnnounces(now.withTime(now.getHourOfDay(), 0, 0, 0)); // removes minutes,seconds
    }

    public List<String> getHourlyAnnounces(DateTime now) {
        List<String> announces = new ArrayList<String>();

        loadEvents();

        if (isGoodmorningTime() && !hasAnnouncedGoodMorning) {
            announces = getGoodmorningAnnounces();
        }


        List<Event> kontorVakter = eventsMap.get(Event.Type.KONTORVAKT);
        for (Event kontorVakt : kontorVakter) {
            Duration durationOnTimestamps;
            if (now.isBefore(kontorVakt.getStartDate()) || kontorVakt.getStartDate().isEqual(now)) {
                durationOnTimestamps = new Duration(now, kontorVakt.getStartDate());

                if (durationOnTimestamps.isShorterThan(Duration.standardHours(1))) {
                    announces.add(String.format(OFFICEHOUR_NOW, kontorVakt.getTitle()));
                }

                if ((durationOnTimestamps.isEqual(Duration.standardHours(1)) || durationOnTimestamps.isLongerThan(Duration.standardHours(1))) && durationOnTimestamps.isShorterThan(Duration.standardHours(2))) {
                    announces.add(String.format(OFFICEHOUR_ETA, getPeriodInStringFormat(durationOnTimestamps.toPeriod()), kontorVakt.getTitle()));
                }
            }
        }

        // Denne er ikke helt riktig, men riktig nok for klokketimer for no :p
        List<Event> onlineEvents = eventsMap.get(Event.Type.ONLINECALENDAR);
        for (Event onlineEvent : onlineEvents) {
            Duration durationOnTimestamps;
            if (now.isBefore(onlineEvent.getStartDate()) || onlineEvent.getStartDate().isEqual(now)) {
                durationOnTimestamps = new Duration(now, onlineEvent.getStartDate());


                if (durationOnTimestamps.isShorterThan(Duration.standardHours(1))) {
                    announces.add(String.format(EVENT_NOW, onlineEvent.getTitle(), getPeriodInStringFormat(new Period(onlineEvent.getEventLengthInSeconds()*1000))));
                }

                if ((durationOnTimestamps.isEqual(Duration.standardHours(1)) || durationOnTimestamps.isLongerThan(Duration.standardHours(1))) && durationOnTimestamps.isShorterThan(Duration.standardHours(4))) {
                    announces.add(String.format(EVENT_ETA, getPeriodInStringFormat(durationOnTimestamps.toPeriod()), onlineEvent.getTitle()));
                }
            }
        }
        return announces;
    }

    public String getPeriodInStringFormat(Period period) {
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

    private void sendMessageToOnline(List<String> messages) {
        for (String message : messages)
            sendMessageToOnline(message);
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
