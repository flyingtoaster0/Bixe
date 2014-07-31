package com.flyingtoaster.bikemapper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;

import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

/**
 * Created by tim on 2014-07-13.
 */
public class TripActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static String TAG = "TripActivity";
    private static final double EARTH_RADIUS = 6373000;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    LocationClient mLocationClient;

    Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trip);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
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
            return new CompassFragment();
            //return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }


    }

    public void onConnected(Bundle dataBundle) {
        // Display the connection status

        mCurrentLocation = mLocationClient.getLastLocation();
        String coordsString = mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude();
        Log.d(TAG, coordsString);
        Toast.makeText(this, coordsString, Toast.LENGTH_SHORT).show();

        Log.d(TAG, String.valueOf(getBearing(43.655416, -79.3753296, 43.6432201, -79.3985133)));
        Log.d(TAG, String.valueOf(getDistance(43.655416, -79.3753296, 43.6432201, -79.3985133)));
    }


    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    protected double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dlon = Math.toRadians(lon2-lon1);
        double dlat = Math.toRadians(lat2-lat1);
        double a = Math.pow(Math.pow(Math.sin(dlat/2),2) + Math.cos((lat1)) * Math.cos((lat2)) * (Math.sin(dlon/2)),2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return  EARTH_RADIUS*c;
    }

    protected double getBearing(double lat1, double lon1, double lat2, double lon2) {
        return (Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(lon2) - Math.toRadians(lon1)) * Math.cos(Math.toRadians(lat2)), Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) - Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lon2) - Math.toRadians(lon1)))) + 360) % 360;
    }
}
