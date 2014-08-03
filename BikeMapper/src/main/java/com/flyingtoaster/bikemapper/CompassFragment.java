package com.flyingtoaster.bikemapper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by tim on 2014-07-13.
 */
public class CompassFragment extends Fragment {

    View mRootView;

    float mPinRot;
    float mCompassRot;

    CompassView mCompassView;

    TextView mStationNameView;
    TextView mDirectionView;

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

        if (bundle != null) {
            mStationNameView.setText(bundle.getString("station_name", ""));
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

}
