package com.flyingtoaster.bixe;


import com.flyingtoaster.bixe.stationmap.DaggerTestStationComponent;
import com.flyingtoaster.bixe.stationmap.StationComponent;
import com.flyingtoaster.bixe.stationmap.TestStationComponent;
import com.flyingtoaster.bixe.stationmap.TestStationModule;

public class TestBixeApplication extends BixeApplication {

    private TestStationComponent mStationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mStationComponent = DaggerTestStationComponent.builder()
                .testStationModule(new TestStationModule())
                .build();
    }

    @Override
    public StationComponent getStationComponent() {
        return mStationComponent;
    }
}
