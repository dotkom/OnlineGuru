package no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel;

import no.ntnu.online.onlineguru.plugin.plugins.calendar.Event;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roy Sindre Norangshol
 */
public class Item {
    public String id;
    public String title;
    protected String details;

    private static String[] shortMonths = new DateFormatSymbols(new Locale("no")).getShortMonths();

    private static final Pattern EVENT_START_PATTERN = Pattern.compile(".*Første start.*(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2}).*");
    private static final Pattern EVENT_START_SPECIAL = Pattern.compile(".*Når: .* (\\d{1,2})\\. (\\w+)\\.? (\\d{4}) (\\d{2}):(\\d{2}) til (\\d{2}):(\\d{2}).*");
    private static final Pattern EVENT_START_SPECIAL2 = Pattern.compile(".*Når: .* (\\d{1,2})\\. (\\w+)\\.? (\\d{4}) (\\d{2}):(\\d{2}) til .* (\\d{1,2})\\. (\\w+)\\. (\\d{4}) (\\d{2}):(\\d{2}).*");
    private static final Pattern EVENT_LENGTH = Pattern.compile(".*Varighet: (\\d+).*");

    static Logger logger = Logger.getLogger(Item.class);

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", details='" + details + '\'' +
                '}';
    }

    public String getDetails() {
        return details.replaceAll("<br />", " ").replaceAll("\\n", " ");
    }

    public DateTime getEventStartTime() {
        Matcher matcher = EVENT_START_PATTERN.matcher(getDetails());
        if (matcher.matches()) {
            // Dirty hack for setting proper today's date ..
            DateTime now = new DateTime();
            return now.withTime(Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)),
                    0, 0);


/*            return new DateTime(Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)),
                    0, 0);*/
        } else {
            // try special event
            matcher = EVENT_START_SPECIAL.matcher(getDetails());
            Matcher matcher2 = EVENT_START_SPECIAL2.matcher(getDetails());
            if (matcher.matches())
                return getEventStartFromGetEventLength(matcher);
            else if (matcher2.matches())
                return getEventStartFromGetEventLength(matcher2);
        }
        return null;
    }

    public int getEventLength() {
        Matcher matcher = EVENT_LENGTH.matcher(getDetails());
        if (matcher.matches())
            return Integer.parseInt(matcher.group(1));
        else {

            matcher = EVENT_START_SPECIAL2.matcher(getDetails());  // Når: fre. 21. okt. 2011 19:00 til søn. 23. okt. 2011 16:00  CEST
            if (matcher.matches()) {
                DateTime eventStart = getEventStartFromGetEventLength(matcher);

                DateTime eventStop = new DateTime(Integer.parseInt(matcher.group(8)),
                        convertMonthlyNameToMonthNumber(matcher.group(7)),
                        Integer.parseInt(matcher.group(6)),
                        Integer.parseInt(matcher.group(9)),
                        Integer.parseInt(matcher.group(10)),
                        0, 0);

                if (eventStop.isEqual(eventStart) || eventStop.isAfter(eventStart)) {
                    Interval intervalBetweenEvents = new Interval(eventStart, eventStop);

                    return ((int) (intervalBetweenEvents.toDurationMillis() / 1000));
                } else {
                    logger.error(String.format("Error with Item : eventStop is before eventStart!\nstart: %s\nend: %s", eventStart.toString(), eventStop.toString()));
                }
            } else {
                matcher = EVENT_START_SPECIAL.matcher(getDetails()); // Når: fre. 21. okt. 2011 19:00 til 23:00  CEST
                if (matcher.matches()) {
                    DateTime eventStart = getEventStartFromGetEventLength(matcher);

                    DateTime eventStop = new DateTime(Integer.parseInt(matcher.group(3)),
                            convertMonthlyNameToMonthNumber(matcher.group(2)),
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(6)),
                            Integer.parseInt(matcher.group(7)),
                            0, 0); // assumes same day.

                    if (eventStop.isEqual(eventStart) || eventStop.isAfter(eventStart)) {
                        Interval intervalBetweenEvents = new Interval(eventStart, eventStop);

                        return ((int) (intervalBetweenEvents.toDurationMillis() / 1000));
                    } else {
                        logger.error(String.format("Error with Item : eventStop is before eventStart!\nstart: %s\nend: %s", eventStart.toString(), eventStop.toString()));
                    }
                }
            }

        }
        logger.error(String.format("Error with Item : %s\n details: %s", this, getDetails()));
        return -1;
    }

    private DateTime getEventStartFromGetEventLength(Matcher matcher) {
        return new DateTime(Integer.parseInt(matcher.group(3)),
                convertMonthlyNameToMonthNumber(matcher.group(2)),
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(4)),
                Integer.parseInt(matcher.group(5)),
                0, 0);
    }

    public int convertMonthlyNameToMonthNumber(String monthName) {
        for (int i = 0; i < shortMonths.length; i++) {
            if (shortMonths[i].equalsIgnoreCase(monthName))
                return i + 1;
        }
        return -1;
    }

    public Event convertToEvent(Event.Type type) {
        return new Event(type, title, getEventStartTime(), getEventLength());
    }
}
