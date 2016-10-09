package com.flyingtoaster.bixe.utils;


import com.flyingtoaster.bixe.stationmap.models.Station;

public class StringUtil {

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

    public static String fixStationName(String stationName) {
        String fixedStationName = "";

        if (stationName != null) {
            fixedStationName = StringUtil.addPeriodToStreetNames(stationName);
            fixedStationName = StringUtil.removeSlashesAndBackSlashes(fixedStationName);
            fixedStationName = StringUtil.removeBracketsAndContents(fixedStationName);
        }

        return fixedStationName;
    }
}
