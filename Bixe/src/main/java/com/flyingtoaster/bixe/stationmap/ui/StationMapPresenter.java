package com.flyingtoaster.bixe.stationmap.ui;


import com.flyingtoaster.bixe.stationmap.data.providers.StationProvider;
import com.flyingtoaster.bixe.stationmap.models.Station;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class StationMapPresenter implements StationMapContract.Presenter {

    final StationProvider mStationProvider;

    StationMapContract.View mView;

    @Inject
    public StationMapPresenter(StationProvider stationProvider) {
        mStationProvider = stationProvider;
    }

    @Override
    public void attachView(StationMapContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void refreshStations() {
        mView.showLoading();

        mStationProvider.getStationsLocal()
                .flatMap(new Function<List<Station>, ObservableSource<List<Station>>>() {
                    @Override
                    public ObservableSource<List<Station>> apply(List<Station> stations) throws Exception {
                        mView.updateMarkers(stations);
                        return mStationProvider.getStations();
                    }
                })
                .flatMap(new Function<List<Station>, ObservableSource<List<Station>>>() {
                    @Override
                    public ObservableSource<List<Station>> apply(List<Station> stations) throws Exception {
                        mView.updateMarkers(stations);
                        return mStationProvider.putStationsLocal(stations);
                    }
                })
                .subscribe(new Consumer<List<Station>>() {
                    @Override
                    public void accept(List<Station> stations) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideLoading();
                    }
                });
    }
}
