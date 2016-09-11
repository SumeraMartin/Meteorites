package com.sumera.meteorites.utils;

import android.content.Context;

import com.sumera.meteorites.MeteoritesApllication;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by martin on 11/09/16.
 */

public class RealmUtils {

    public static Realm getRealmInstance() {
        Context context = MeteoritesApllication.getContext();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
        try {
            return Realm.getInstance(context);
        } catch (RealmMigrationNeededException r) {
            Realm.deleteRealm(realmConfiguration);
            return Realm.getInstance(context);
        }
    }

    public static <T extends RealmObject> List<T> toList(RealmResults<T> results) {
        List<T> elements = new ArrayList<>(results.size());
        for (T element : results) {
            elements.add(element);
        }
        return elements;
    }


}
