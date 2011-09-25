package no.ntnu.online.onlineguru.plugin.plugins.calendar;


import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Roy Sindre Norangshol
 */
public class ScheduleAnnouncer {
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
        timer = new Timer();


        loadEvents();

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
    }


    private void loadEvents() {
        DateTime today = new DateTime();
        //today = today.withDate(2011, 9, 28);

        // ime(int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond)
        eventsMap.put(Event.Type.KONTORVAKT,
                calendar.getEvent(Event.Type.KONTORVAKT, today.withTime(0, 0, 0, 0), today.withTime(23, 59, 59, 0))
        );
        eventsMap.put(Event.Type.ONLINECALENDAR,
                calendar.getEvent(Event.Type.ONLINECALENDAR, today.withTime(0, 0, 0, 0), today.withTime(23, 59, 59, 0))
        );

    }

    private void startPreschedulerForHourlyScheduler() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startHourlyScheduler();
            }
        }, getSecondsLeftForNewHour() * 1000);
    }

    private void startHourlyScheduler() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.warn(" running hourly cronjob from calendar plugin");
                checkForNewDay();
                doHourlyAnnounces();

            }
        }, 0, 3600 * 1000); // repeat every hour

    }

    private void doHourlyAnnounces() {
        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        now = now.withTime(now.getHourOfDay(), 0, 0, 0); // removes minutes,seconds

        List<Event> kontorVakter = eventsMap.get(Event.Type.KONTORVAKT);
        for (Event kontorVakt : kontorVakter) {
            Duration durationOnTimestamps;
            if (now.isBefore(kontorVakt.getStartDate()))
                durationOnTimestamps = new Duration(now, kontorVakt.getStartDate());
            else
                durationOnTimestamps = new Duration(kontorVakt.getStartDate(), now);


            if (durationOnTimestamps.isShorterThan(Duration.standardHours(1))) {
                sendMessageToOnline(String.format("[Kontorvakt] %s skal nå være kontorvakt!", kontorVakt.getTitle()));
            }

            if ((durationOnTimestamps.isEqual(Duration.standardHours(1)) || durationOnTimestamps.isLongerThan(Duration.standardHours(1))) && durationOnTimestamps.isShorterThan(Duration.standardHours(2))) {
                sendMessageToOnline(String.format("[Kontorvakt ETA] %stil %s har kontorvakt!", getPeriodInStringFormat(durationOnTimestamps.toPeriod()), kontorVakt.getTitle()));
            }

        }

        // Denne er ikke helt riktig, men riktig nok for klokketimer for no :p
        List<Event> onlineEvents = eventsMap.get(Event.Type.ONLINECALENDAR);
        for (Event onlineEvent : onlineEvents) {
            Duration durationOnTimestamps;
            if (now.isBefore(onlineEvent.getStartDate()))
                durationOnTimestamps = new Duration(now, onlineEvent.getStartDate());
            else
                durationOnTimestamps = new Duration(onlineEvent.getStartDate(), now);


            if (durationOnTimestamps.isShorterThan(Duration.standardHours(1))) {
                sendMessageToOnline(String.format("[Event start] %s", onlineEvent.getTitle()));
            }

            if ((durationOnTimestamps.isEqual(Duration.standardHours(1)) || durationOnTimestamps.isLongerThan(Duration.standardHours(1))) && durationOnTimestamps.isShorterThan(Duration.standardHours(4))) {
                sendMessageToOnline(String.format("[Event ETA] %stil %s starter!", getPeriodInStringFormat(durationOnTimestamps.toPeriod()), onlineEvent.getTitle()));
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
        return now.getMinuteOfHour();
    }

    private boolean isGoodmorningTime() {
        DateTime now = new DateTime();
        return now.getHourOfDay() == 18;
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
