package no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel;

/**
 * Created by IntelliJ IDEA.
 * User: rockj
 * Date: 9/26/11
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Error {
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

