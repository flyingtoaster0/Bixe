package com.flyingtoaster.bixe.providers;

import com.flyingtoaster.bixe.models.Station;
import com.google.gson.JsonArray;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import clients.StationClient;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.internal.operators.observable.ObservableJust;

public class StationProvider {

    private final StationClient mClient;

    public StationProvider(StationClient client) {
        mClient = client;
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
}
