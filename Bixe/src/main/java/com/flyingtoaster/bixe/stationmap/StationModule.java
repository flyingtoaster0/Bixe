package com.flyingtoaster.bixe.stationmap;

import android.content.Context;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.stationmap.ui.map.StationMapFragment;
import com.flyingtoaster.bixe.utils.StationFormatter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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

    @Provides
    StationFormatter provideStationFormatter() {
        return new StationFormatter();
    }

    @Provides
    GoogleApiClient provideGoogleApiClient(StationMapFragment stationMapFragment) {
        return new GoogleApiClient.Builder(stationMapFragment.getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(stationMapFragment)
                .build();
    }
}
