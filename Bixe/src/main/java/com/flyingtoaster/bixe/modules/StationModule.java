package com.flyingtoaster.bixe.modules;

import android.content.Context;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.scopes.StationScope;

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
