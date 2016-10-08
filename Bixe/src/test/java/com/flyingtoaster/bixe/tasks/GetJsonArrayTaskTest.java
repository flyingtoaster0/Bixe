package com.flyingtoaster.bixe.tasks;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.BixeTestRunner;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.models.Station;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BixeTestRunner.class)
public class GetJsonArrayTaskTest {
    @Test
    public void shouldInsertStationsToDatabase() {
        String expectedFirstStationName = "Jarvis St. and Carleton St.";
        MockGetJsonArrayTask jsonArrayTask = new MockGetJsonArrayTask();
        StationDataSource dataSource = new StationDataSource(BixeApplication.getAppContext());
        dataSource.open();

        jsonArrayTask.execute();
        List<Station> stationList = dataSource.getAllStations();
        Station firstStation = stationList.get(0);

        assertThat(stationList).isNotEmpty();
        assertThat(firstStation.getStationName()).isEqualTo(expectedFirstStationName);

        dataSource.close();
    }
}
