package com.flyingtoaster.bixe.stationmap;

import android.content.Context;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.stationmap.data.providers.StationProvider;
import com.flyingtoaster.bixe.stationmap.ui.StationMapPresenter;
import com.flyingtoaster.bixe.stationmap.ui.map.StationMapFragment;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestStationModule {

    @StationScope
    @Provides
    Context provideContext() {
        return BixeApplication.getApplication();
    }

    @Provides
    StationProvider provideStationProvider() {
        return mock(StationProvider.class);
    }

    @Provides
    StationMapPresenter provideStationMapPresenter() {
        return mock(StationMapPresenter.class);
    }

    @Provides
    StationMapFragment provideStationMapFragment() {
        return mock(StationMapFragment.class);
    }
}
