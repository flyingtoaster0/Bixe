package com.flyingtoaster.bixe.utils;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class StationFormatterTest {
    
    private StationFormatter mSubject;
    
    @Before
    public void setup() {
        mSubject = new StationFormatter();
    }

    @Test
    public void shouldReplaceSlashesAndBackSlashes() {
        String originalStationName = "Dundas St \\/ Yonge St";

        String expectedStationName = mSubject.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldReplaceSingleSlashes() {
        String originalStationName = "Dundas St / Yonge St";

        String expectedStationName = mSubject.removeSlashesAndBackSlashes(originalStationName);

        assertThat(expectedStationName).isEqualTo("Dundas St and Yonge St");
    }

    @Test
    public void shouldAddPeriodToStreet() {
        String originalStreetName = "Dundas St";
        String originalStreetNameWithCompass = "Queen St E";

        String expectedStreetName = mSubject.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = mSubject.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Dundas St.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Queen St. E.");
    }

    @Test
    public void shouldAddPeriodToAvenue() {
        String originalStreetName = "Brunswick Ave";
        String originalStreetNameWithCompass = "Fake Ave N";

        String expectedStreetName = mSubject.addPeriodToStreetNames(originalStreetName);
        String expectedStreetNameWithCompass = mSubject.addPeriodToStreetNames(originalStreetNameWithCompass);

        assertThat(expectedStreetName).isEqualTo("Brunswick Ave.");
        assertThat(expectedStreetNameWithCompass).isEqualTo("Fake Ave. N.");
    }

    @Test
    public void shouldCorrectStationNames() {
        String originalStationName = "Queen St W \\/ Spadina Ave";

        String expectedStationName = mSubject.addPeriodToStreetNames(originalStationName);
        expectedStationName = mSubject.removeSlashesAndBackSlashes(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }

    @Test
    public void shouldRemoveBracketsAndContents() {
        String originalStationName = "Queen St W \\/ Spadina Ave (That's Chinatown btw)";

        String expectedStationName = mSubject.addPeriodToStreetNames(originalStationName);
        expectedStationName = mSubject.removeSlashesAndBackSlashes(expectedStationName);
        expectedStationName = mSubject.removeBracketsAndContents(expectedStationName);

        assertThat(expectedStationName).isEqualTo("Queen St. W. and Spadina Ave.");
    }
}
