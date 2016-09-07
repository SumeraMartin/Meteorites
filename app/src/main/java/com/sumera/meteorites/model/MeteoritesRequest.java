package com.sumera.meteorites.model;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by martin on 05/09/16.
 */

/**
 * RxAndroid wrapper for data provider.
 */
public class MeteoritesRequest {

    private Action0 m_onBeforeAsyncTaskAction = null;

    public void setActionBeforeAsyncTask(Action0 beforeAsyncTaskAction) {
        m_onBeforeAsyncTaskAction =  beforeAsyncTaskAction;
    }

    public Subscription getMeteorites(Action1<List<Meteorite>> onAction, Action1<Throwable> onError) {
        Observable<List<Meteorite>> observable = null;
        if(MeteoritesCache.isDatatabaseUpdated()) {
            observable = createCacheRequestObservable();
        } else {
            m_onBeforeAsyncTaskAction.call();
            observable = createNetworkRequestObservable();
        }

        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAction, onError);
    }

    private Observable<List<Meteorite>> createCacheRequestObservable() {
        return Observable.just(MeteoritesCache.getMeteorites());
    }

    private Observable<List<Meteorite>> createNetworkRequestObservable() {
        return Observable
                .fromCallable(new Callable<List<Meteorite>>() {
                    @Override
                    public List<Meteorite> call() throws Exception {
                        return MeteoritesProvider.getMeteorites();
                    }
                })
                .timeout(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io());
    }

}
