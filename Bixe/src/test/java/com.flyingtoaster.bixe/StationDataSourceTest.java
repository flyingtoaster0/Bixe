package com.flyingtoaster.bixe;

import com.flyingtoaster.bixe.FakeDataUtil;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.models.Station;

import org.fest.assertions.api.Assertions;
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

    @Test
    public void shouldRetrieveCorrectValuesAfterUpdate() {
        StationDataSource dataSource = getStationDataSource();
        Station stationToInsert = FakeDataUtil.getStation();
        List<Station> stationList;
        Station expectedStation;

        dataSource.open();
        dataSource.createStation(stationToInsert);

        stationToInsert.setAvailableBikes(8);
        stationToInsert.setAvailableDocks(2);
        dataSource.createStation(stationToInsert);

        stationList = dataSource.getAllStations();
        dataSource.close();

        assertThat(stationList).isNotEmpty();

        expectedStation = stationList.get(0);
        assertThat(expectedStation.getId()).isEqualTo(1);
        assertThat(expectedStation.getStationName()).isEqualTo("Yonge St. and Dundas St.");
        assertThat(expectedStation.getAvailableBikes()).isEqualTo(8);
        assertThat(expectedStation.getAvailableDocks()).isEqualTo(2);
        assertThat(expectedStation.getTotalDocks()).isEqualTo(10);
        assertThat(expectedStation.getLatitude()).isEqualTo(43.7);
        assertThat(expectedStation.getLongitude()).isEqualTo(79.4);
        assertThat(expectedStation.isInService()).isEqualTo(true);
    }

    @Test
    public void shouldInsertMultipleStations() {
        StationDataSource dataSource = getStationDataSource();
        ArrayList<Station> stationsToInsert = FakeDataUtil.getStations();
        List<Station> expectedStationList;
        Station firstExpectedStation;
        Station secondExpectedStation;

        dataSource.open();
        dataSource.createStations(stationsToInsert);

        expectedStationList = dataSource.getAllStations();
        dataSource.close();

        assertThat(expectedStationList).isNotEmpty();

        firstExpectedStation = expectedStationList.get(0);
        assertThat(firstExpectedStation.getId()).isEqualTo(1);
        assertThat(firstExpectedStation.getStationName()).isEqualTo("Yonge St. and Dundas St.");

        secondExpectedStation = expectedStationList.get(1);
        assertThat(secondExpectedStation.getId()).isEqualTo(2);
        assertThat(secondExpectedStation.getStationName()).isEqualTo("Yonge St. and Adelaide St.");
    }

    private StationDataSource getStationDataSource() {
        StationDataSource dataSource = new StationDataSource(BixeApplication.getAppContext());
        return dataSource;
    }
}