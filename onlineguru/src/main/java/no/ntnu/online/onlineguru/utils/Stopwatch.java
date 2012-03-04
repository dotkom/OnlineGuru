package no.ntnu.online.onlineguru.utils;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * @author Håvard Slettvold
 */


public class Stopwatch {

    private DateTime start;

    public Stopwatch() {
    }

    public Stopwatch(DateTime start) {
        this.start = start;
    }

    public void reset() {
        start = new DateTime();
    }

    public int getTimeInSeconds() {
        if (start == null) {
            return 0;
        }
        return new Duration(start, new DateTime()).toStandardSeconds().getSeconds();
    }

    public long getTimeInMilliSeconds() {
        return new Duration(start, new DateTime()).getMillis();
    }

}
