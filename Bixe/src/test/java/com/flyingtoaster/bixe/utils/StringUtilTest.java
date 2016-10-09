package com.flyingtoaster.bixe.utils;

import com.flyingtoaster.bixe.BixeTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(BixeTestRunner.class)
public class StringUtilTest {

    @Test
    public void shouldReplaceSlashesAndBackSlashes() {
        String originalStationName = "Dundas St \\/ Yonge St";

        String expectedStationName = StringUtil.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldReplaceSingleSlashes() {
        String originalStationName = "Dundas St / Yonge St";

        String expectedStationName = StringUtil.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldAddPeriodToStreet() {
        String originalStreetName = "Dundas St";
        String originalStreetNameWithCompass = "Queen St E";

        String expectedStreetName = StringUtil.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = StringUtil.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Dundas St.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Queen St. E.");
    }

    @Test
    public void shouldAddPeriodToAvenue() {
        String originalStreetName = "Brunswick Ave";
        String originalStreetNameWithCompass = "Fake Ave N";

        String expectedStreetName = StringUtil.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = StringUtil.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Brunswick Ave.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Fake Ave. N.");
    }

    @Test
    public void shouldCorrectStationNames() {
        String originalStationName = "Queen St W \\/ Spadina Ave";

        String expectedStationName = StringUtil.addPeriodToStreetNames(originalStationName);
        expectedStationName = StringUtil.removeSlashesAndBackSlashes(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }

    @Test
    public void shouldRemoveBracketsAndContents() {
        String originalStationName = "Queen St W \\/ Spadina Ave (That's Chinatown btw)";

        String expectedStationName = StringUtil.addPeriodToStreetNames(originalStationName);
        expectedStationName = StringUtil.removeSlashesAndBackSlashes(expectedStationName);
        expectedStationName = StringUtil.removeBracketsAndContents(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }

    @Test
    public void shouldReturnCorrectGoogleMapsUrl() {
        double latitude = 12.345;
        double longitude = 54.321;

        String expectedGoogleMapsUrl = StringUtil.GOOGLE_MAPS_URL_PREFIX  + latitude + "+" + longitude;
        String actualGoogleMapsUrl = StringUtil.getLatLngUrl(latitude, longitude);

        assertThat(expectedGoogleMapsUrl).isEqualTo(actualGoogleMapsUrl);
    }
}
