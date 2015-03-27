package com.flyingtoaster.bixe;

import com.flyingtoaster.bixe.FakeDataUtil;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.models.Station;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.ANDROID.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(com.flyingtoaster.bixe.BixeTestRunner.class)
public class StationDataSourceTest {

    @Test
    public void shouldRetrieveCorrectValues() {
        StationDataSource dataSource = getStationDataSource();
        Station stationToInsert = FakeDataUtil.getStation();
        List<Station> stationList;
        Station expectedStation;

        dataSource.open();
        dataSource.createStation(stationToInsert);

        stationList = dataSource.getAllStations();
        dataSource.close();

        assertThat(stationList).isNotEmpty();

        expectedStation = stationList.get(0);
        assertThat(expectedStation.getId()).isEqualTo(1);
        assertThat(expectedStation.getStationName()).isEqualTo("Yonge St. and Dundas St.");
        assertThat(expectedStation.getAvailableBikes()).isEqualTo(4);
        assertThat(expectedStation.getAvailableDocks()).isEqualTo(6);
        assertThat(expectedStation.getTotalDocks()).isEqualTo(10);
        assertThat(expectedStation.getLatitude()).isEqualTo(43.7);
        assertThat(expectedStation.getLongitude()).isEqualTo(79.4);
        assertThat(expectedStation.isInService()).isEqualTo(true);
    }

    private StationDataSource getStationDataSource() {
        StationDataSource dataSource = new StationDataSource(BixeApplication.getAppContext());
        return dataSource;
    }
}