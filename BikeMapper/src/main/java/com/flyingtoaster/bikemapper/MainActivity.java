package com.flyingtoaster.bikemapper;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

    private final String API_URL = "http://www.bikesharetoronto.com/stations/json";
    private final double STARTING_LAT = 43.652992;
    private final double STARTING_LNG = -79.383657;

    private MapFragment mTorontoFragment;

    private GoogleMap mGoogleMap;
    private GetJSONArrayTask mJSONTask;

    private HashMap<Integer, Station> mStations;
    private HashMap<String, Integer> mMarkerHash;

    private ImageButton refreshButton;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private LinearLayout dragView;
    private LinearLayout slidingContentView;

    private TextView mStationNameView;
    private TextView mBikesAmountView;
    private TextView mDocksAmountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStations = new HashMap<Integer, Station>();
        mMarkerHash = new HashMap<String, Integer>();


        mStationNameView = (TextView) findViewById(R.id.station_name_view);
        mBikesAmountView = (TextView) findViewById(R.id.bikes_amount_textview);
        mDocksAmountView = (TextView) findViewById(R.id.docks_amount_textview);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        dragView = (LinearLayout) findViewById(R.id.drag_view);
        slidingContentView = (LinearLayout) findViewById(R.id.sliding_content_view);
        refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStations();
            }
        });

        // This sets the "handle" of the SlidingUpPanel
        mSlidingUpPanelLayout.setDragView(dragView);

        // Prevent presses from "bleeding" through below the SlidingUpPanel
        slidingContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
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
                mSlidingUpPanelLayout.expandPanel();
                return true;
            }
        });

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(torontoCoords, 13));
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
        if (id == R.id.action_settings) {
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

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .title(station.getStationName())
                        .snippet(availabilityString(station))
                        .position(station.getLatLng()));

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

        mStationNameView.setText(stationName);
        mBikesAmountView.setText(availableBikes.toString());
        mDocksAmountView.setText(availableDocks.toString());
    }

    @Override
    public void onBackPressed() {
        // TODO: Also show the handle, or add a collapse button or something
        if (mSlidingUpPanelLayout.isPanelExpanded()) {
            mSlidingUpPanelLayout.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }
}
