package com.flyingtoaster.bixe.stationmap.ui;


import com.flyingtoaster.bixe.stationmap.models.Station;

import java.util.List;

public class StationMapContract {

    public interface View {
        void updateMarkers(List<Station> stations);
        void showLoading();
        void hideLoading();
    }

    public interface Presenter {
        void attachView(View view);
        void detachView();
        void refreshStations();
    }
}
