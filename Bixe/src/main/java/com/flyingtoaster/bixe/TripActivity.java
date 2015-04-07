package com.flyingtoaster.bixe;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.IntentSender;
import android.location.Location;

import com.flyingtoaster.bixe.tasks.GetJSONArrayListener;
import com.flyingtoaster.bixe.tasks.GetJsonArrayTask;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;

import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import com.google.gson.JsonArray;
import com.viewpagerindicator.LinePageIndicator;

/**
 * Created by tim on 2014-07-13.
 */
public class TripActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, SensorEventListener, LocationListener,
        GetJSONArrayListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static String TAG = "TripActivity";
    private static final double EARTH_RADIUS = 6373000;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private LocationClient mLocationClient;

    private Location mCurrentLocation;

    private CompassFragment mCompassFragment;
    private MiniMapFragment mMiniMapFragment;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Float azimut;

    public static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 2000;
    private static final int BIKE_UPDATE_FREQUENCY = 60000;

    private double mCurrentLatitude = 43.655423;
    private double mCurrentLongitude = -79.375904;
    private double mLatitude;
    private double mLongitude;

    private String mStationName;

    private boolean mUpdatesRequested;
    private LocationRequest mLocationRequest;

    private Thread mUpdateThread;
    private GetJsonArrayTask mJSONTask;

    private TextView mStationNameView;
    private TextView mDirectionView;
    private TextView mBikesAmountView;
    private TextView mDocksAmountView;

    private final String API_URL = "http://www.bikesharetoronto.com/stations/json";

    private float mStationID;

    private int mBikes;
    private int mDocks;
    private int mTotalDocks;

    private LinePageIndicator mIndicator;


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

        /*
        mIndicator = (LinePageIndicator)findViewById(R.id.titles);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setSelectedColor(Color.WHITE);
        mIndicator.setStrokeWidth(8);
        mIndicator.setLineWidth(64);
        mIndicator.setGapWidth(8);
        */

        mStationNameView = (TextView) findViewById(R.id.station_name_text_view);
        mDirectionView = (TextView) findViewById(R.id.distance_text_view);
        mBikesAmountView = (TextView) findViewById(R.id.bikes_amount_textview);
        mDocksAmountView = (TextView) findViewById(R.id.docks_amount_textview);

        Bundle bundle = getIntent().getExtras();

        mLatitude = bundle.getDouble("latitude");
        mLongitude = bundle.getDouble("longitude");
        mStationName = bundle.getString("station_name","");


        if (bundle != null) {
            mStationNameView.setText(bundle.getString("station_name", ""));
            mStationID = bundle.getInt("station_id", -1);
            mBikes = bundle.getInt("bikes", -1);
            mDocks = bundle.getInt("docks", -1);
            mTotalDocks = bundle.getInt("total_docks", -1);

            mBikesAmountView.setText(String.valueOf(mBikes));
            mDocksAmountView.setText(String.valueOf(mDocks));
        }

        mCompassFragment = new CompassFragment();
        mMiniMapFragment = new MiniMapFragment();
        mMiniMapFragment.setDest(mLatitude, mLongitude);
        mMiniMapFragment.setBikeInfo(mBikes, mDocks, mTotalDocks);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mUpdatesRequested = true;
        mLocationClient = new LocationClient(this, this, this);
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

    @Override
    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

        if (mUpdateThread == null) {
            mUpdateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            updateStations();
                            Thread.sleep(BIKE_UPDATE_FREQUENCY);
                            Log.d(TAG, "update JSON");
                            if (Thread.interrupted()) {
                                return;
                            }
                        } catch (InterruptedException e) {
                            Log.d(TAG, "update JSON interrupted");
                            // We've been interrupted: no more messages.
                            return;
                        } catch (Exception e) {
                        }
                    }
                }
            });

            mUpdateThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
            mUpdateThread = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();

        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
            mUpdateThread = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();

        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
            mUpdateThread = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
            overridePendingTransition(R.anim.pull_in_up, R.anim.push_out_down);
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
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
            switch (position) {
                case 0:
                    return mMiniMapFragment;
                //case 1:
                //    return mCompassFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    public void onConnected(Bundle dataBundle) {
        // Display the connection status

        if (mUpdatesRequested) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }

        mCurrentLocation = mLocationClient.getLastLocation();
        if (mCurrentLocation == null) return;
        String coordsString = mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude();
        mCurrentLongitude = mCurrentLocation.getLatitude();
        mCurrentLongitude = mCurrentLocation.getLongitude();
        Log.d(TAG, coordsString);
        //Toast.makeText(this, coordsString, Toast.LENGTH_SHORT).show();

        //Log.d(TAG, String.valueOf(getBearing(43.6432201, -79.3985133, mLatitude, mLongitude)));
        //Log.d(TAG, String.valueOf(getDistance(43.6432201, -79.3985133, mLatitude, mLongitude)));
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

    protected double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dlon = Math.toRadians(lon2-lon1);
        double dlat = Math.toRadians(lat2-lat1);
        double a = Math.pow(Math.pow(Math.sin(dlat/2),2) + Math.cos((lat1)) * Math.cos((lat2)) * (Math.sin(dlon/2)),2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS*c;
    }

    protected double getBearing(double lat1, double lon1, double lat2, double lon2) {
        return (Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(lon2) - Math.toRadians(lon1)) * Math.cos(Math.toRadians(lat2)), Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) - Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lon2) - Math.toRadians(lon1)))) + 360) % 360;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {

        //Log.d(TAG, "onSensorChanged");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                azimut = (float)Math.toDegrees(azimut) + 180;
                //azimut = azimut + (azimut < 0 ? 360 + azimut : azimut);
                //Log.d(TAG, String.valueOf(azimut));
                //Log.d(TAG, getCompassDir(azimut));
                //Log.d(TAG, String.valueOf(getBearing(mCurrentLatitude, mCurrentLongitude, mLatitude, mLongitude)));
                float bearing = (float)getBearing(mCurrentLatitude, mCurrentLongitude, mLatitude, mLongitude);
                int distance = (int)getDistance(mCurrentLatitude, mCurrentLongitude, mLatitude, mLongitude);
                // Round to the nearest 10
                distance = (distance / 10) * 10;
                mCompassFragment.setPinRot(bearing /*- azimut*/);
                //Log.d(TAG, String.valueOf(azimut));
                mCompassFragment.setCompassRot(azimut + 180);
                setDirectionText(distance + "m " + getCompassDir(bearing));
            }
        }
        //mCustomDrawableView.invalidate();
    }

    private String getCompassDir(float degrees) {
        // Use 22.5 for finer compass directions
        float correction = degrees+22.5 > 360 ? (float)(degrees+22.5 - 360) : (float)(degrees+22.5);
        int bearing = (int)((correction)/45);
        String direction = "";
        switch(bearing) {
            case 0:
                direction = "N";
                break;
            case 1:
                direction = "NE";
                break;
            case 2:
                direction = "E";
                break;
            case 3:
                direction = "SE";
                break;
            case 4:
                direction = "S";
                break;
            case 5:
                direction = "SW";
                break;
            case 6:
                direction = "W";
                break;
            case 7:
                direction = "NW";
                break;
        }
        //Log.d(TAG, direction);
        return direction;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();

        float bearing = (float)getBearing(mCurrentLatitude, mCurrentLongitude, mLatitude, mLongitude);
        int distance = (int)getDistance(mCurrentLatitude, mCurrentLongitude, mLatitude, mLongitude);
        distance = (distance / 10) * 10;

        setDirectionText(distance + " m " + getCompassDir(bearing));
        mCompassFragment.setPinRot(bearing);
    }

    public void onJSONArrayPreExecute() {

    }
    public void onJSONArrayProgressUpdate(String... params) {

    }
    public void onJSONArrayPostExecute(JsonArray jArray) {
        for (int i = 0; i < jArray.size(); i++) {
            if(jArray.get(i).getAsJsonObject().get("id").getAsInt() == mStationID) {
                mBikes = jArray.getAsJsonObject().get("availableBikes").getAsInt();
                mDocks = jArray.getAsJsonObject().get("availableDocks").getAsInt();
                mTotalDocks = jArray.getAsJsonObject().get("totalDocks").getAsInt();
                mBikesAmountView.setText(String.valueOf(mBikes));
                mDocksAmountView.setText(String.valueOf(mDocks));

                mMiniMapFragment.setBikeInfo(mBikes, mTotalDocks);
                break;
            }
        }
        Log.d("MainActivity", "GetJSONArrayTask complete");
    }
    public void onJSONArrayCancelled() {
        Log.d("MainActivity", "GetJSONArrayTask cancelled");
    }

    @Override
    public void onJSONArrayFailed() {

    }


    public void updateStations() {
        if (mJSONTask != null) {
            mJSONTask.cancel(true);
        }
        mJSONTask = new GetJsonArrayTask(this, API_URL);
        mJSONTask.execute();
    }

    public void setDirectionText(String description) {
        if (mDirectionView == null) return;
        mDirectionView.setText(description);
    }
}
