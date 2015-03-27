package com.flyingtoaster.bixe.activities;

import java.util.HashMap;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingtoaster.bixe.tasks.GetJSONArrayListener;
import com.flyingtoaster.bixe.tasks.GetJSONArrayTask;
import com.flyingtoaster.bixe.interpolators.MaterialInterpolator;
import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.models.Station;
import com.flyingtoaster.bixe.fragments.TouchableMapFragment;
import com.flyingtoaster.bixe.fragments.wrappers.TouchableWrapper;
import com.flyingtoaster.bixe.TripActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.FloatingActionButtonLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;



public class MainActivity extends ActionBarActivity implements GetJSONArrayListener {

    private final String TAG = "ManActivity";

    private final String GOOGLE_MAPS_URL_PREFIX = "http://maps.google.com/maps?q=";
    private final String API_URL = "http://www.bikesharetoronto.com/stations/json";
    private final double STARTING_LAT = 43.652992;
    private final double STARTING_LNG = -79.383657;

    public static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 2000;

    private static final int NO_ID = -1;

    private TouchableMapFragment mTorontoFragment;

    private GoogleMap mGoogleMap;
    private GetJSONArrayTask mJSONTask;

    private HashMap<Integer, Station> mStations;
    private HashMap<String, Integer> mMarkerHash;

    private ImageButton mNavigateButton;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private View dragView;
    private RelativeLayout mSlidingContentView;
    private LinearLayout mContentLayout;

    private int mStationID = NO_ID;
    private int mBikes;
    private int mDocks;
    private int mTotalDocks;

    private View mCollapsedContentView;

    private TextView mStationNameView;
    private TextView mBikesAmountView;
    private TextView mDocksAmountView;

    private View mBikesAmountLayout;
    private View mDocksAmountLayout;

    private View mBikesLabelView;
    private View mDocksLabelView;

    private View mSaveButton;
    private View mShareButton;

    private MenuItem mRefreshProgressBarItem;
    private MenuItem mRefreshButtonItem;

    private LatLng mStationLatLng;
    private String mStationName;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    //    private FloatingActionButton mDirectionsFab;
    private FloatingActionButton mLocationFab;
    private FloatingActionButtonLayout mFloatingButtonLayout;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private LatLng mLastLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open_content_description, R.string.drawer_close_content_description) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mActionBarDrawerToggle.syncState();


        mStations = new HashMap<Integer, Station>();
        mMarkerHash = new HashMap<String, Integer>();

        mBikesAmountLayout = findViewById(R.id.bikes_amount_layout);
        mDocksAmountLayout = findViewById(R.id.docks_amount_layout);

        mBikesLabelView = findViewById(R.id.bikes_amount_label);
        mDocksLabelView = findViewById(R.id.docks_amount_label);

//        mDirectionsFab = (FloatingActionButton) findViewById(R.id.sliding_menu_floating_button_directions);
        mLocationFab = (FloatingActionButton) findViewById(R.id.sliding_menu_floating_button_location);

        mLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleMap != null) {
                    showMyLocation();
                }
            }
        });

        mContentLayout = (LinearLayout) findViewById(R.id.content_layout);

        mSaveButton = findViewById(R.id.slidemenu_button_save);
        mShareButton = findViewById(R.id.slidemenu_button_share);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();

                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
                sendIntent.setType("text/plain");

                startActivity(sendIntent);
            }
        });

        mCollapsedContentView = findViewById(R.id.slidemenu_collapsed_content);
        mStationNameView = (TextView) findViewById(R.id.station_name_text_view);
        mBikesAmountView = (TextView) findViewById(R.id.bikes_amount_textview);
        mDocksAmountView = (TextView) findViewById(R.id.docks_amount_textview);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mSlidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.sliding_panel_collapsed_height));
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mSlidingUpPanelLayout.setTouchEnabled(false);
        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {

            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }

            @Override
            public void onPanelHiddenExecuted(View panel, Interpolator interpolator, int duration) {

            }

            @Override
            public void onPanelShownExecuted(View panel, Interpolator interpolator, int duration) {

            }

            @Override
            public void onPanelExpandedStateY(View panel, boolean reached) {

            }

            @Override
            public void onPanelCollapsedStateY(View panel, boolean reached) {
                if (reached) {
                    fadePanelToLight();
                } else {
                    fadePanelToDark();
                }
            }

            @Override
            public void onPanelLayout(View panel, SlidingUpPanelLayout.PanelState state) {

            }
        });
        dragView = findViewById(R.id.sliding_content_view);

        mSlidingContentView = (RelativeLayout) findViewById(R.id.sliding_content_view);
        mFloatingButtonLayout = (FloatingActionButtonLayout) findViewById(R.id.floating_button_layout);

//        mDirectionsFab = (FloatingActionButton) findViewById(R.id.sliding_menu_floating_button_directions);

        mNavigateButton = (ImageButton) findViewById(R.id.navigate_button);
        mNavigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mNavigateButton.onClick()");
                // Pass info to the TripActivity here

                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", mStationLatLng.latitude);
                bundle.putDouble("longitude", mStationLatLng.longitude);
                bundle.putString("station_name", mStationName);
                bundle.putInt("station_id", mStationID);
                bundle.putInt("bikes", mBikes);
                bundle.putInt("docks", mDocks);
                bundle.putInt("total_docks", mTotalDocks);
                intent.putExtras(bundle);
                startActivity(intent);

                overridePendingTransition(R.anim.pull_in_down, R.anim.push_out_up);
            }
        });

        // This sets the "handle" of the SlidingUpPanel
        mSlidingUpPanelLayout.setDragView(dragView);

        // Prevent presses from "bleeding" through below the SlidingUpPanel
        mSlidingContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStationID == NO_ID) {
                    return;
                }

                SlidingUpPanelLayout.PanelState nextState;

                switch (mSlidingUpPanelLayout.getPanelState()) {
                    case COLLAPSED:
                    case HIDDEN:
                        nextState = SlidingUpPanelLayout.PanelState.EXPANDED;
                        break;
                    case EXPANDED:
                    case ANCHORED:
                    default:
                        nextState= SlidingUpPanelLayout.PanelState.COLLAPSED;
                        break;
                }

                mSlidingUpPanelLayout.setPanelState(nextState);
            }
        });

        mTorontoFragment = (TouchableMapFragment) getFragmentManager().findFragmentById(R.id.toronto_fragment);

//        mSlidingUpPanelLayout.attachFloatingActionButton(mDirectionsFab, 0, 100, 0, 1000);
        updateStations();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleMap = mTorontoFragment.getMap();
        LatLng torontoCoords = new LatLng(STARTING_LAT, STARTING_LNG);

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                updateStationInfoView(marker);

                if (mBikesAmountLayout.getVisibility() != View.VISIBLE) {
                    showAmounts();
                }
                return true;
            }
        });

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);

        if (mLastLatLng == null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(torontoCoords, 13));
        }

        setupLocationClient();

    }

    private void showMyLocation() {
        if (mLastLatLng == null) {
            return;
        }

        mGoogleMap.setOnMyLocationChangeListener(getOnMyLocationChangeListener());

        mTorontoFragment.setTouchListener(new TouchableWrapper.OnTouchListener() {
            @Override
            public void onTouch() {

            }

            @Override
            public void onRelease() {

            }

            @Override
            public void onDrag() {
                mGoogleMap.setOnMyLocationChangeListener(null);
                mTorontoFragment.setTouchListener(null);
            }
        });

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mLastLatLng));
    }

    @Override
    protected void onPause() {
        mGoogleMap.setOnMyLocationChangeListener(null);

        setRefreshVisible(true);

        super.onPause();
    }

    private void fadePanelToLight() {
        TextView bikeLabel = (TextView) findViewById(R.id.bikes_amount_label);
        TextView dockLabel = (TextView) findViewById(R.id.docks_amount_label);

        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.green_white_animator);
        animator.setTarget(mCollapsedContentView);
        animator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator2 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator2.setTarget(mStationNameView);
        animator2.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator3 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator3.setTarget(bikeLabel);
        animator3.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator4 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator4.setTarget(dockLabel);
        animator4.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator5 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator5.setTarget(mDocksAmountView);
        animator5.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator6 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator6.setTarget(mBikesAmountView);
        animator6.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator7 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.fab_white_green_animator);
        animator7.setTarget(mLocationFab);
        animator7.setEvaluator(new ArgbEvaluator());

        animator.start();
        animator2.start();
        animator3.start();
        animator4.start();
        animator5.start();
        animator6.start();
        animator7.start();
        mLocationFab.setColorPressedResId(R.color.green_dark_highlight);

        TransitionDrawable transitionDrawable = (TransitionDrawable) mLocationFab.getDrawable();
        transitionDrawable.reverseTransition(250);
    }

    private void fadePanelToDark() {
        TextView bikeLabel = (TextView) findViewById(R.id.bikes_amount_label);
        TextView dockLabel = (TextView) findViewById(R.id.docks_amount_label);

        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_green_animator);
        animator.setTarget(mCollapsedContentView);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();

        ObjectAnimator animator2 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator2.setTarget(mStationNameView);
        animator2.setEvaluator(new ArgbEvaluator());
        animator2.start();

        ObjectAnimator animator3 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator3.setTarget(bikeLabel);
        animator3.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator4 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator4.setTarget(dockLabel);
        animator4.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator5 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator5.setTarget(mDocksAmountView);
        animator5.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator6 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator6.setTarget(mBikesAmountView);
        animator6.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator7 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.fab_green_white_animator);
        animator7.setTarget(mLocationFab);
        animator7.setEvaluator(new ArgbEvaluator());

        animator.start();
        animator2.start();
        animator3.start();
        animator4.start();
        animator5.start();
        animator6.start();
        animator7.start();
        mLocationFab.setColorPressedResId(R.color.white_button_press);

        TransitionDrawable transitionDrawable = (TransitionDrawable) mLocationFab.getDrawable();
        transitionDrawable.startTransition(250);

    }

    public void updateStations() {
        if (mJSONTask != null) {
            mJSONTask.cancel(true);
        }
        mJSONTask = new GetJSONArrayTask(this, API_URL);
        mJSONTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mRefreshProgressBarItem = menu.findItem(R.id.menu_progress);
        mRefreshButtonItem = menu.findItem(R.id.action_refresh);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            setRefreshVisible(false);
            updateStations();
            return true;
        }

        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void onJSONArrayPreExecute() {

    }

    public void onJSONArrayProgressUpdate(String... params) {

    }

    public void onJSONArrayPostExecute(JSONArray jArray) {
        try {
            mGoogleMap.clear();

            for (int i = 0; i < jArray.length(); i++) {
                Station station = new Station(jArray.getJSONObject(i));
                mStations.put(station.getId(), station);

                BitmapDescriptor bitmapDescriptor = getBitmapDescriptor(station);

                MarkerOptions options = new MarkerOptions()
                        .title(station.getStationName())
                        .snippet(availabilityString(station))
                        .position(station.getLatLng());

                if (bitmapDescriptor != null) {
                    options.icon(bitmapDescriptor);
                }

                Marker marker = mGoogleMap.addMarker(options);

                String markerId = marker.getId();
                Integer stationId = station.getId();

                mMarkerHash.put(markerId, stationId);
            }
        } catch (JSONException e) {
            setRefreshVisible(true);
            Log.e("MainActivity", "Could not get JSONObject");
        }


        setRefreshVisible(true);

        Log.d("MainActivity", "GetJSONArrayTask complete");
    }

    public void onJSONArrayCancelled() {
        setRefreshVisible(true);

        Log.d("MainActivity", "GetJSONArrayTask cancelled");
    }

    public void onJSONArrayFailed() {
        setRefreshVisible(true);

        Log.d("MainActivity", "GetJSONArrayTask cancelled");
    }

    public String availabilityString(Station station) {
        if (station == null) return "";
        return "Available Bikes: " + station.getAvailableBikes() + "\nAvailable Docks: " + station.getAvailableDocks();
    }

    private void updateStationInfoView(Marker marker) {
        if (marker == null || mMarkerHash == null) return;

        Integer stationId = mMarkerHash.get(marker.getId());
        if (stationId == null) return;

        Station theStation = mStations.get(stationId);

        String stationName = theStation.getStationName();
        Integer availableBikes = theStation.getAvailableBikes();
        Integer availableDocks = theStation.getAvailableDocks();
        Integer totalDocks = theStation.getTotalDocks();
        mStationLatLng = theStation.getLatLng();
        mStationName = theStation.getStationName();

        mStationID = stationId;
        mBikes = availableBikes;
        mDocks = availableDocks;
        mTotalDocks = totalDocks;

        mStationNameView.setText(stationName);
        mBikesAmountView.setText(availableBikes.toString());
        mDocksAmountView.setText(availableDocks.toString());

        mSlidingUpPanelLayout.setTouchEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    public static BitmapDescriptor getBitmapDescriptor(Station station) {
        BitmapDescriptor bitmapDescriptor = null;
        float percent = (float) station.getAvailableBikes() / (float) station.getTotalDocks();

        if (percent == 1) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_5);
        } else if (percent >= 0.8) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_4);
        } else if (percent >= 0.6) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_3);
        } else if (percent >= 0.4) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_2);
        } else if (percent >= 0.2) {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_1);
        } else {
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_0);
        }

        return bitmapDescriptor;
    }

    private void setupLocationClient() {
        mLocationClient = new LocationClient(this, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                double myLat = mLocationClient.getLastLocation().getLatitude();
                double myLng = mLocationClient.getLastLocation().getLongitude();

                if (mLastLatLng == null) {
                    mLastLatLng = new LatLng(myLat, myLng);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
                }

                mLastLatLng = new LatLng(myLat, myLng);
            }

            @Override
            public void onDisconnected() {

            }
        }, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                // NOOP
            }
        });
        mLocationClient.connect();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    private LocationListener getLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double myLat = location.getLatitude();
                double myLng = location.getLongitude();

                mLastLatLng = new LatLng(myLat, myLng);
            }
        };
    }

    private GoogleMap.OnMyLocationChangeListener getOnMyLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                double myLat = location.getLatitude();
                double myLng = location.getLongitude();
                LatLng myLocation = new LatLng(myLat, myLng);

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
            }
        };
    }

    private String getShareText() {
        StringBuilder builder = new StringBuilder();

        builder.append(mStationName);
        builder.append("\n\n");
        builder.append(getLatLngUrl());

        return builder.toString();
    }

    private String getLatLngUrl() {
        return GOOGLE_MAPS_URL_PREFIX + mStationLatLng.latitude + "+" + mStationLatLng.longitude;
    }

    private void showAmounts() {
        circularRevealView(mBikesAmountLayout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                circularRevealView(mDocksAmountLayout);
            }
        }, 75);
    }

    private void circularRevealView(View view) {
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
        view.setTranslationX(0.25f);
        view.setTranslationY(0.25f);

        view.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).translationX(0.0f).translationY(0.0f).setDuration(200).setInterpolator(new MaterialInterpolator()).start();
    }

    private void setRefreshVisible(boolean visible) {
        if (mRefreshButtonItem != null) {
            mRefreshButtonItem.setVisible(visible);
        }

        if (mRefreshProgressBarItem != null) {
            mRefreshProgressBarItem.setVisible(!visible);
        }
    }
}
