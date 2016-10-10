package com.flyingtoaster.bixe.stationmap;

import com.flyingtoaster.bixe.stationmap.ui.StationMapActivity;
import com.flyingtoaster.bixe.stationmap.data.clients.StationClient;

import dagger.Component;

@StationScope
@Component(modules = {StationModule.class})
public interface StationComponent {
    void inject(StationMapActivity activity);
    StationClient stationClient();
}
