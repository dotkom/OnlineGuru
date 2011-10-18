package no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel;

import no.ntnu.online.onlineguru.plugin.plugins.calendar.Event;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roy Sindre Norangshol
 */
public class Item {
    public String id;
    public String title;
    private String details;

    private static final Pattern EVENT_START_PATTERN = Pattern.compile(".*Første start.*(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2}).*");
    private static final Pattern EVENT_START_SPECIAL = Pattern.compile(".*Når: \\w+\\. (\\d{1,2})\\. (\\w+)\\. (\\d{4}) (\\d{2}):(\\d{2}) til (\\d{2}):(\\d{2}).*");
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
            if (matcher.matches()) {
                return new DateTime(Integer.parseInt(matcher.group(3)),
                        convertMonthlyNameToMonthNumber(matcher.group(2)),
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)),
                        0, 0);

            }
        }
        return null;
    }

    public int getEventLength() {
        Matcher matcher = EVENT_LENGTH.matcher(getDetails());
        if (matcher.matches())
            return Integer.parseInt(matcher.group(1));
        else {
            matcher = EVENT_START_SPECIAL.matcher(getDetails());
            if (matcher.matches()) {
                DateTime eventStart = new DateTime(Integer.parseInt(matcher.group(3)),
                        convertMonthlyNameToMonthNumber(matcher.group(2)),
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)),
                        0, 0);

                DateTime eventStop = new DateTime(Integer.parseInt(matcher.group(3)),
                        convertMonthlyNameToMonthNumber(matcher.group(2)),
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(6)),
                        Integer.parseInt(matcher.group(7)),
                        0, 0);

                if (eventStop.isEqual(eventStart) || eventStop.isAfter(eventStart)) {
                    Interval intervalBetweenEvents = new Interval(eventStart, eventStop);

                    return ((int) (intervalBetweenEvents.toDurationMillis() / 1000));
                } else {
                    logger.error(String.format("Error with Item : eventStop is before eventStart!\nstart: %s\nend: %s", eventStart.toString(), eventStop.toString()));
                }
            }

        }
        logger.error(String.format("Error with Item : %s\n details: %s", this, getDetails()));
        return -1;
    }

    private int convertMonthlyNameToMonthNumber(String monthName) {
        return 9;
    }

    public Event convertToEvent(Event.Type type) {
        return new Event(type, title, getEventStartTime(), getEventLength());
    }
}
