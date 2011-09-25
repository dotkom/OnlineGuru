package no.ntnu.online.onlineguru.plugin.plugins.calendar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel.Calendar;
import no.ntnu.online.onlineguru.plugin.plugins.calendar.jsonmodel.Item;
import org.apache.abdera.Abdera;
import org.apache.abdera.parser.Parser;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.joda.time.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class GoogleCalendar {
    private static final HashMap<Event.Type, String> availableCalendars = new HashMap<Event.Type, String>() {
        {
            put(Event.Type.KONTORVAKT, "https://www.google.com/calendar/feeds/b72fgdhuv6g5mpoqa0bdvj095k%%40group.calendar.google.com/public/basic?start-min=%s&start-max=%s&alt=jsonc");
            put(Event.Type.ONLINECALENDAR, "https://www.google.com/calendar/feeds/54v6g4v6r46qi4asf7lh5j9pcs%%40group.calendar.google.com/public/basic?start-min=%s&start-max=%s&alt=jsonc");
        }
    };


    static Logger logger = Logger.getLogger(GoogleCalendar.class);
    private Gson gson;

    public GoogleCalendar() {
        GsonBuilder builder = new GsonBuilder();
        gson = builder
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .setPrettyPrinting()
                .create();
/*
        try {
            HttpResponse httpResponse = httpGet(new URI(urlToPull), null);
            Calendar data = gson.fromJson(readString(httpResponse.getEntity().getContent()), Calendar.class);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
  **/

    }

    public static void main(String[] args) {
        new GoogleCalendar();
    }

    public List<Event> getEvent(Event.Type eventType) {
        DateTime today = new DateTime();
        return getEvent(eventType, today.withTime(0, 0, 0, 0), today.withTime(23, 59, 59, 0));
    }

    public List<Event> getEvent(Event.Type eventType, DateTime fromDate, DateTime toDate) {
        HttpResponse httpResponse = null;
        List<Event> events = new ArrayList<Event>();

        DateTime today = new DateTime();

        if (!isSameDay(fromDate, toDate))
            throw new IllegalArgumentException("Different from and to time is not supported (yet) ..");

        try {
            httpResponse = httpGet(new URI(String.format(availableCalendars.get(eventType), fromDate.toString("yyyy-MM-dd'T'HH:mm:ss"), toDate.toString("yyyy-MM-dd'T'HH:mm:ss"))), null);

            Calendar data = gson.fromJson(readString(httpResponse.getEntity().getContent()), Calendar.class);

            if (data.data.items != null) {
                for (Item event : data.data.items) {
                    Event convertedEvent = event.convertToEvent(eventType);
                    if (!isSameDay(fromDate, today)) {
                        // increase days if requesting events which is not today
                        // has to be done since Event objects shows only startTime of when the objects were created int he calendar

                        today = today.withTime(0, 0, 0, 0); // reset today time to make it only count days.

                        Interval intervalBetweenFromDateAndToday;

                        if (fromDate.isAfter(today))
                            intervalBetweenFromDateAndToday = new Interval(today, fromDate);
                        else
                            intervalBetweenFromDateAndToday = new Interval(fromDate, today);

                        Period period = intervalBetweenFromDateAndToday.toPeriod();
                        //if (period.getDays() == 0)


                        convertedEvent.setStartDate(
                                convertedEvent.getStartDate().plus(period)
                        );
                    }

                    events.add(convertedEvent);
                }
            }

        } catch (IOException e) {
            logger.warn(String.format("io exception occured when loading event type %s", eventType), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return events;

    }


    private boolean isSameDay(DateTime fromDate, DateTime today) {
        return today.getYear() == fromDate.getYear()
                && today.getMonthOfYear() == fromDate.getMonthOfYear()
                && today.getDayOfWeek() == fromDate.getDayOfWeek();
    }


    private HttpResponse httpGet(URI uri, UsernamePasswordCredentials creds) throws IOException, URISyntaxException {

        DefaultHttpClient httpClient = new DefaultHttpClient();

        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(uri.getHost(), uri.getPort()),
                creds);

        HttpGet httpGet = new HttpGet(uri);

        //httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("User-Agent", "OnlineGuru/0.1");

        return httpClient.execute(httpGet);
    }

    public static String readString(InputStream inputStream) throws UnsupportedEncodingException, IOException {
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();

        reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }


        return stringBuilder.toString();
    }
}
