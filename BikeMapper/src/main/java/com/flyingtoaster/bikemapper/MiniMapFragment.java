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

/**
 * Created by tim on 2014-07-13.
 */
public class MiniMapFragment extends Fragment implements OnMyLocationChangeListener {

    private static final String TAG = "MiniMapFragment";
    private final double STARTING_LAT = 43.652992;
    private final double STARTING_LNG = -79.383657;

    private double mDestLat;
    private double mDestLng;

    View mRootView;


    private MapFragment mMapFragment;
    private GoogleMap mGoogleMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_minimap, container, false);

        Bundle bundle = getArguments();

        mDestLat = bundle.getDouble("dest_lat", STARTING_LAT);
        mDestLng = bundle.getDouble("dest_lng", STARTING_LNG);

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
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(torontoCoords, 13));

        mGoogleMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (mGoogleMap == null) return;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

}
