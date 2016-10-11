package com.flyingtoaster.bixe.stationmap.ui;


import com.flyingtoaster.bixe.stationmap.models.Station;

import java.util.List;

public class StationMapContract {

    public interface View {
        void showLoading();
        void hideLoading();
        void updateMarkers(List<Station> stations);
        void updateSelectedStationView(String stationName, String availableBikes, String availableDocks);
    }

    public interface Presenter {
        void attachView(View view);
        void detachView();
        void refreshStations();
        void onStationSelect(Station station);
    }
}
