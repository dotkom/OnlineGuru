package no.ntnu.online.onlineguru.plugin.plugins.calendar;

import com.google.gson.*;
import org.joda.time.Instant;

import java.lang.reflect.Type;

/**
 * https://sites.google.com/site/gson/gson-type-adapters-for-common-classes-1
 * @author Roy Sindre Norangshol
 */
public class InstantTypeConverter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    @Override
    public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.getMillis());
    }

    @Override
    public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return new Instant(json.getAsLong());
    }
}

