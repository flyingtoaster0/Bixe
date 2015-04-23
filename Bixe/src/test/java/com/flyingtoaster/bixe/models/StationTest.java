package com.flyingtoaster.bixe.models;

import com.flyingtoaster.bixe.BixeTestRunner;
import com.flyingtoaster.bixe.FakeDataUtil;
import com.flyingtoaster.bixe.R;
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

    @Test
    public void shouldReturnCorrectMarkerDrawable() {
        Station station = FakeDataUtil.getStation();
        station.setTotalDocks(10);

        station.setAvailableBikes(0);
        int expectedResourceId = R.drawable.ic_marker_0;
        int actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(1);
        expectedResourceId = R.drawable.ic_marker_1;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(2);
        expectedResourceId = R.drawable.ic_marker_1;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(3);
        expectedResourceId = R.drawable.ic_marker_2;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(4);
        expectedResourceId = R.drawable.ic_marker_2;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(5);
        expectedResourceId = R.drawable.ic_marker_3;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(6);
        expectedResourceId = R.drawable.ic_marker_3;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(7);
        expectedResourceId = R.drawable.ic_marker_4;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(8);
        expectedResourceId = R.drawable.ic_marker_4;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(9);
        expectedResourceId = R.drawable.ic_marker_5;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(10);
        expectedResourceId = R.drawable.ic_marker_5;
        actualResourceId = station.getMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);
    }
    @Test
    public void shouldReturnCorrectSelectedMarkerDrawable() {
        Station station = FakeDataUtil.getStation();
        station.setTotalDocks(10);

        station.setAvailableBikes(0);
        int expectedResourceId = R.drawable.ic_marker_0_selected;
        int actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(1);
        expectedResourceId = R.drawable.ic_marker_1_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(2);
        expectedResourceId = R.drawable.ic_marker_1_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(3);
        expectedResourceId = R.drawable.ic_marker_2_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(4);
        expectedResourceId = R.drawable.ic_marker_2_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(5);
        expectedResourceId = R.drawable.ic_marker_3_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(6);
        expectedResourceId = R.drawable.ic_marker_3_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(7);
        expectedResourceId = R.drawable.ic_marker_4_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(8);
        expectedResourceId = R.drawable.ic_marker_4_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(9);
        expectedResourceId = R.drawable.ic_marker_5_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(10);
        expectedResourceId = R.drawable.ic_marker_5_selected;
        actualResourceId = station.getSelectedMarkerResourceId();
        assertThat(actualResourceId).isEqualTo(expectedResourceId);
    }
}
