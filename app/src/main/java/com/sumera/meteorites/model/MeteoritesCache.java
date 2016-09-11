package com.sumera.meteorites.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.sumera.meteorites.MeteoritesApllication;
import com.sumera.meteorites.utils.RealmUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by martin on 05/09/16.
 */

public class MeteoritesCache {

    private static final String PREFERENCES_NAME = "cache_preferences";

    private static final String LAST_DATABASE_UPDATE = "last_refresh";

    private static final long DATABASE_UPDATE_INTERVAL = 1 * 10000; // minute

    public static List<Meteorite> getMeteorites() {
        Realm realm = RealmUtils.getRealmInstance();
        RealmResults<Meteorite> realmMeteorites = realm.where(Meteorite.class).findAll();

        return RealmUtils.toList(realmMeteorites);
    }

    public static Meteorite getMeteoriteById(String id) {
        Realm realm = RealmUtils.getRealmInstance();
        return realm.where(Meteorite.class).equalTo("id", id).findFirst();
    }

    public static void saveNewMeteorites(List<Meteorite> meteorites) {
        removeOldData();
        removeUpdateTime();

        Realm realm = RealmUtils.getRealmInstance();

        realm.beginTransaction();
        realm.copyToRealm(meteorites);
        realm.commitTransaction();

        saveNewUpdateTime();
    }

    public static boolean isDatatabaseUpdated() {
        long lastDatabaseRefresh = getPreferences().getLong(LAST_DATABASE_UPDATE, 0);
        return System.currentTimeMillis() - lastDatabaseRefresh < DATABASE_UPDATE_INTERVAL;
    }

    public static boolean containsData() {
        Realm realm = RealmUtils.getRealmInstance();
        return realm.where(Meteorite.class).count() > 0;
    }

    public static long getLastDatabaseUpdateTimeInMiliseconds() {
        return getPreferences().getLong(LAST_DATABASE_UPDATE, 0);
    }

    private static void removeOldData() {
        Realm realm = RealmUtils.getRealmInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.clear(Meteorite.class);
            }
        });
    }

    private static void saveNewUpdateTime() {
        getPreferences()
                .edit()
                .putLong(LAST_DATABASE_UPDATE, System.currentTimeMillis())
                .apply();
    }

    private static void removeUpdateTime() {
        getPreferences()
                .edit()
                .remove(LAST_DATABASE_UPDATE)
                .apply();
    }

    private static SharedPreferences getPreferences() {
        Context context = MeteoritesApllication.getContext();
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

}
