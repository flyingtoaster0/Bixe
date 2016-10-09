package com.flyingtoaster.bixe.clients;

import com.flyingtoaster.bixe.models.Station;
import com.flyingtoaster.bixe.models.StationsResponse;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class StationClient extends OkHttpClient {

    private static final String API_URL = "https://feeds.bikesharetoronto.com/stations/stations.json";

    @Inject
    public StationClient() {
    }

    public List<Station> getStations() {
        List<Station> stationList = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .build();

            Response callResponse = newCall(request).execute();
            Gson gson = new Gson();
            String body = callResponse.body().string();
            StationsResponse stationsResponse = gson.fromJson(body, StationsResponse.class);
            stationList = stationsResponse.getStationList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stationList;
    }
}
