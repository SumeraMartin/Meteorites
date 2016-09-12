package com.sumera.meteorites.model;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by martin on 05/09/16.
 */

public class MeteoritesProvider {

    private static final String TAG = MeteoritesProvider.class.getSimpleName();

    private static final String URL_TOKEN_HEADER_NAME = "X-App-Token";

    private static final String URL_TOKEN_HEADER_VALUE = "pSmeTIMmJVzTk5BqVOGa7An4C";

    private static final String URL = "https://data.nasa.gov/resource/y77d-th95.json";

    private static final String QUERY_PARAM = "$where";

    private static final String YEAR_QUERY = "year >= '%s'";

    private static final int DEFAULT_YEAR = 2011;

    private static final int REQUEST_TIMEOUT_IN_SECONDS = 5;

    public static List<Meteorite> getMeteorites() throws CannotProvideDataException {
        List<Meteorite> meteorites = getMeteoritesFromServer(DEFAULT_YEAR);
        MeteoritesCache.saveNewMeteorites(meteorites);
        return meteorites;
    }

    private static List<Meteorite> getMeteoritesFromServer(int sinceYear) throws CannotProvideDataException {
        Request request = createRequest(sinceYear);
        try {
            List<Meteorite> meteorites = getMeteoritesFromRequest(request);
            Meteorite.sortByMass(meteorites);
            return meteorites;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            throw new CannotProvideDataException("Can't access meteorites data from server");
        }
    }

    private static List<Meteorite> getMeteoritesFromRequest(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        return createFromJson(response.body().string());
    }

    private static List<Meteorite> createFromJson(String jsonData) {
        Type type = new TypeToken<List<Meteorite>>(){}.getType();
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new TimestampJsonDeserializer())
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create()
                .fromJson(jsonData, type);
    }

    private static Request createRequest(int sinceYear) {
        String query = String.format(YEAR_QUERY, "" + sinceYear + "-01-01T00:00:00.000");
        String url = URL + "?" + QUERY_PARAM + "=" + urlEncode(query);
        return new Request.Builder()
                .url(url)
                .addHeader(URL_TOKEN_HEADER_NAME, URL_TOKEN_HEADER_VALUE)
                .build();
    }

    private static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
            throw new CannotProvideDataException("Unsupported encoding operation during query creation");
        }
    }

}
