package com.flyingtoaster.bixe.stationmap;

import android.content.Context;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.stationmap.ui.StationMapPresenter;
import com.flyingtoaster.bixe.stationmap.ui.map.StationMapFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class StationModule {

    @StationScope
    @Provides
    Context provideContext() {
        return BixeApplication.getApplication();
    }

    @Provides
    StationMapFragment provideStationMapFragment() {
        return new StationMapFragment();
    }
}
