package no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel;

import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class Data {
    public String title;
    public String details;
    public String updated;
    public List<Item> items;

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", updated='" + updated + '\'' +
                ", items=" + items +
                '}';
    }
}
