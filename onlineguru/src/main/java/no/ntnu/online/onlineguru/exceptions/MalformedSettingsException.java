package no.ntnu.online.onlineguru.exceptions;

/**
 * @author Håvard Slettvold
 */


public class MalformedSettingsException extends Exception {

    String error;

    public MalformedSettingsException() {
        super();             // call superclass constructor
        error = "unknown";
    }

    public MalformedSettingsException(String err) {
        super(err);     // call super class constructor
        error = err;  // save message
    }

    public String getError() {
        return error;
    }

}
