package no.ntnu.online.onlineguru.plugin.plugins.calendar;

import com.google.gson.*;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Type;
import java.text.*;

/**
 * https://sites.google.com/site/gson/gson-type-adapters-for-common-classes-1
 *
 * @author Roy Sindre Norangshol
 */
public class LocalDateTimeTypeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public JsonElement serialize(LocalDateTime src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return getLocalDateTimeFromString(json.getAsString());

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static LocalDateTime getLocalDateTimeFromString(String timeStamp) {

        LocalDateTime departureTime = null;
        try {
            departureTime = new LocalDateTime(dateFormatter.parse(timeStamp));
        } catch (ParseException e) {
            return null;
        }
        return departureTime;
    }


}
