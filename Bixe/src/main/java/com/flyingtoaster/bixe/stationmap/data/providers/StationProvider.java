package com.flyingtoaster.bixe.stationmap.data.providers;

import com.flyingtoaster.bixe.stationmap.data.database.StationDataSource;
import com.flyingtoaster.bixe.stationmap.models.Station;

import java.util.List;

import com.flyingtoaster.bixe.stationmap.data.clients.StationClient;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class StationProvider {

    private final StationClient mClient;
    private final StationDataSource mDataSource;

    @Inject
    public StationProvider(StationClient client, StationDataSource dataSource) {
        mClient = client;
        mDataSource = dataSource;
    }

    public Observable<List<Station>> getStations() {
        return Observable.create(new ObservableOnSubscribe<List<Station>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Station>> e) throws Exception {
                List<Station> stations = mClient.getStations();
                e.onNext(stations);
                e.onComplete();
            }
        });
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
        });
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
        });
    }
}
