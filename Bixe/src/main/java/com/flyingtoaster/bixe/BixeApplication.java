package com.flyingtoaster.bixe;

import android.app.Application;

import com.flyingtoaster.bixe.stationmap.DaggerStationComponent;
import com.flyingtoaster.bixe.stationmap.StationComponent;
import com.flyingtoaster.bixe.stationmap.StationModule;

public class BixeApplication extends Application {
    private static BixeApplication sApplication;

    private StationComponent mStationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        mStationComponent = DaggerStationComponent.builder()
                .stationModule(new StationModule())
                .build();
    }

    public static BixeApplication getApplication() {
        return sApplication;
    }

    public StationComponent getStationComponent() {
        return mStationComponent;
    }
}
