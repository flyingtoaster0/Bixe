package com.flyingtoaster.bixe;

import java.util.HashMap;
import java.util.Locale;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.FloatingActionButtonLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;


//import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends ActionBarActivity implements GetJSONArrayListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private final String TAG = "ManActivity";

    private final String API_URL = "http://www.bikesharetoronto.com/stations/json";
    private final double STARTING_LAT = 43.652992;
    private final double STARTING_LNG = -79.383657;

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

    private int mStationID;
    private int mBikes;
    private int mDocks;
    private int mTotalDocks;

    private View mCollapsedContentView;

    private TextView mStationNameView;
    private TextView mBikesAmountView;
    private TextView mDocksAmountView;

    private LatLng mStationLatLng;
    private String mStationName;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private FloatingActionButton mDirectionsFab;
    private FloatingActionButton mLocationFab;
    private FloatingActionButtonLayout mFloatingButtonLayout;

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

        mDirectionsFab = (FloatingActionButton) findViewById(R.id.sliding_menu_floating_button_directions);
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

        mDirectionsFab = (FloatingActionButton) findViewById(R.id.sliding_menu_floating_button_directions);

        // TODO put this in the onmenuoptionselected
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
////            public void onClick(View v) {
//                updateStations();
//            }
//        });

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
            }
        });

        mTorontoFragment = (TouchableMapFragment) getFragmentManager().findFragmentById(R.id.toronto_fragment);

//        mSlidingUpPanelLayout.attachFloatingActionButton(mDirectionsFab, 0, 100, 0, 1000);
        updateStations();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        //mViewPager = (ViewPager) findViewById(R.id.pager);
        //mViewPager.setAdapter(mSectionsPagerAdapter);

        //mStationNameView.setText("DUNDAS OR SOMETHING");
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
                //mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                return true;
            }
        });

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(torontoCoords, 13));

    }

    private void showMyLocation() {
        double myLat = mGoogleMap.getMyLocation().getLatitude();
        double myLng = mGoogleMap.getMyLocation().getLongitude();

        LatLng myLocation = new LatLng(myLat, myLng);


        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                double myLat = location.getLatitude();
                double myLng = location.getLongitude();
                LatLng myLocation = new LatLng(myLat, myLng);

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
            }
        });

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
                mLocationFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_location_grey600_24dp));
                mTorontoFragment.setTouchListener(null);
            }
        });

        mLocationFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_location_darkgreen_24px));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    @Override
    protected void onPause() {
        mGoogleMap.setOnMyLocationChangeListener(null);
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
        animator7.setTarget(mDirectionsFab);
        animator7.setEvaluator(new ArgbEvaluator());

        animator.start();
        animator2.start();
        animator3.start();
        animator4.start();
        animator5.start();
        animator6.start();
        animator7.start();
        mDirectionsFab.setColorPressedResId(R.color.green_dark_highlight);

        TransitionDrawable transitionDrawable = (TransitionDrawable) mDirectionsFab.getDrawable();
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
        animator7.setTarget(mDirectionsFab);
        animator7.setEvaluator(new ArgbEvaluator());

        animator.start();
        animator2.start();
        animator3.start();
        animator4.start();
        animator5.start();
        animator6.start();
        animator7.start();
        mDirectionsFab.setColorPressedResId(R.color.white_button_press);

        TransitionDrawable transitionDrawable = (TransitionDrawable) mDirectionsFab.getDrawable();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateStations();
//            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
//            startActivity(intent);
            return true;
        }

        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
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
            Log.e("MainActivity", "Could not get JSONObject");
        }


        Log.d("MainActivity", "GetJSONArrayTask complete");
    }

    public void onJSONArrayCancelled() {
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

        if (mDirectionsFab.getVisibility() != View.VISIBLE) {
            Animation fadeInDirectionsFab = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in);
            fadeInDirectionsFab.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mDirectionsFab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            mDirectionsFab.startAnimation(fadeInDirectionsFab);
        }

        if (mLocationFab.getVisibility() != View.VISIBLE) {

            Animation fadeInLocationFab = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in);
            fadeInLocationFab.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mLocationFab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            mLocationFab.startAnimation(fadeInLocationFab);
        }

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
}
