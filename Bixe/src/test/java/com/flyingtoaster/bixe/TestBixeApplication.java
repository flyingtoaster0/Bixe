package com.flyingtoaster.bixe;


import com.flyingtoaster.bixe.schedulers.TestObservableModule;
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
                .testObservableModule(new TestObservableModule())
                .build();
    }

    @Override
    public StationComponent getStationComponent() {
        return mStationComponent;
    }
}
