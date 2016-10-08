package com.flyingtoaster.bixe.utils;


import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.models.Station;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class StationUtils {

    public static final String GOOGLE_MAPS_URL_PREFIX = "http://maps.google.com/maps?q=";

    public static String removeSlashesAndBackSlashes(String original) {
        String outputName = "";

        if (original != null) {
            outputName = original.replaceAll(" ?\\\\/ ?", " and ");
            outputName = outputName.replaceAll(" ?/ ?", " and ");
        }

        return outputName;
    }

    public static String addPeriodToStreetNames(String original) {
        String outputName = "";

        if (original != null) {
            outputName = original.replaceAll("Ave$", "Ave.");
            outputName = outputName.replaceAll("Ave ", "Ave. ");
            outputName = outputName.replaceAll("St$", "St.");
            outputName = outputName.replaceAll("St ", "St. ");
            outputName = outputName.replaceAll("Cres", "Cres.");
            outputName = outputName.replaceAll("Cres ", "Cres. ");
            outputName = outputName.replaceAll(" N$", " N.");
            outputName = outputName.replaceAll(" N ", " N. ");
            outputName = outputName.replaceAll(" E$", " E.");
            outputName = outputName.replaceAll(" E ", " E. ");
            outputName = outputName.replaceAll(" S$", " S.");
            outputName = outputName.replaceAll(" S ", " S. ");
            outputName = outputName.replaceAll(" W$", " W.");
            outputName = outputName.replaceAll(" W ", " W. ");
        }

        return outputName;
    }

    public static String removeBracketsAndContents(String original) {
        String output = "";

        output = original.replaceAll(" ?\\(.*\\)", "");

        return output;
    }

    public static String getShareText(Station station) {
        StringBuilder builder = new StringBuilder();
        double latitude = station.getLatitude();
        double longitude = station.getLongitude();

        builder.append(station.getStationName());
        builder.append("\n\n");
        builder.append(getLatLngUrl(latitude, longitude));

        return builder.toString();
    }

    public static String getLatLngUrl(double latitude, double longitude) {
        return GOOGLE_MAPS_URL_PREFIX + latitude + "+" + longitude;
    }

    private static String fixStationName(String stationName) {
        String fixedStationName = "";

        if (stationName != null) {
            fixedStationName = StationUtils.addPeriodToStreetNames(stationName);
            fixedStationName = StationUtils.removeSlashesAndBackSlashes(fixedStationName);
            fixedStationName = StationUtils.removeBracketsAndContents(fixedStationName);
        }

        return fixedStationName;
    }

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

    public static int getSelectedMarkerResourceId(Station station) {int percent = (int)(((float) station.getAvailableBikes() / (float) station.getTotalDocks())*100);
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
