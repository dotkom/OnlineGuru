package no.ntnu.online.onlineguru.plugin.plugins.calendar;


public class GoogleException extends Exception {
    public GoogleException(String message) {
        super(message);
    }

    public GoogleException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
