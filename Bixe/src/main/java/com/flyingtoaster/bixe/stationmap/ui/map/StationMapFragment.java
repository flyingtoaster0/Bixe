package com.flyingtoaster.bixe.stationmap.ui.map;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.stationmap.models.Station;
import com.flyingtoaster.bixe.utils.DrawableUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class StationMapFragment extends SupportMapFragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener {

    private static final double STARTING_LAT = 43.652992;
    private static final double STARTING_LNG = -79.383657;
    private static LatLng DEFAULT_LATLNG = new LatLng(STARTING_LAT, STARTING_LNG);

    @Inject
    GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private HashMap<Integer, Station> mStations;
    private HashMap<String, Integer> mMarkerHash;
    private HashMap<Integer, Marker> mStationMarkerHash;

    private boolean mLocationLatched = false;
    private LocationLatchListener mInternalLocationLatchListener;
    private OnStationSelectListener mOnStationSelectListener;
    private Integer mLastSelectedStationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View originalContentView = super.onCreateView(inflater, parent, savedInstanceState);

        BixeApplication.getApplication().getStationComponent().inject(this);

        TouchableWrapper touchView = new TouchableWrapper(getActivity());
        touchView.addView(originalContentView);
        touchView.setTouchListener(new TouchableWrapper.OnTouchListener() {
            @Override
            public void onTouch() {

            }

            @Override
            public void onRelease() {

            }

            @Override
            public void onDrag() {
                if (mLocationLatched) {
                    setLocationLatched(false);
                }
            }
        });

        mStations = new HashMap<>();
        mMarkerHash = new HashMap<>();
        mStationMarkerHash = new HashMap<>();

        createLocationRequest();

        return touchView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        mGoogleApiClient.disconnect();
    }

    public synchronized void latchMyLocation() {
        setLocationLatched(true);
        animateMyLocation();
    }

    private void setLocationLatched(boolean latched) {
        mLocationLatched = latched;
        if (mInternalLocationLatchListener != null) {
            mInternalLocationLatchListener.onLocationLatched(latched);
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if (mLocationLatched) {
            animateMyLocation();
        }
    }

    public synchronized void animateMyLocation() {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (mLastLocation != null) {
                    final LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    final CameraUpdate update = CameraUpdateFactory.newLatLng(currentLatLng);
                    googleMap.animateCamera(update);
                }
            }
        });
    }

    public synchronized void animateMyLocationZoom(final int zoomLevel) {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (mLastLocation != null) {
                    final LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    final CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel);
                    googleMap.animateCamera(update);
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        boolean isFirstSetup = mLastLocation == null;

        if (location != null) {
            mLastLocation = location;

            if (isFirstSetup) {
                animateMyLocationZoom(15);
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        setupGoogleMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void setupGoogleMap() {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(false);

                CameraUpdate update;
                if (mLastLocation != null) {
                    LatLng lastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    update = CameraUpdateFactory.newLatLngZoom(lastLatLng, 15);
                } else {
                    update = CameraUpdateFactory.newLatLngZoom(DEFAULT_LATLNG, 15);
                }

                googleMap.moveCamera(update);
                googleMap.setOnMarkerClickListener(StationMapFragment.this);
            }
        });
    }

    public void updateMarkers(final List<Station> stations) {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();

                for (Station station : stations) {
                    mStations.put(station.getId(), station);

                    BitmapDescriptor bitmapDescriptor;

                    if (mLastSelectedStationId != null && mLastSelectedStationId == station.getId()) {
                        bitmapDescriptor = DrawableUtil.getSelectedMarkerBitmapDescriptor(station);
                    } else {
                        bitmapDescriptor = DrawableUtil.getMarkerBitmapDescriptor(station);
                    }

                    MarkerOptions options = new MarkerOptions()
                            .title(station.getStationName())
                            .position(station.getLatLng());

                    if (bitmapDescriptor != null) {
                        options.icon(bitmapDescriptor);
                    }

                    Marker marker = googleMap.addMarker(options);

                    String markerId = marker.getId();
                    Integer stationId = station.getId();

                    mMarkerHash.put(markerId, stationId);
                    mStationMarkerHash.put(stationId, marker);
                }

            }
        });
    }

    public Station getStationForMarker(Marker marker) {
        Integer stationId = mMarkerHash.get(marker.getId());
        Station selectedStation = mStations.get(stationId);
        resetLastMarkerIcon();
        mLastSelectedStationId = selectedStation.getId();
        marker.setIcon(DrawableUtil.getSelectedMarkerBitmapDescriptor(selectedStation));

        return selectedStation;
    }

    public void resetLastMarkerIcon() {
        Marker lastMarker = mStationMarkerHash.get(mLastSelectedStationId);
        if (lastMarker == null) {
            return;
        }

        Station lastStation = mStations.get(mLastSelectedStationId);

        BitmapDescriptor bitmapDescriptor = DrawableUtil.getMarkerBitmapDescriptor(lastStation);
        lastMarker.setIcon(bitmapDescriptor);
    }

    public void setLocationLatchListener(LocationLatchListener listener) {
        mInternalLocationLatchListener = listener;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mOnStationSelectListener != null) {
            Station selectedStation = getStationForMarker(marker);
            mOnStationSelectListener.onStationSelect(selectedStation);
            return true;
        }

        return false;
    }

    public void setOnStationSelectListener(OnStationSelectListener onStationSelectListener) {
        mOnStationSelectListener = onStationSelectListener;
    }

    public abstract static class LocationLatchListener {
        public abstract void onLocationLatched(boolean isLatched);
    }

    public interface OnStationSelectListener {
        void onStationSelect(Station station);
    }
}