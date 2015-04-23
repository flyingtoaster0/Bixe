package com.flyingtoaster.bixe.fragments;

import android.database.ContentObserver;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.datasets.BixeContentProvider;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.fragments.wrappers.TouchableWrapper;
import com.flyingtoaster.bixe.models.Station;
import com.flyingtoaster.bixe.tasks.GetJSONArrayListener;
import com.flyingtoaster.bixe.tasks.GetJsonArrayTask;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BixeMapFragment extends SupportMapFragment implements LocationListener, GetJSONArrayListener, GoogleApiClient.ConnectionCallbacks {

    private View mOriginalContentView;
    private TouchableWrapper mTouchView;

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final double STARTING_LAT = 43.652992;
    private static final double STARTING_LNG = -79.383657;

    private static LatLng DEFAULT_LATLNG = new LatLng(STARTING_LAT, STARTING_LNG);
    private ContentObserver mStationContentObserver;

    private HashMap<Integer, Station> mStations;
    private HashMap<String, Integer> mMarkerHash;
    private HashMap<Integer, Marker> mStationMarkerHash;

    private Integer mLastSelectedStationId;

    private GetJsonArrayTask mJSONTask;

    private MenuItem mRefreshProgressBarItem;
    private MenuItem mRefreshButtonItem;

    private boolean mLocationLatched = false;

    LocationLatchListener mInternalLocationLatchListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        setHasOptionsMenu(true);

        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        mTouchView.setTouchListener(new TouchableWrapper.OnTouchListener() {
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

        setupGoogleApi();

        mStationContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                loadStoredMarkers();
            }
        };

        makeRefreshCall();

        return mTouchView;
    }

    protected void setupGoogleApi() {
        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mStationContentObserver != null) {
            getActivity().getContentResolver().registerContentObserver(BixeContentProvider.CONTENT_URI, false, mStationContentObserver);
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mStationContentObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(mStationContentObserver);
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void loadStoredMarkers() {
        StationDataSource dataSource = new StationDataSource(getActivity());
        dataSource.open();

        ArrayList<Station> stationList = dataSource.getAllStations();
        dataSource.close();

        updateMarkers(stationList);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
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
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        mRefreshProgressBarItem = menu.findItem(R.id.menu_progress);
        mRefreshButtonItem = menu.findItem(R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                setRefreshVisible(false);
                makeRefreshCall();
                return true;
        }

        return false;
    }

    private void setRefreshVisible(boolean visible) {
        if (mRefreshButtonItem != null) {
            mRefreshButtonItem.setVisible(visible);
        }

        if (mRefreshProgressBarItem != null) {
            mRefreshProgressBarItem.setVisible(!visible);
        }
    }

   protected void makeRefreshCall() {
        if (mJSONTask != null) {
            mJSONTask.cancel(true);
        }
        mJSONTask = new GetJsonArrayTask(this);
        mJSONTask.execute();
    }

    public void onJSONArrayPreExecute() {

    }

    public void onJSONArrayProgressUpdate(String... params) {

    }

    public void onJSONArrayPostExecute(JsonArray jArray) {
        setRefreshVisible(true);
    }

    public void onJSONArrayCancelled() {
        setRefreshVisible(true);
    }

    public void onJSONArrayFailed() {
        setRefreshVisible(true);
    }

    private void updateMarkers(final List<Station> stations) {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();

                for (Station station : stations) {
                    mStations.put(station.getId(), station);

                    BitmapDescriptor bitmapDescriptor;

                    if (mLastSelectedStationId != null && mLastSelectedStationId == station.getId()) {
                        bitmapDescriptor = station.getSelectedMarkerBitmapDescriptor();
                    } else {
                        bitmapDescriptor = station.getMarkerBitmapDescriptor();
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
        marker.setIcon(selectedStation.getSelectedMarkerBitmapDescriptor());

        return selectedStation;
    }

    public void resetLastMarkerIcon() {
        Marker lastMarker = mStationMarkerHash.get(mLastSelectedStationId);
        if (lastMarker == null) {
            return;
        }

        Station lastStation = mStations.get(mLastSelectedStationId);

        BitmapDescriptor bitmapDescriptor = lastStation.getMarkerBitmapDescriptor();
        lastMarker.setIcon(bitmapDescriptor);
    }

    public void setOnMarkerClickListener(final GoogleMap.OnMarkerClickListener onMarkerClickListener) {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMarkerClickListener(onMarkerClickListener);
            }
        });
    }

    public void setLocationLatchListener(LocationLatchListener listener) {
        mInternalLocationLatchListener = listener;
    }

    public abstract static class LocationLatchListener {
        public abstract void onLocationLatched(boolean isLatched);
    }
}