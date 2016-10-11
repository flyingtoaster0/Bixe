package com.flyingtoaster.bixe.stationmap.data.providers;

import com.flyingtoaster.bixe.schedulers.IOScheduler;
import com.flyingtoaster.bixe.schedulers.MainThreadScheduler;
import com.flyingtoaster.bixe.stationmap.data.database.StationDataSource;
import com.flyingtoaster.bixe.stationmap.models.Station;

import java.util.List;

import com.flyingtoaster.bixe.stationmap.data.clients.StationClient;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;

public class StationProvider {

    private final StationClient mClient;
    private final StationDataSource mDataSource;
    @MainThreadScheduler
    private final Scheduler mMainThreadScheduler;
    @IOScheduler
    private final Scheduler mIOScheduler;

    @Inject
    public StationProvider(StationClient client, StationDataSource dataSource,
                           @MainThreadScheduler Scheduler mainThreadScheduler, @IOScheduler Scheduler ioScheduler) {
        mClient = client;
        mDataSource = dataSource;
        mMainThreadScheduler = mainThreadScheduler;
        mIOScheduler = ioScheduler;
    }

    public Observable<List<Station>> getStations() {
        return Observable.create(new ObservableOnSubscribe<List<Station>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Station>> e) throws Exception {
                List<Station> stations = mClient.getStations();
                e.onNext(stations);
                e.onComplete();
            }
        }).subscribeOn(mIOScheduler).observeOn(mMainThreadScheduler);
    }

    public Observable<List<Station>> getStationsLocal() {
        return Observable.create(new ObservableOnSubscribe<List<Station>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Station>> e) throws Exception {
                mDataSource.open();
                List<Station> stations = mDataSource.getAllStations();
                mDataSource.close();
                e.onNext(stations);
                e.onComplete();
            }
        }).subscribeOn(mIOScheduler).observeOn(mMainThreadScheduler);
    }

    public Observable<List<Station>> putStationsLocal(final List<Station> stations) {
        return Observable.create(new ObservableOnSubscribe<List<Station>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Station>> e) throws Exception {
                mDataSource.open();
                mDataSource.putStations(stations);
                mDataSource.close();
                e.onNext(stations);
                e.onComplete();
            }
        }).subscribeOn(mIOScheduler).observeOn(mMainThreadScheduler);
    }
}
