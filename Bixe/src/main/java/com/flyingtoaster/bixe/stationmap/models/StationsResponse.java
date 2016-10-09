package com.flyingtoaster.bixe.stationmap.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StationsResponse {

    @SerializedName("stationBeanList")
    List<Station> mStationList;

    public List<Station> getStationList() {
        return mStationList;
    }
}
