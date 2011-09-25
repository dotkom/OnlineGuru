package no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel;



/**
 * @author Roy Sindre Norangshol
 */
public class Calendar {
    public String apiVersion;
    public Data data;

    @Override
    public String toString() {
        return "Calendar{" +
                "apiVersion='" + apiVersion + '\'' +
                ", data=" + data +
                '}';
    }
}
