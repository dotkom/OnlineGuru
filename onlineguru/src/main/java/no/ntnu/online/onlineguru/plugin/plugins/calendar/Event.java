package no.ntnu.online.onlineguru.plugin.plugins.calendar;

import org.joda.time.DateTime;

/**
 * @author Roy Sindre Norangshol
 */
public class Event {
    public enum Type {KONTORVAKT, ONLINECALENDAR, KJELLERN}

    ;

    private String title;
    private DateTime startDate;
    private long eventLengthInSeconds;
    private Type eventType;

    public Event(Type eventType, String title, DateTime startDate, long eventLengthInSeconds) {
        this.eventType = eventType;
        this.title = title;
        this.startDate = startDate;
        this.eventLengthInSeconds = eventLengthInSeconds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public long getEventLengthInSeconds() {
        return eventLengthInSeconds;
    }

    public void setEventLengthInSeconds(long eventLengthInSeconds) {
        this.eventLengthInSeconds = eventLengthInSeconds;
    }

    public Type getEventType() {
        return eventType;
    }

    public void setEventType(Type eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", startDate=" + startDate +
                ", eventLengthInSeconds=" + eventLengthInSeconds +
                ", eventType=" + eventType +
                '}';
    }
}
