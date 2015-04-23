package com.flyingtoaster.bixe.activities;

import android.view.View;
import android.widget.TextView;

import com.flyingtoaster.bixe.fragments.BixeMapFragment;
import com.flyingtoaster.bixe.fragments.base.AbsMarkerCallbackMapFragment;
import com.flyingtoaster.bixe.models.Station;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ReflectionHelpers;

@Implements(MainActivity.class)
public class ShadowMainActivity extends ShadowActivity {
    @RealObject private MainActivity realMainActivity;

    @Implementation
    public SlidingUpPanelLayout getSlidingUpPanel() {
        return (SlidingUpPanelLayout) ReflectionHelpers.getField(realMainActivity, "mSlidingUpPanelLayout");
    }

    @Implementation
    public void setMapFragment(AbsMarkerCallbackMapFragment mapFragment) {
        ReflectionHelpers.setField(realMainActivity, "mTorontoFragment", mapFragment);
    }

    @Implementation
    public View getBikesAmountLayout() {
        return (View)ReflectionHelpers.getField(realMainActivity, "mBikesAmountLayout");
    }

    @Implementation
    public View getDocksAmountLayout() {
        return (View)ReflectionHelpers.getField(realMainActivity, "mDocksAmountLayout");
    }

    @Implementation
    public TextView getBikesAmountTextView() {
        return (TextView)ReflectionHelpers.getField(realMainActivity, "mBikesAmountTextView");
    }

    @Implementation
    public TextView getDocksAmountTextView() {
        return (TextView)ReflectionHelpers.getField(realMainActivity, "mDocksAmountTextView");
    }

    @Implementation
    public TextView getStationNameTextView() {
        return (TextView)ReflectionHelpers.getField(realMainActivity, "mStationNameTextView");
    }

    @Implementation
    public Station getLastSelectedStation() {
        return (Station)ReflectionHelpers.getField(realMainActivity, "mLastSelectedStation");
    }

    @Implementation
    public View getShareButton() {
        return (View)ReflectionHelpers.getField(realMainActivity, "mShareButton");
    }

    @Implementation
    public View getSaveButton() {
        return (View)ReflectionHelpers.getField(realMainActivity, "mSaveButton");
    }
}
