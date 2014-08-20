package com.flyingtoaster.bixe;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingtoaster.util.IabHelper;
import com.flyingtoaster.util.IabResult;
import com.flyingtoaster.util.Inventory;
import com.flyingtoaster.util.Purchase;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;


//import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends Activity implements GetJSONArrayListener {

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

    private MapFragment mTorontoFragment;

    private GoogleMap mGoogleMap;
    private GetJSONArrayTask mJSONTask;

    private HashMap<Integer, Station> mStations;
    private HashMap<String, Integer> mMarkerHash;

    private ImageButton refreshButton;
    private ImageButton mNavigateButton;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private View dragView;
    private LinearLayout slidingContentView;
    private RelativeLayout contentLayout;

    private int mStationID;
    private int mBikes;
    private int mDocks;
    private int mTotalDocks;

    private TextView mStationNameView;
    private TextView mBikesAmountView;
    private TextView mDocksAmountView;

    private AdView mAdView;

    private LatLng mStationLatLng;
    private String mStationName;

    private IabHelper mHelper;
    private String base64EncodedPublicKey;

    private boolean mIsPremium;

    private SharedPreferences prefs;

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStations = new HashMap<Integer, Station>();
        mMarkerHash = new HashMap<String, Integer>();

        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);

        checkAndSetupAd();

        mStationNameView = (TextView) findViewById(R.id.station_name_text_view);
        mBikesAmountView = (TextView) findViewById(R.id.bikes_amount_textview);
        mDocksAmountView = (TextView) findViewById(R.id.docks_amount_textview);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        dragView = findViewById(R.id.drag_view);
        slidingContentView = (LinearLayout) findViewById(R.id.sliding_content_view);
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStations();
            }
        });

        mNavigateButton= (ImageButton) findViewById(R.id.navigate_button);
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
        slidingContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mTorontoFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.toronto_fragment);


        updateStations();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        //mViewPager = (ViewPager) findViewById(R.id.pager);
        //mViewPager.setAdapter(mSectionsPagerAdapter);

        mStationNameView.setText("DUNDAS OR SOMETHING");
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
                mSlidingUpPanelLayout.expandPane();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    public void updateStations() {
        if (mJSONTask != null) {
            mJSONTask.cancel(true);
        }
        mJSONTask = new GetJSONArrayTask(this, API_URL);
        mJSONTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
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
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    }

    @Override
    public void onBackPressed() {
        // TODO: Also show the handle, or add a collapse button or something
        if (mSlidingUpPanelLayout.isExpanded()) {
            mSlidingUpPanelLayout.collapsePane();
        } else {
            super.onBackPressed();
        }
    }

    private void checkAndSetupAd() {
        prefs = getSharedPreferences(getString(R.string.app_name), 0);

        if (prefs.getBoolean("remove_ads", false)) return;

        base64EncodedPublicKey = getString(R.string.base64_rsa_key);
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                } else {
                    Log.d(TAG, "IAB set up successfully!! " + result);

                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            }
        });

        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

                if (result.isFailure()) {
                    Log.d(TAG, "The thing failed");
                }
                else {

                    if (inventory.hasPurchase(getString(R.string.sku_test_purchased))) {
                        mHelper.consumeAsync(inventory.getPurchase(getString(R.string.sku_test_purchased)), null);
                    }
                    // does the user have the premium upgrade?
                    mIsPremium = inventory.hasPurchase(getString(R.string.sku_remove_ads));

                    if (!mIsPremium) {
                        BannerAdFragment adFragment = new BannerAdFragment();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.add(R.id.content_layout, adFragment);
                        ft.commit();
                    } else {
                        prefs.edit().putBoolean("remove_ads", true).commit();
                    }

                    Log.d(TAG, String.valueOf(mIsPremium));
                }
            }
        };

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase)
            {
                if (result.isFailure()) {
                    Log.d(TAG, "Error purchasing: " + result);
                    return;
                }
                else if (purchase.getSku().equals(getString(R.string.sku_remove_ads))) {
                    Log.d(TAG, "Success??");
                    if (mAdView == null) return;

                    prefs.edit().putBoolean("remove_ads", true).commit();
                    contentLayout.removeView(mAdView);
                }
            }
        };
    }

    public void launchPurchaseFlow() {
        if (mHelper == null) return;
        mHelper.launchPurchaseFlow(this, getString(R.string.sku_remove_ads), 1001, mPurchaseFinishedListener);
    }

    public static BitmapDescriptor getBitmapDescriptor(Station station) {
        BitmapDescriptor bitmapDescriptor = null;
        float percent = (float)station.getAvailableBikes() / (float)station.getTotalDocks();

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
