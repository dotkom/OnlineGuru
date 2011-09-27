package no.ntnu.online.onlineguru.plugin.plugins.calendar;

import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class FakeGoogleCalendar extends GoogleCalendar {
    private List<Event> officeHours;
    private List<Event> onlineEvents;

    public FakeGoogleCalendar(List<Event> officeHours, List<Event> onlineEvents) {
        this.officeHours = officeHours;
        this.onlineEvents = onlineEvents;
    }

    @Override
    public List<Event> getEvent(Event.Type eventType, DateTime fromDate, DateTime toDate) throws GoogleException {
        if (eventType.equals(Event.Type.KONTORVAKT))
            return officeHours;
        else if (eventType.equals(Event.Type.ONLINECALENDAR))
            return onlineEvents;
        else
            throw new IllegalArgumentException("No valid event type chosen.");
    }

    @Override
    List<Event> fetchCalendar(Event.Type eventType, List<Event> orignalFallbackEvents) {
        final DateTime today = new DateTime();
        int eventsFailLoadingCounter = 0;

        while (eventsFailLoadingCounter < MAX_EVENT_LOAD_TRIES) {
            try {
                List<Event> events = getEvent(eventType, today.withTime(0, 0, 0, 0), today.withTime(23, 59, 59, 0));
                return events;
            } catch (GoogleException e) {
                logger.warn(e);
                eventsFailLoadingCounter++;
            }
        }
        //scheduleAnnouncer.sendMessageToOnline(String.format("[google internal error] Vi klarte ikke å lese kalenderen til %s - google gir oss server internal error :-(", eventType.toString()));

        return orignalFallbackEvents;
    }
}
