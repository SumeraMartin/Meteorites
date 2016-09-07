package com.sumera.meteorites;

import android.app.Application;

/**
 * Created by martin on 06/09/16.
 */

public class MeteoritesApllication extends Application {

    private static MeteoritesApllication instance;

    public static MeteoritesApllication getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}