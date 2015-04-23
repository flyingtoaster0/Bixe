package com.flyingtoaster.bixe.utils;

import com.flyingtoaster.bixe.BixeTestRunner;
import com.flyingtoaster.bixe.FakeDataUtil;
import com.flyingtoaster.bixe.models.Station;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BixeTestRunner.class)
public class StringUtilsTest {

    @Test
    public void shouldReplaceSlashesAndBackSlashes() {
        String originalStationName = "Dundas St \\/ Yonge St";

        String expectedStationName = StringUtils.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldReplaceSingleSlashes() {
        String originalStationName = "Dundas St / Yonge St";

        String expectedStationName = StringUtils.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldAddPeriodToStreet() {
        String originalStreetName = "Dundas St";
        String originalStreetNameWithCompass = "Queen St E";

        String expectedStreetName = StringUtils.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = StringUtils.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Dundas St.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Queen St. E.");
    }

    @Test
    public void shouldAddPeriodToAvenue() {
        String originalStreetName = "Brunswick Ave";
        String originalStreetNameWithCompass = "Fake Ave N";

        String expectedStreetName = StringUtils.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = StringUtils.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Brunswick Ave.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Fake Ave. N.");
    }

    @Test
    public void shouldCorrectStationNames() {
        String originalStationName = "Queen St W \\/ Spadina Ave";

        String expectedStationName = StringUtils.addPeriodToStreetNames(originalStationName);
        expectedStationName = StringUtils.removeSlashesAndBackSlashes(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }

    @Test
    public void shouldRemoveBracketsAndContents() {
        String originalStationName = "Queen St W \\/ Spadina Ave (That's Chinatown btw)";

        String expectedStationName = StringUtils.addPeriodToStreetNames(originalStationName);
        expectedStationName = StringUtils.removeSlashesAndBackSlashes(expectedStationName);
        expectedStationName = StringUtils.removeBracketsAndContents(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }

    @Test
    public void shouldReturnCorrectGoogleMapsUrl() {
        double latitude = 12.345;
        double longitude = 54.321;

        String expectedGoogleMapsUrl = StringUtils.GOOGLE_MAPS_URL_PREFIX  + latitude + "+" + longitude;
        String actualGoogleMapsUrl = StringUtils.getLatLngUrl(latitude, longitude);

        assertThat(expectedGoogleMapsUrl).isEqualTo(actualGoogleMapsUrl);
    }

    @Test
    public void givenStation_shouldReturnCorrectShareText() {
        Station station = FakeDataUtil.getStation();

        String expectedShareText = station.getStationName() + "\n\n" + StringUtils.GOOGLE_MAPS_URL_PREFIX  + station.getLatitude() + "+" + station.getLongitude();
        String actualShareText = StringUtils.getShareText(station);

        assertThat(expectedShareText).isEqualTo(actualShareText);
    }
}
