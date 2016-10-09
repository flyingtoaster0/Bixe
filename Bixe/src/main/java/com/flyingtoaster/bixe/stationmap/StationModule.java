package com.flyingtoaster.bixe.stationmap;

import android.content.Context;

import com.flyingtoaster.bixe.BixeApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class StationModule {

    @StationScope
    @Provides
    Context provideContext() {
        return BixeApplication.getAppContext();
    }
}
