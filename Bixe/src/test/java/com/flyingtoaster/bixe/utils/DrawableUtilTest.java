package com.flyingtoaster.bixe.utils;

import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.models.Station;

import org.junit.Ignore;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DrawableUtilTest {

    @Ignore("TODO: Test drawables in MainActivity instead")
    @Test
    public void shouldReturnCorrectMarkerDrawable() {
        Station station = mock(Station.class);
        station.setTotalDocks(10);

        station.setAvailableBikes(0);
        int expectedResourceId = R.drawable.ic_marker_0;
        int actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(1);
        expectedResourceId = R.drawable.ic_marker_1;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(2);
        expectedResourceId = R.drawable.ic_marker_1;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(3);
        expectedResourceId = R.drawable.ic_marker_2;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(4);
        expectedResourceId = R.drawable.ic_marker_2;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(5);
        expectedResourceId = R.drawable.ic_marker_3;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(6);
        expectedResourceId = R.drawable.ic_marker_3;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(7);
        expectedResourceId = R.drawable.ic_marker_4;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(8);
        expectedResourceId = R.drawable.ic_marker_4;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(9);
        expectedResourceId = R.drawable.ic_marker_5;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(10);
        expectedResourceId = R.drawable.ic_marker_5;
        actualResourceId = DrawableUtil.getMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);
    }
    @Test
    public void shouldReturnCorrectSelectedMarkerDrawable() {
        Station station = mock(Station.class);
        station.setTotalDocks(10);

        station.setAvailableBikes(0);
        int expectedResourceId = R.drawable.ic_marker_0_selected;
        int actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(1);
        expectedResourceId = R.drawable.ic_marker_1_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(2);
        expectedResourceId = R.drawable.ic_marker_1_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(3);
        expectedResourceId = R.drawable.ic_marker_2_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(4);
        expectedResourceId = R.drawable.ic_marker_2_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(5);
        expectedResourceId = R.drawable.ic_marker_3_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(6);
        expectedResourceId = R.drawable.ic_marker_3_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(7);
        expectedResourceId = R.drawable.ic_marker_4_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(8);
        expectedResourceId = R.drawable.ic_marker_4_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(9);
        expectedResourceId = R.drawable.ic_marker_5_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);

        station.setAvailableBikes(10);
        expectedResourceId = R.drawable.ic_marker_5_selected;
        actualResourceId = DrawableUtil.getSelectedMarkerResourceId(station);
        assertThat(actualResourceId).isEqualTo(expectedResourceId);
    }
}