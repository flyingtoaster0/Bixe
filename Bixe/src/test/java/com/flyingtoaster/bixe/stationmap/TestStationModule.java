package com.flyingtoaster.bixe.stationmap;

import android.content.Context;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.stationmap.data.providers.StationProvider;
import com.flyingtoaster.bixe.stationmap.ui.StationMapPresenter;
import com.flyingtoaster.bixe.stationmap.ui.map.StationMapFragment;
import com.flyingtoaster.bixe.utils.StationFormatter;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

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
    StationFormatter provideStationFormatter() {
        return mock(StationFormatter.class);
    }

    @Provides
    StationMapPresenter provideStationMapPresenter() {
        return mock(StationMapPresenter.class);
    }

    @Provides
    StationMapFragment provideStationMapFragment() {
        return spy(new StationMapFragment());
    }
}
