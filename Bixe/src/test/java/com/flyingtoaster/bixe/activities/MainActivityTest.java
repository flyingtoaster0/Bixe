package com.flyingtoaster.bixe.activities;

import android.content.Intent;
import android.view.View;

import com.flyingtoaster.bixe.BixeTestRunner;
import com.flyingtoaster.bixe.FakeDataUtil;
import com.flyingtoaster.bixe.fragments.base.AbsMarkerCallbackMapFragment;
import com.flyingtoaster.bixe.models.Station;
import com.flyingtoaster.bixe.tasks.MockGetJsonArrayTask;
import com.flyingtoaster.bixe.utils.StringUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BixeTestRunner.class)
@Config(shadows = {ShadowMainActivity.class})
public class MainActivityTest {

    ActivityController<MainActivity> mController;
    MainActivity mActivity;
    ShadowMainActivity mShadowActivity;
    NoUiMockBixeMapFragment mMapFragment;

    @Before
    public void setup() {
        mController = Robolectric.buildActivity(MainActivity.class);
        mActivity = mController.get();

        mMapFragment = new NoUiMockBixeMapFragment();
        mMapFragment.setOnMarkerClickListener(mActivity);

        mShadowActivity = (ShadowMainActivity)ShadowExtractor.extract(mActivity);
        mShadowActivity.setMapFragment(mMapFragment);
    }

    @Test
    public void whenMarkerIsClicked_stationInfoShouldPopulate() {
        mController.create().start().resume().visible();

        mMapFragment.onMarkerClick();
        Robolectric.flushForegroundScheduler();

        Station expectedStationData = FakeDataUtil.getStation();
        String expectedStationName = expectedStationData.getStationName();
        String expectedBikesText = String.valueOf(expectedStationData.getAvailableBikes());
        String expectedDocksText = String.valueOf(expectedStationData.getAvailableDocks());

        assertThat(mShadowActivity.getBikesAmountLayout()).isVisible();
        assertThat(mShadowActivity.getDocksAmountLayout()).isVisible();

        assertThat(mShadowActivity.getStationNameTextView()).hasText(expectedStationName);
        assertThat(mShadowActivity.getBikesAmountTextView()).hasText(expectedBikesText);
        assertThat(mShadowActivity.getDocksAmountTextView()).hasText(expectedDocksText);
    }

    @Test
    public void beforeMarkerIsClicked_slidingUpPanelCannotScroll() {
        mController.create().start().resume().visible();

        SlidingUpPanelLayout slidingUpPanel = mShadowActivity.getSlidingUpPanel();

        assertThat(slidingUpPanel.isTouchEnabled()).isFalse();
    }

    @Test
    public void afterMarkerIsClicked_slidingUpPanelCanScroll() {
        mController.create().start().resume().visible();

        SlidingUpPanelLayout slidingUpPanel = mShadowActivity.getSlidingUpPanel();

        mMapFragment.onMarkerClick();
        Robolectric.flushForegroundScheduler();

        assertThat(slidingUpPanel.isTouchEnabled()).isTrue();
    }

    @Test
    public void beforeMarkerIsClicked_lastSelectedStationIsNull() {
        mController.create().start().resume().visible();

        Station actualLastSelectedStation = mShadowActivity.getLastSelectedStation();

        assertThat(actualLastSelectedStation).isNull();
    }

    @Test
    public void afterMarkerIsClicked_lastSelectedStationExists() {
        mController.create().start().resume().visible();

        mMapFragment.onMarkerClick();

        Station actualLastSelectedStation = mShadowActivity.getLastSelectedStation();

        assertThat(actualLastSelectedStation).isNotNull();

        String expectedLastSelectedStationName = FakeDataUtil.getStation().getStationName();
        String actualLastSelectedStationName = actualLastSelectedStation.getStationName();

        assertThat(expectedLastSelectedStationName).isEqualTo(actualLastSelectedStationName);
    }

    @Test
    public void beforeMarkerIsClicked_shareButtonThrowsException() {
        mController.create().start().resume().visible();

        View shareButton = mShadowActivity.getShareButton();
        Exception actualException = null;

        try {
            shareButton.callOnClick();
        } catch (IllegalStateException e) {
            actualException = e;
        }

        assertThat(actualException).isNotNull();
    }

    @Test
    public void beforeMarkerIsClicked_shareButtonLaunchesSendIntent() {
        mController.create().start().resume().visible();
        View shareButton = mShadowActivity.getShareButton();

        mMapFragment.onMarkerClick();
        shareButton.callOnClick();

        Intent actualSendIntent = mShadowActivity.getNextStartedActivity();
        ShadowIntent shadowSendIntent = Shadows.shadowOf(actualSendIntent);

        String expectedAction = Intent.ACTION_SEND;
        String actualAction = shadowSendIntent.getAction();
        assertThat(expectedAction).isEqualTo(actualAction);

        String expectedShareText = StringUtils.getShareText(FakeDataUtil.getStation());
        String actualShareText = shadowSendIntent.getStringExtra(Intent.EXTRA_TEXT);
        assertThat(expectedShareText).isEqualTo(actualShareText);

        String expectedType = "text/plain";
        String actualType = shadowSendIntent.getType();
        assertThat(expectedType).isEqualTo(actualType);
    }

    @Test
    public void beforeMarkerIsClicked_saveButtonThrowsException() {
        mController.create().start().resume().visible();

        View shareButton = mShadowActivity.getSaveButton();
        Exception actualException = null;

        try {
            shareButton.callOnClick();
        } catch (IllegalStateException e) {
            actualException = e;
        }

        assertThat(actualException).isNotNull();
    }

    @Ignore
    @Test
    public void afterMarkerIsClicked_saveButtonShouldDoSomething() {
        mController.create().start().resume().visible();

        View shareButton = mShadowActivity.getSaveButton();
    }

    @Test
    public void whenSlidingPanelIsExpanded_onBackPressShouldCollapse() {
        mController.create().start().resume();

        SlidingUpPanelLayout slidingUpPanel = mShadowActivity.getSlidingUpPanel();

        mMapFragment.onMarkerClick();
        Robolectric.flushForegroundScheduler();

        slidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        Robolectric.flushForegroundScheduler();
        assertThat(slidingUpPanel.getPanelState()).isEqualTo(SlidingUpPanelLayout.PanelState.EXPANDED);

        mActivity.onBackPressed();
        assertThat(slidingUpPanel.getPanelState()).isEqualTo(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Test
    public void whenSlidingPanelIsCollapse_onBackPressShouldFinishActivity() {
        mController.create().start().resume();

        NoUiMockBixeMapFragment mapFragment = new NoUiMockBixeMapFragment();
        mapFragment.setOnMarkerClickListener(mActivity);

        ShadowMainActivity shadowMainActivity = (ShadowMainActivity)ShadowExtractor.extract(mActivity);
        shadowMainActivity.setMapFragment(mapFragment);

        mActivity.onBackPressed();

        assertThat(shadowMainActivity.isFinishing()).isTrue();
    }


    public static class NoUiMockBixeMapFragment extends AbsMarkerCallbackMapFragment {
        GoogleMap.OnMarkerClickListener mOnMarkerClickListener;

        @Override
        public void setOnMarkerClickListener(GoogleMap.OnMarkerClickListener onMarkerClickListener) {
            mOnMarkerClickListener = onMarkerClickListener;
        }

        @Override
        public Station getStationForMarker(Marker marker) {
            Station station = FakeDataUtil.getStation();
            return station;
        }

        @Override
        public void latchMyLocation() {
            /* NO OP */
        }

        @Override
        protected void makeRefreshCall() {
            MockGetJsonArrayTask jsonTask = new MockGetJsonArrayTask();
            jsonTask.execute();
        }

        public boolean onMarkerClick() {
            if (mOnMarkerClickListener != null) {
                mOnMarkerClickListener.onMarkerClick(null);
                return true;
            }

            return false;
        }
    }


}
