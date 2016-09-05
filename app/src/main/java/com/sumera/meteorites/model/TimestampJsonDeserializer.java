package com.sumera.meteorites.model;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by martin on 05/09/16.
 */

public class TimestampJsonDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String date = element.getAsString();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss"); // Quoted "Z" to indicate UTC, no timezone offset
        formater.setTimeZone(tz);

        try {
            return formater.parse(date);
        } catch (ParseException e) {
            Log.e("SUMERA", "Failed to parse Date due to:", e);
            return null;
        }
    }
}