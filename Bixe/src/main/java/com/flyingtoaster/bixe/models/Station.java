package com.flyingtoaster.bixe.models;

import android.util.Log;

import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.utils.StringUtils;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Station {
    private int mId;
    private String mStationName;
    private int mAvailableDocks;
    private int mTotalDocks;
    private double mLatitude;
    private double mLongitude;
    private boolean inService;
    private int mAvailableBikes;

    public Station() {

    }

    public Station(JSONObject jObj) {
        update(jObj);
    }

    public Station(JsonObject stationJson) {
        update(stationJson);
    }

    public void update(JsonObject stationJson) {
        mId = stationJson.get("id").getAsInt();

        String serverStationName = stationJson.get("stationName").getAsString();
        mStationName = fixStationName(serverStationName);
        mAvailableDocks = stationJson.get("availableDocks").getAsInt();
        mTotalDocks = stationJson.get("totalDocks").getAsInt();
        mAvailableBikes = stationJson.get("availableBikes").getAsInt();
        inService = stationJson.get("statusValue").getAsString().equals("In Service");
        mLatitude = stationJson.get("latitude").getAsDouble();
        mLongitude = stationJson.get("longitude").getAsDouble();
    }

    public void update(JSONObject jObj) {
        try {
            mId = jObj.getInt("id");
            mStationName = fixStationName(jObj.getString("stationName"));
            mAvailableDocks = jObj.getInt("availableDocks");
            mTotalDocks = jObj.getInt("totalDocks");
            mAvailableBikes = jObj.getInt("availableBikes");
            inService = jObj.getString("statusValue").equals("In Service");
//            mLatLng = new LatLng(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
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

    public int getAvailableBikes() {
        return mAvailableBikes;
    }

    public int getAvailableDocks() {
        return mAvailableDocks;
    }

    public int getTotalDocks() {
        return mTotalDocks;
    }

    public LatLng getLatLng() {
        return new LatLng(mLatitude, mLongitude);
    }

    public boolean isInService() {
        return inService;
    }

    private String fixStationName(String stationName) {
        String fixedStationName = "";

        if (stationName != null) {
            fixedStationName = StringUtils.addPeriodToStreetNames(stationName);
            fixedStationName = StringUtils.removeSlashesAndBackSlashes(fixedStationName);
            fixedStationName = StringUtils.removeBracketsAndContents(fixedStationName);
        }

        return fixedStationName;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setStationName(String mStationName) {
        this.mStationName = mStationName;
    }

    public void setAvailableDocks(int mAvailableDocks) {
        this.mAvailableDocks = mAvailableDocks;
    }

    public void setTotalDocks(int mTotalDocks) {
        this.mTotalDocks = mTotalDocks;
    }

    public void setInService(boolean inService) {
        this.inService = inService;
    }

    public void setAvailableBikes(int mAvailableBikes) {
        this.mAvailableBikes = mAvailableBikes;
    }


    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public BitmapDescriptor getMarkerBitmapDescriptor() {
        BitmapDescriptor bitmapDescriptor = null;
        int markerResource = getMarkerResourceId();

        bitmapDescriptor = BitmapDescriptorFactory.fromResource(markerResource);

        return bitmapDescriptor;
    }

    public BitmapDescriptor getSelectedMarkerBitmapDescriptor() {
        BitmapDescriptor bitmapDescriptor = null;
        int markerResource = getSelectedMarkerResourceId();

        bitmapDescriptor = BitmapDescriptorFactory.fromResource(markerResource);

        return bitmapDescriptor;
    }

    public int getMarkerResourceId() {
        int percent = (int)(((float) getAvailableBikes() / (float) getTotalDocks())*100);
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
    public int getSelectedMarkerResourceId() {int percent = (int)(((float) getAvailableBikes() / (float) getTotalDocks())*100);
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
}
