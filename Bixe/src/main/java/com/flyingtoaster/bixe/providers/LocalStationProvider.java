package com.flyingtoaster.bixe.providers;


import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.models.Station;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class LocalStationProvider {

    private final StationDataSource mDataSource;

    public LocalStationProvider(StationDataSource dataSource) {
        mDataSource = dataSource;
    }

    public Observable<List<Station>> getStations() {
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

    public Observable<List<Station>> putStations(final List<Station> stations) {
        return Observable.create(new ObservableOnSubscribe<List<Station>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Station>> e) throws Exception {
                mDataSource.open();
                mDataSource.putStations(stations);
                mDataSource.close();
                e.onComplete();
            }
        });
    }
}
