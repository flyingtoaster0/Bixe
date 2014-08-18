package com.flyingtoaster.bixe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;

/**
 * Created by tim on 6/6/14.
 */

public class TorontoFragment extends MapFragment {

    View mRootView;
    MapView mMapView;
    GoogleMap mGoogleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_toronto, container, false);

        /*
        mMapView = (MapView)mRootView.findViewById(R.id.map_view);
        mGoogleMap = mMapView.getMap();

        LatLng sydney = new LatLng(-33.867, 151.206);

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        mGoogleMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
*/
        return mRootView;
    }

}
