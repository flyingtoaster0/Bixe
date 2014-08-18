package com.flyingtoaster.bixe;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tim on 2014-07-13.
 */
public class CompassFragment extends Fragment {

    private static final String TAG = "CompassFragment";

    float mPinRot;
    float mCompassRot;
    CompassView mCompassView;
    View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_compass, container, false);
        mCompassView = (CompassView) mRootView.findViewById(R.id.compass_view);

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
}
