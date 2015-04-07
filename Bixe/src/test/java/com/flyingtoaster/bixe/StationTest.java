package com.flyingtoaster.bixe;

import com.flyingtoaster.bixe.models.Station;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BixeTestRunner.class)
public class StationTest {

    @Test
    public void givenJsonObject_StationShouldHaveCorrectFields() {
        JsonObject stationJson = FakeDataUtil.getStationJsonObject();

        Station station = new Station(stationJson);

        assertThat(station.getId()).isEqualTo(1);
        assertThat(station.getStationName()).isEqualTo("Yonge St. and Dundas St.");
        assertThat(station.getAvailableBikes()).isEqualTo(4);
        assertThat(station.getAvailableDocks()).isEqualTo(6);
        assertThat(station.getTotalDocks()).isEqualTo(10);
        assertThat(station.getLatitude()).isEqualTo(43.7);
        assertThat(station.getLongitude()).isEqualTo(79.4);
        assertThat(station.isInService()).isEqualTo(true);
    }
}
