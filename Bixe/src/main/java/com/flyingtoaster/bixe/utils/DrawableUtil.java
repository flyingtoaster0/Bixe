package com.flyingtoaster.bixe.utils;


import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.stationmap.models.Station;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class DrawableUtil {

    public static int getMarkerResourceId(Station station) {
        int percent = (int)(((float) station.getAvailableBikes() / (float) station.getTotalDocks())*100);
        int markerResourceId;

        if (percent == 0) {
            markerResourceId = R.drawable.ic_marker_0;
        } else if (percent <= 20) {
            markerResourceId = R.drawable.ic_marker_1;
        } else if (percent <= 40) {
            markerResourceId = R.drawable.ic_marker_2;
        } else if (percent <= 60) {
            markerResourceId = R.drawable.ic_marker_3;
        } else if (percent <= 80) {
            markerResourceId = R.drawable.ic_marker_4;
        } else {
            markerResourceId = R.drawable.ic_marker_5;
        }

        return markerResourceId;
    }

    public static int getSelectedMarkerResourceId(Station station) {
        int percent = (int)(((float) station.getAvailableBikes() / (float) station.getTotalDocks())*100);
        int markerResourceId;

        if (percent == 0) {
            markerResourceId = R.drawable.ic_marker_0_selected;
        } else if (percent <= 20) {
            markerResourceId = R.drawable.ic_marker_1_selected;
        } else if (percent <= 40) {
            markerResourceId = R.drawable.ic_marker_2_selected;
        } else if (percent <= 60) {
            markerResourceId = R.drawable.ic_marker_3_selected;
        } else if (percent <= 80) {
            markerResourceId = R.drawable.ic_marker_4_selected;
        } else {
            markerResourceId = R.drawable.ic_marker_5_selected;
        }

        return markerResourceId;
    }

    public static BitmapDescriptor getMarkerBitmapDescriptor(Station station) {
        BitmapDescriptor bitmapDescriptor = null;
        int markerResource = getMarkerResourceId(station);

        bitmapDescriptor = BitmapDescriptorFactory.fromResource(markerResource);

        return bitmapDescriptor;
    }

    public static BitmapDescriptor getSelectedMarkerBitmapDescriptor(Station station) {
        BitmapDescriptor bitmapDescriptor = null;
        int markerResource = getSelectedMarkerResourceId(station);

        bitmapDescriptor = BitmapDescriptorFactory.fromResource(markerResource);

        return bitmapDescriptor;
    }
}
