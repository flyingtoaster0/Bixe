package com.flyingtoaster.bikemapper;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by tim on 2014-07-13.
 */
public class MiniMapFragment extends Fragment implements OnMyLocationChangeListener {

    private static final String TAG = "MiniMapFragment";
    private final double STARTING_LAT = 43.652992;
    private final double STARTING_LNG = -79.383657;
    private final LatLng STARTING_LAT_LNG = new LatLng(STARTING_LAT, STARTING_LNG);

    private double mDestLat;
    private double mDestLng;
    private LatLng mDestLatLng;

    View mRootView;


    private MapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    private LatLngBounds mBounds;
    private LatLngBounds.Builder mBuilder = new LatLngBounds.Builder();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_minimap, container, false);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.minimap);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleMap = mMapFragment.getMap();
        LatLng torontoCoords = new LatLng(STARTING_LAT, STARTING_LNG);

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // no op for marker click
                return true;
            }
        });

        UiSettings mapSettings = mGoogleMap.getUiSettings();
        mapSettings.setScrollGesturesEnabled(false);
        mapSettings.setZoomGesturesEnabled(false);
        mapSettings.setRotateGesturesEnabled(false);


        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mDestLat, mDestLng)));
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(torontoCoords, 12));

        mGoogleMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (mGoogleMap == null || mDestLatLng == null) return;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mBuilder.include(mDestLatLng).include(latLng);
        mBounds =  mBuilder.build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 128));
    }

    public void setDest(double latitude, double longitude) {
        mDestLatLng = new LatLng(latitude, longitude);
        mDestLat = latitude;
        mDestLng = longitude;
    }
}
