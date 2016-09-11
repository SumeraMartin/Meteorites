package com.sumera.meteorites.model;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by martin on 05/09/16.
 */

/**
 * RxAndroid wrapper for data provider.
 */
public class MeteoritesRequest implements Callable<List<Meteorite>> {

    @Override
    public List<Meteorite> call() throws Exception {
        return MeteoritesProvider.getMeteorites();
    }

    public static Subscription getMeteorites(Action1<List<Meteorite>> onAction, Action1<Throwable> onError) {
        if(MeteoritesCache.isDatatabaseUpdated()) {
            return createCacheObservable(onAction, onError);
        }

        return createNetworkObservable(onAction, onError);
    }

    public static Subscription getMeteoritesFromNetwork(Action1<List<Meteorite>> onAction, Action1<Throwable> onError) {
        return createNetworkObservable(onAction, onError);
    }

    private static Subscription createCacheObservable(Action1<List<Meteorite>> onAction, Action1<Throwable> onError) {
        return Observable.just(MeteoritesCache.getMeteorites())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAction, onError);
    }

    private static Subscription createNetworkObservable(Action1<List<Meteorite>> onAction, Action1<Throwable> onError) {
        return Observable.fromCallable(new Callable<List<Meteorite>>() {
                    @Override
                    public List<Meteorite> call() throws Exception {
                        return MeteoritesProvider.getMeteorites();
                    }
                })
                .timeout(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAction, onError);
    }
}
