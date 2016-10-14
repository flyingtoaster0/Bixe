package com.flyingtoaster.bixe.stationmap.data;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.BuildConfig;
import com.flyingtoaster.bixe.stationmap.data.database.StationDataSource;
import com.flyingtoaster.bixe.stationmap.models.Station;

import org.fest.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StationDataSourceTest {

    private static final String STATION_NAME = "Yonge St. and Dundas St.";
    private static final int AVAILABLE_BIKES = 4;
    private static final int AVAILABLE_DOCKS = 6;
    private static final int TOTAL_DOCKS = 10;
    private static final double LATITUDE = 43.7;
    private static final double LONGITUDE = 79.4;
    private static final int ID = 1;

    @Mock
    private Station mStation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(mStation.getId()).thenReturn(ID);
        when(mStation.getStationName()).thenReturn(STATION_NAME);
        when(mStation.getAvailableBikes()).thenReturn(AVAILABLE_BIKES);
        when(mStation.getAvailableDocks()).thenReturn(AVAILABLE_DOCKS);
        when(mStation.getTotalDocks()).thenReturn(TOTAL_DOCKS);
        when(mStation.getLatitude()).thenReturn(LATITUDE);
        when(mStation.getLongitude()).thenReturn(LONGITUDE);
    }

    @Test
    public void shouldRetrieveStationFields() {
        StationDataSource dataSource = getStationDataSource();
        List<Station> stationList;
        Station actualStation;

        dataSource.open();
        dataSource.putStation(mStation);
        stationList = dataSource.getAllStations();
        dataSource.close();

        assertThat(stationList).hasSize(1);
        actualStation = stationList.get(0);
        assertThat(actualStation.getId()).isEqualTo(ID);
        assertThat(actualStation.getStationName()).isEqualTo(STATION_NAME);
        assertThat(actualStation.getAvailableBikes()).isEqualTo(AVAILABLE_BIKES);
        assertThat(actualStation.getAvailableDocks()).isEqualTo(AVAILABLE_DOCKS);
        assertThat(actualStation.getTotalDocks()).isEqualTo(TOTAL_DOCKS);
        assertThat(actualStation.getLatitude()).isEqualTo(LATITUDE);
        assertThat(actualStation.getLongitude()).isEqualTo(LONGITUDE);
    }

    @Test
    public void shouldRetrieveCorrectValuesAfterUpdate() {
        StationDataSource dataSource = getStationDataSource();
        List<Station> stationList;
        Station actualStation;

        dataSource.open();
        dataSource.putStation(mStation);
        when(mStation.getAvailableBikes()).thenReturn(8);
        when(mStation.getAvailableDocks()).thenReturn(2);
        dataSource.putStation(mStation);
        stationList = dataSource.getAllStations();
        dataSource.close();

        assertThat(stationList).hasSize(1);
        actualStation = stationList.get(0);
        assertThat(actualStation.getId()).isEqualTo(ID);
        assertThat(actualStation.getStationName()).isEqualTo(STATION_NAME);
        assertThat(actualStation.getAvailableBikes()).isEqualTo(8);
        assertThat(actualStation.getAvailableDocks()).isEqualTo(2);
        assertThat(actualStation.getTotalDocks()).isEqualTo(TOTAL_DOCKS);
        assertThat(actualStation.getLatitude()).isEqualTo(LATITUDE);
        assertThat(actualStation.getLongitude()).isEqualTo(LONGITUDE);
    }

    @Test
    public void shouldInsertMultipleStations() {
        Station station1 = mock(Station.class);
        Station station2 = mock(Station.class);
        when(station1.getId()).thenReturn(1);
        when(station2.getId()).thenReturn(2);
        StationDataSource dataSource = getStationDataSource();
        ArrayList<Station> stationsToInsert = Lists.newArrayList(station1, station2);
        List<Station> actualStationList;

        dataSource.open();
        dataSource.putStations(stationsToInsert);
        actualStationList = dataSource.getAllStations();
        dataSource.close();

        assertThat(actualStationList).hasSize(2);
    }

    private StationDataSource getStationDataSource() {
        StationDataSource dataSource = new StationDataSource(BixeApplication.getApplication());
        return dataSource;
    }
}