package com.flyingtoaster.bixe;

import com.flyingtoaster.bixe.models.Station;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class FakeDataUtil {

    public static JsonObject getStationJsonObject() {
        JsonObject stationJson = new JsonObject();

        stationJson.addProperty("id", 1);
        stationJson.addProperty("stationName", "Yonge St \\/ Dundas St");
        stationJson.addProperty("availableBikes", 4);
        stationJson.addProperty("availableDocks", 6);
        stationJson.addProperty("totalDocks", 10);
        stationJson.addProperty("statusValue", "In Service");
        stationJson.addProperty("latitude", 43.7000);
        stationJson.addProperty("longitude", 79.4000);

        return stationJson;
    }

    public static Station getStation() {
        return new Station(FakeDataUtil.getStationJsonObject());
    }

    public static ArrayList<Station> getStations() {
        ArrayList<Station> stations = new ArrayList<>();

        Station firstStation = getStation();
        Station secondStation = new Station();
        secondStation.setId(2);
        secondStation.setStationName("Yonge St. and Adelaide St.");
        secondStation.setAvailableBikes(5);
        secondStation.setAvailableDocks(7);
        secondStation.setTotalDocks(12);
        secondStation.setInService(true);
        secondStation.setLatitude(43.6504161);
        secondStation.setLongitude(79.378426);

        stations.add(firstStation);
        stations.add(secondStation);

        return stations;
    }
}
