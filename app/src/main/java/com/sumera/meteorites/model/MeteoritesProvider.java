package com.sumera.meteorites.model;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by martin on 05/09/16.
 */

public class MeteoritesProvider {

    private static final String TAG = MeteoritesProvider.class.getSimpleName();

    private static final String URL = "https://data.nasa.gov/resource/y77d-th95.json";

    private static final String QUERY_PARAM = "$where";

    private static final String YEAR_QUERY = "year >= '%s'";

    private static final int DEFAULT_YEAR = 2011;

    public static List<Meteorite> getMeteorites() throws CannotProvideDataException {
        return getMeteoritesFromYear(DEFAULT_YEAR);
    }

    private static List<Meteorite> getMeteoritesFromYear(int year) throws CannotProvideDataException {
        String query = String.format(YEAR_QUERY, "" + year + "-01-01T00:00:00.000");
        query = urlEncode(query);
        String url = URL + "?" + QUERY_PARAM + "=" + query;

        Request request = new Request.Builder().url(url).build();

        try {
            List<Meteorite> meteorites = getMeteorites(request);
            Meteorite.sortByMass(meteorites);
            return meteorites;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            throw new CannotProvideDataException("Can't access meteorites data from server");
        }
    }

    private static List<Meteorite> getMeteorites(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        Type type = new TypeToken<List<Meteorite>>(){}.getType();

        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new TimestampJsonDeserializer())
                .create()
                .fromJson(response.body().string(), type);
    }

    private static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }

}
