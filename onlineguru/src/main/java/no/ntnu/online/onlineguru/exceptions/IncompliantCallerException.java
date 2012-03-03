package no.ntnu.online.onlineguru.exceptions;

/**
 * @author Håvard Slettvold
 */


public class IncompliantCallerException extends Exception {

    String error;

    public IncompliantCallerException() {
        super();             // call superclass constructor
        error = "unknown";
    }

    public IncompliantCallerException(String err) {
        super(err);     // call super class constructor
        error = err;  // save message
    }

    public String getError() {
        return error;
    }

}
