package com.flyingtoaster.bixe.fragments.base;

import com.flyingtoaster.bixe.models.Station;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

public abstract class AbsMarkerCallbackMapFragment extends SupportMapFragment {

    public abstract void setOnMarkerClickListener(final GoogleMap.OnMarkerClickListener onMarkerClickListener);
    public abstract Station getStationForMarker(Marker marker);
    public abstract void latchMyLocation();
    protected abstract void makeRefreshCall();
}
