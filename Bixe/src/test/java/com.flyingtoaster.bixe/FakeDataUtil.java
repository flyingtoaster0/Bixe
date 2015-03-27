package com.flyingtoaster.bixe;

import com.flyingtoaster.bixe.models.Station;
import com.google.gson.JsonObject;

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
}
