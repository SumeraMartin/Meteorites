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


/**
 * Deserialize floating_timestamp type without timezone information
 */
public class TimestampJsonDeserializer implements JsonDeserializer<Date> {

    private static final String TAG = TimestampJsonDeserializer.class.getSimpleName();

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String date = element.getAsString();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        formater.setTimeZone(tz);

        try {
            return formater.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse Date due to:", e);
            return null;
        }
    }
}