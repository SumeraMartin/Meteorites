package com.sumera.meteorites.model;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by martin on 05/09/16.
 */

/**
 * Wrapper for RxAndroid
 */
public class MeteoritesRequest {

    public static Subscription performAsyncRequest(Action1<List<Meteorite>> onAction, Action1<Throwable> onError) {
        return createObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAction, onError);
    }

    private static Observable<List<Meteorite>> createObservable() {
        return Observable.fromCallable(new Callable<List<Meteorite>>() {
            @Override
            public List<Meteorite> call() {
                return MeteoritesProvider.getMeteorites();
            }
        });
    }

}
