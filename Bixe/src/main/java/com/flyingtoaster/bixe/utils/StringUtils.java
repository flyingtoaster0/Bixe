package com.flyingtoaster.bixe.utils;


public class StringUtils {

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
}
