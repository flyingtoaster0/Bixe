package com.flyingtoaster.bikemapper;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Station {
    private int mId;
    private String mStationName;
    private int mAvailableDocks;
    private int mTotalDocks;
    private LatLng mLatLng;
    private boolean inService;
    private int mAvailableBikes;

    public Station(JSONObject jObj) {
        update(jObj);
    }

    public void update(JSONObject jObj) {
        try {
            mId = jObj.getInt("id");
            mStationName = fixStationName(jObj.getString("stationName"));
            mAvailableDocks = jObj.getInt("availableDocks");
            mTotalDocks = jObj.getInt("totalDocks");
            mAvailableBikes = jObj.getInt("availableBikes");
            inService = jObj.getString("statusValue").equals("In Service");
            mLatLng = new LatLng(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
        } catch (JSONException e) {
            Log.e("Station", "Could not update Station");
        }
    }

    public int getId() {
        return mId;
    }

    public String getStationName() {
        return mStationName;
    }

    public int getAvailableDocks() {
        return mAvailableDocks;
    }

    public int getTotalDocks() {
        return mTotalDocks;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public int getAvailableBikes() {
        return mAvailableBikes;
    }

    public boolean isInService() {
        return inService;
    }

    private String fixStationName(String stationName) {
        if (stationName == null) return "";

        return stationName.replaceAll(" ?/", " And");
    }
}
