package com.sumera.meteorites.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.sumera.meteorites.MeteoritesApllication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by martin on 05/09/16.
 */

public class MeteoritesCache {

    private static final String PREFERENCES_NAME = "cache_preferences";

    private static final String LAST_DATABASE_UPDATE = "last_refresh";

    private static final long DATABASE_UPDATE_INTERVAL = 5 * 10000; // minute

    public static List<Meteorite> getMeteorites() {
        Realm realm = getRealmInstance();
        RealmResults<Meteorite> realmMeteorites = realm.where(Meteorite.class).findAll();
        Meteorite[] meteorites = (Meteorite[]) realmMeteorites.toArray();
        return new ArrayList<>(Arrays.asList(meteorites));
    }

    public static Meteorite getMeteoriteById(String id) {
        Realm realm = getRealmInstance();
        return realm.where(Meteorite.class).equalTo("id", id).findFirst();
    }


    public static void saveMeteorites(List<Meteorite> meteorites) {
        removeOldData();

        Realm realm = getRealmInstance();

        realm.beginTransaction();
        realm.copyToRealm(meteorites);
        realm.commitTransaction();

        saveNewUpdateTime();
    }

    public static boolean isDatatabaseUpdated() {
        long lastDatabaseRefresh = getPreferences().getLong(LAST_DATABASE_UPDATE, 0);
        return System.currentTimeMillis() - lastDatabaseRefresh < DATABASE_UPDATE_INTERVAL;
    }

    public static long getLastDatabaseUpdateTimeInMiliseconds() {
        return getPreferences().getLong(LAST_DATABASE_UPDATE, 0);
    }

    private static void removeOldData() {
        Realm realm = getRealmInstance();
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

    private static Realm getRealmInstance() {
        Context context = MeteoritesApllication.getContext();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
        try {
            return Realm.getInstance(context);
        } catch (RealmMigrationNeededException r) {
            Realm.deleteRealm(realmConfiguration);
            return Realm.getInstance(context);
        }
    }

    private static SharedPreferences getPreferences() {
        Context context = MeteoritesApllication.getContext();
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

}
