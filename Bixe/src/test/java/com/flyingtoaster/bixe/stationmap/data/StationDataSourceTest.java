package com.flyingtoaster.bixe.stationmap.data;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.stationmap.data.database.StationDataSource;
import com.flyingtoaster.bixe.stationmap.models.Station;

import org.fest.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(com.flyingtoaster.bixe.BixeTestRunner.class)
public class StationDataSourceTest {

    @Test
    public void shouldRetrieveCorrectValues() {
        StationDataSource dataSource = getStationDataSource();
        Station stationToInsert = mock(Station.class);
        List<Station> stationList;
        Station expectedStation;

        dataSource.open();
        dataSource.putStation(stationToInsert);

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
    }

    @Test
    public void shouldRetrieveCorrectValuesAfterUpdate() {
        StationDataSource dataSource = getStationDataSource();
        Station stationToInsert = mock(Station.class);
        List<Station> stationList;
        Station expectedStation;

        dataSource.open();
        dataSource.putStation(stationToInsert);

        stationToInsert.setAvailableBikes(8);
        stationToInsert.setAvailableDocks(2);
        dataSource.putStation(stationToInsert);

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
    }

    @Test
    public void shouldInsertMultipleStations() {
        StationDataSource dataSource = getStationDataSource();
        ArrayList<Station> stationsToInsert = Lists.newArrayList(mock(Station.class), mock(Station.class));
        List<Station> expectedStationList;

        dataSource.open();
        dataSource.putStations(stationsToInsert);
        expectedStationList = dataSource.getAllStations();
        dataSource.close();

        assertThat(expectedStationList).hasSize(2);
    }

    private StationDataSource getStationDataSource() {
        StationDataSource dataSource = new StationDataSource(BixeApplication.getApplication());
        return dataSource;
    }
}