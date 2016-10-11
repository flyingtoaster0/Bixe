package com.flyingtoaster.bixe.utils;


import com.flyingtoaster.bixe.stationmap.models.Station;

public class StationFormatter {

    public String getFormattedStationName(Station station) {
        String formatted = station.getStationName();
        formatted = addPeriodToStreetNames(formatted);
        formatted = removeSlashesAndBackSlashes(formatted);
        formatted = removeBracketsAndContents(formatted);

        return formatted;
    }

    String removeSlashesAndBackSlashes(String original) {
        String outputName = "";

        if (original != null) {
            outputName = original.replaceAll(" ?\\\\/ ?", " and ");
            outputName = outputName.replaceAll(" ?/ ?", " and ");
        }

        return outputName;
    }

    String addPeriodToStreetNames(String original) {
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

    String removeBracketsAndContents(String original) {
        String output = "";

        output = original.replaceAll(" ?\\(.*\\)", "");

        return output;
    }
}
