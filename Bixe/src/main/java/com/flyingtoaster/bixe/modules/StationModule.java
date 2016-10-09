package com.flyingtoaster.bixe.modules;

import com.flyingtoaster.bixe.clients.StationClient;
import com.flyingtoaster.bixe.scopes.StationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class StationModule {

    @StationScope
    @Provides
    StationClient provideStationClient() {
        return new StationClient();
    }
}
