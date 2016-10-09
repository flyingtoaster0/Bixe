package com.flyingtoaster.bixe.stationmap.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Station {

    @SerializedName("id")
    private int mId;
    @SerializedName("stationName")
    private String mStationName;
    @SerializedName("availableDocks")
    private int mAvailableDocks;
    @SerializedName("totalDocks")
    private int mTotalDocks;
    @SerializedName("availableBikes")
    private int mAvailableBikes;
    @SerializedName("latitude")
    private double mLatitude;
    @SerializedName("longitude")
    private double mLongitude;

    public Station() {
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

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setStationName(String stationName) {
        mStationName = stationName;
    }

    public void setAvailableDocks(int availableDocks) {
        mAvailableDocks = availableDocks;
    }

    public void setTotalDocks(int totalDocks) {
        mTotalDocks = totalDocks;
    }

    public void setAvailableBikes(int availableBikes) {
        mAvailableBikes = availableBikes;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
