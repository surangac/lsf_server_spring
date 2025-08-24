package com.dfn.lsf.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.toString()); // ISO-8601 format (yyyy-MM-dd)
    }

//    @Override
//    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//            throws JsonParseException {
//        return LocalDate.parse(json.getAsString()); // Parse ISO-8601 format
//    }

    @Override
    public LocalDate deserialize(final JsonElement jsonElement,
                                 final Type type,
                                 final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDate.parse(jsonElement.getAsString()); // Parse ISO-8601 format
    }
}
