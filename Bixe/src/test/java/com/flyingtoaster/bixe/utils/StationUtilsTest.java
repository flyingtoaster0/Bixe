package com.flyingtoaster.bixe.utils;

import com.flyingtoaster.bixe.BixeTestRunner;
import com.flyingtoaster.bixe.FakeDataUtil;
import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.models.Station;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BixeTestRunner.class)
public class StationUtilsTest {

    @Test
    public void shouldReplaceSlashesAndBackSlashes() {
        String originalStationName = "Dundas St \\/ Yonge St";

        String expectedStationName = StationUtils.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldReplaceSingleSlashes() {
        String originalStationName = "Dundas St / Yonge St";

        String expectedStationName = StationUtils.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldAddPeriodToStreet() {
        String originalStreetName = "Dundas St";
        String originalStreetNameWithCompass = "Queen St E";

        String expectedStreetName = StationUtils.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = StationUtils.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Dundas St.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Queen St. E.");
    }

    @Test
    public void shouldAddPeriodToAvenue() {
        String originalStreetName = "Brunswick Ave";
        String originalStreetNameWithCompass = "Fake Ave N";

        String expectedStreetName = StationUtils.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = StationUtils.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Brunswick Ave.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Fake Ave. N.");
    }

    @Test
    public void shouldCorrectStationNames() {
        String originalStationName = "Queen St W \\/ Spadina Ave";

        String expectedStationName = StationUtils.addPeriodToStreetNames(originalStationName);
        expectedStationName = StationUtils.removeSlashesAndBackSlashes(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }

    @Test
    public void shouldRemoveBracketsAndContents() {
        String originalStationName = "Queen St W \\/ Spadina Ave (That's Chinatown btw)";

        String expectedStationName = StationUtils.addPeriodToStreetNames(originalStationName);
        expectedStationName = StationUtils.removeSlashesAndBackSlashes(expectedStationName);
        expectedStationName = StationUtils.removeBracketsAndContents(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }

    @Test
    public void shouldReturnCorrectGoogleMapsUrl() {
        double latitude = 12.345;
        double longitude = 54.321;

        String expectedGoogleMapsUrl = StationUtils.GOOGLE_MAPS_URL_PREFIX  + latitude + "+" + longitude;
        String actualGoogleMapsUrl = StationUtils.getLatLngUrl(latitude, longitude);

        assertThat(expectedGoogleMapsUrl).isEqualTo(actualGoogleMapsUrl);
    }

    @Test
    public void givenStation_shouldReturnCorrectShareText() {
        Station station = FakeDataUtil.getStation();

        String expectedShareText = station.getStationName() + "\n\n" + StationUtils.GOOGLE_MAPS_URL_PREFIX  + station.getLatitude() + "+" + station.getLongitude();
        String actualShareText = StationUtils.getShareText(station);

        assertThat(expectedShareText).isEqualTo(actualShareText);
    }

    @Test
    public void shouldReturnCorrectMarkerDrawable() {
        Station station = FakeDataUtil.getStation();
        station.setTotalDocks(10);

        station.setAvailableBikes(0);
        int expectedResourceId = R.drawable.ic_marker_0;
        int actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(1);
        expectedResourceId = R.drawable.ic_marker_1;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(2);
        expectedResourceId = R.drawable.ic_marker_1;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(3);
        expectedResourceId = R.drawable.ic_marker_2;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(4);
        expectedResourceId = R.drawable.ic_marker_2;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(5);
        expectedResourceId = R.drawable.ic_marker_3;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(6);
        expectedResourceId = R.drawable.ic_marker_3;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(7);
        expectedResourceId = R.drawable.ic_marker_4;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(8);
        expectedResourceId = R.drawable.ic_marker_4;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(9);
        expectedResourceId = R.drawable.ic_marker_5;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(10);
        expectedResourceId = R.drawable.ic_marker_5;
        actualResourceId = StationUtils.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);
    }
    @Test
    public void shouldReturnCorrectSelectedMarkerDrawable() {
        Station station = FakeDataUtil.getStation();
        station.setTotalDocks(10);

        station.setAvailableBikes(0);
        int expectedResourceId = R.drawable.ic_marker_0_selected;
        int actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(1);
        expectedResourceId = R.drawable.ic_marker_1_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(2);
        expectedResourceId = R.drawable.ic_marker_1_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(3);
        expectedResourceId = R.drawable.ic_marker_2_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(4);
        expectedResourceId = R.drawable.ic_marker_2_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(5);
        expectedResourceId = R.drawable.ic_marker_3_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(6);
        expectedResourceId = R.drawable.ic_marker_3_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(7);
        expectedResourceId = R.drawable.ic_marker_4_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(8);
        expectedResourceId = R.drawable.ic_marker_4_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(9);
        expectedResourceId = R.drawable.ic_marker_5_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(10);
        expectedResourceId = R.drawable.ic_marker_5_selected;
        actualResourceId = StationUtils.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);
    }

}
