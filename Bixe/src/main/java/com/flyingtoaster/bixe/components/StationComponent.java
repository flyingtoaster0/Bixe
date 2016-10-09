package com.flyingtoaster.bixe.components;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.activities.MainActivity;
import com.flyingtoaster.bixe.clients.StationClient;
import com.flyingtoaster.bixe.modules.StationModule;
import com.flyingtoaster.bixe.scopes.StationScope;

import dagger.Component;

@StationScope
@Component(modules = {StationModule.class})
public interface StationComponent {
//    void inject(BixeApplication application);
    void inject(MainActivity activity);
    StationClient stationClient();
}
