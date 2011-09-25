package no.ntnu.online.onlineguru.plugin.plugins.calendar;

import no.fictive.irclib.event.container.Event;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;

/**
 * @author Roy Sindre Norangshol
 */
public class CalendarPlugin implements Plugin {
    private static final String DESCRIPTION = "Updates #Online with events happening on Online's Google Calendars";
    private Wand wand;
    private ScheduleAnnouncer scheduleAnnouncer;

    public CalendarPlugin() {
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        // not needed
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        // not needed
    }

    public void addWand(Wand wand) {
        this.wand = wand;
        scheduleAnnouncer = new ScheduleAnnouncer(this.wand, new GoogleCalendar());
    }
}
