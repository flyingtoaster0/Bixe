package com.flyingtoaster.bixe.stationmap;

import com.flyingtoaster.bixe.schedulers.ObservableModule;
import com.flyingtoaster.bixe.stationmap.ui.StationMapActivity;
import com.flyingtoaster.bixe.stationmap.data.clients.StationClient;
import com.flyingtoaster.bixe.stationmap.ui.StationMapPresenter;

import dagger.Component;

@StationScope
@Component(modules = {StationModule.class, ObservableModule.class})
public interface StationComponent {
    void inject(StationMapActivity activity);
    StationClient stationClient();
    StationMapPresenter stationPresenter();
}
