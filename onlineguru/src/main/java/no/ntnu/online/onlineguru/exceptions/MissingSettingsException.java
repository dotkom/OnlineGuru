package no.ntnu.online.onlineguru.exceptions;

/**
 * @author Håvard Slettvold
 */


public class MissingSettingsException extends Exception {

    String error;

    public MissingSettingsException() {
        super();             // call superclass constructor
        error = "unknown";
    }

    public MissingSettingsException(String err) {
        super(err);     // call super class constructor
        error = err;  // save message
    }

    public String getError() {
        return error;
    }

}
