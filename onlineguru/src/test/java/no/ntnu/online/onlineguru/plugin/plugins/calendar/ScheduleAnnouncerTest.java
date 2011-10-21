package no.ntnu.online.onlineguru.plugin.plugins.calendar;

import no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel.Item;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * @author Roy Sindre Norangshol
 */
public class ScheduleAnnouncerTest {
    private List<Event> officeHours;
    private List<Event> onlineEvents;
    private ScheduleAnnouncer scheduleAnnouncer;
    private Item item;

    @Before
    public void setUp() {

        DateTime now = new DateTime();

        officeHours = new ArrayList<Event>(Arrays.asList(
                new Event[]{
                        new Event(Event.Type.KONTORVAKT, "Kontorvakt9", now.withTime(9, 0, 0, 0), 3600L),
                        new Event(Event.Type.KONTORVAKT, "Kontorvakt10", now.withTime(10, 0, 0, 0), 3600L),
                        // onlineEvent møte. 2 timer
                        new Event(Event.Type.KONTORVAKT, "Kontorvakt13", now.withTime(13, 0, 0, 0), 3600L),
                        new Event(Event.Type.KONTORVAKT, "Kontorvakt14", now.withTime(14, 0, 0, 0), 3600L)

                }));

        onlineEvents = new ArrayList<Event>(Arrays.asList(
                new Event[]{
                        new Event(Event.Type.ONLINECALENDAR, "HS-møte", now.withTime(11, 0, 0, 0), 3600 * 2L),
                        new Event(Event.Type.ONLINECALENDAR, "Troll-møte", now.withTime(17, 0, 0, 0), 3600),
                        new Event(Event.Type.ONLINECALENDAR, "Dotkom-møte", now.withTime(18, 0, 0, 0), 3600 * 6L)
                }
        ));

        scheduleAnnouncer = new ScheduleAnnouncer(new FakeGoogleCalendar(officeHours, onlineEvents));
        item = new Item();
    }

    @Test
    public void testForCorrectSizeOfAnnounces() {
        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        now = now.withTime(9, 0, 0, 0); // removes minutes,seconds

        List<String> announces = scheduleAnnouncer.getHourlyAnnounces(now);
        //System.out.println(Arrays.toString(announces.toArray()));
        assertEquals(3, announces.size());

        announces = null;
        now = now.withTime(10, 0, 0, 0);
        announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(2, announces.size());

        announces = null;
        now = now.withTime(11, 0, 0, 0);
        announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(1, announces.size());

        announces = null;
        now = now.withTime(12, 0, 0, 0);
        announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(1, announces.size());

        announces = null;
        now = now.withTime(15, 0, 0, 0);
        announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(2, announces.size());

        announces = null;
        now = now.withTime(19, 0, 0, 0);
        announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(0, announces.size());

    }


    @Test
    public void testCorrectAnnouncesFor9oClock() {

        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        now = now.withTime(9, 0, 0, 0); // removes minutes,seconds

        List<String> announces = scheduleAnnouncer.getHourlyAnnounces(now);

        assertEquals(String.format(ScheduleAnnouncer.OFFICEHOUR_NOW, "Kontorvakt9"), announces.get(0));
        assertEquals(String.format(ScheduleAnnouncer.OFFICEHOUR_ETA, scheduleAnnouncer.getPeriodInStringFormat(new Period(now, officeHours.get(1).getStartDate())), "Kontorvakt10"), announces.get(1));
        assertEquals(String.format(ScheduleAnnouncer.EVENT_ETA, scheduleAnnouncer.getPeriodInStringFormat(new Period(now, onlineEvents.get(0).getStartDate())), "HS-møte"), announces.get(2));


    }

    @Test
    public void testCorrectAnnouncesFor15OClock() {
        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        now = now.withTime(15, 0, 0, 0); // removes minutes,seconds

        List<String> announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(String.format(ScheduleAnnouncer.EVENT_ETA, scheduleAnnouncer.getPeriodInStringFormat(new Period(now, onlineEvents.get(1).getStartDate())), "Troll-møte"), announces.get(0));
        assertEquals(String.format(ScheduleAnnouncer.EVENT_ETA, scheduleAnnouncer.getPeriodInStringFormat(new Period(now, onlineEvents.get(2).getStartDate())), "Dotkom-møte"), announces.get(1));
    }

    @Test
    public void testNoAnnounces() {
        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        now = now.withTime(19, 0, 0, 0); // removes minutes,seconds

        List<String> announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(0, announces.size());
    }

    @Test
    public void testHsMoteNowAnnounce() {
        DateTime now = new DateTime();
        //now = now.withDate(2011, 9, 28);
        //now = now.withTime(9, 0, 0, 0);
        now = now.withTime(11, 0, 0, 0); // removes minutes,seconds

        List<String> announces = scheduleAnnouncer.getHourlyAnnounces(now);
        assertEquals(String.format(ScheduleAnnouncer.EVENT_NOW, onlineEvents.get(0).getTitle()), announces.get(0));

    }

    @Test
    public void testMonthlyNameToNumber() {
        String[] shortMonths = new DateFormatSymbols(new Locale("no")).getShortMonths();

        assertEquals(1, item.convertMonthlyNameToMonthNumber("jan"));
        assertEquals(2, item.convertMonthlyNameToMonthNumber("feb"));
        assertEquals(3, item.convertMonthlyNameToMonthNumber("mar"));
        assertEquals(4, item.convertMonthlyNameToMonthNumber("apr"));
        assertEquals(5, item.convertMonthlyNameToMonthNumber("mai"));
        assertEquals(6, item.convertMonthlyNameToMonthNumber("jun"));
        assertEquals(7, item.convertMonthlyNameToMonthNumber("jul"));
        assertEquals(8, item.convertMonthlyNameToMonthNumber("aug"));
        assertEquals(9, item.convertMonthlyNameToMonthNumber("sep"));
        assertEquals(10, item.convertMonthlyNameToMonthNumber("okt"));
        assertEquals(11, item.convertMonthlyNameToMonthNumber("nov"));
        assertEquals(12, item.convertMonthlyNameToMonthNumber("des"));

    }

}
