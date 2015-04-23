package com.flyingtoaster.bixe.fragments;

import com.flyingtoaster.bixe.models.Station;
import com.google.android.gms.maps.model.Marker;

public interface MarkerCallback {

    public Station getStationForMarker(Marker marker);
}
