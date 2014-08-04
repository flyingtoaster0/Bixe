package com.flyingtoaster.bikemapper;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

/**
 * Created by tim on 2014-07-13.
 */
public class CompassFragment extends Fragment implements GetJSONArrayListener {

    private static final String TAG = "CompassFragment";
    private final String API_URL = "http://www.bikesharetoronto.com/stations/json";

    float mStationID;

    View mRootView;

    float mPinRot;
    float mCompassRot;

    int mBikes;
    int mDocks;
    private TextView mBikesAmountView;
    private TextView mDocksAmountView;

    CompassView mCompassView;

    TextView mStationNameView;
    TextView mDirectionView;

    private GetJSONArrayTask mJSONTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_compass, container, false);


        Bundle bundle = getActivity().getIntent().getExtras();

        mCompassView = (CompassView) mRootView.findViewById(R.id.compass_view);
        mStationNameView = (TextView) mRootView.findViewById(R.id.station_name_text_view);
        mDirectionView = (TextView) mRootView.findViewById(R.id.distance_text_view);
        mBikesAmountView = (TextView) mRootView.findViewById(R.id.bikes_amount_textview);
        mDocksAmountView = (TextView) mRootView.findViewById(R.id.docks_amount_textview);

        if (bundle != null) {
            mStationNameView.setText(bundle.getString("station_name", ""));
            mStationID = bundle.getInt("station_id", -1);
            mBikes = bundle.getInt("bikes", -1);
            mDocks = bundle.getInt("docks", -1);

            mBikesAmountView.setText(String.valueOf(mBikes));
            mDocksAmountView.setText(String.valueOf(mDocks));
        }



        return mRootView;
    }


    public void setPinRot(float degrees) {
        if (mCompassView == null) return;
        mCompassView.setPinAngle(degrees);
        mCompassView.invalidate();
        mPinRot = degrees;
    }

    public void setCompassRot(float degrees) {
        if (mCompassView == null) return;
        mCompassView.setCompassAngle(degrees);
        mCompassRot = degrees;
    }

    public void setStationName(String name) {
        //mStationNameView.
    }

    public void setDirectionText(String description) {
        if (mDirectionView == null) return;
        mDirectionView.setText(description);
    }

    public void onJSONArrayPreExecute() {

    }
    public void onJSONArrayProgressUpdate(String... params) {

    }
    public void onJSONArrayPostExecute(JSONArray jArray) {
        try {
            for (int i = 0; i < jArray.length(); i++) {
                if(jArray.getJSONObject(i).getInt("id") == mStationID) {
                    mBikes = jArray.getJSONObject(i).getInt("availableBikes");
                    mDocks = jArray.getJSONObject(i).getInt("availableDocks");
                    mBikesAmountView.setText(String.valueOf(mBikes));
                    mDocksAmountView.setText(String.valueOf(mDocks));
                }
            }
        } catch (JSONException e) {
            Log.e("MainActivity", "Could not get JSONObject");
        }
        Log.d("MainActivity", "GetJSONArrayTask complete");
    }
    public void onJSONArrayCancelled() {
        Log.d("MainActivity", "GetJSONArrayTask cancelled");
    }


    public void updateStations() {
        if (mJSONTask != null) {
            mJSONTask.cancel(true);
        }
        mJSONTask = new GetJSONArrayTask(this, API_URL);
        mJSONTask.execute();
    }
}
