package com.flyingtoaster.bixe.stationmap.ui;

import android.view.MenuItem;

import com.flyingtoaster.bixe.BuildConfig;
import com.flyingtoaster.bixe.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StationMapActivityTest {

    ActivityController<StationMapActivity> mController;
    StationMapActivity mActivity;

    @Before
    public void setup() {
        mController = Robolectric.buildActivity(StationMapActivity.class);
        mController.create().visible();
        mActivity = mController.get();
    }

    @Test
    public void showLoading_shouldShowLoadingSpinner() {
        ShadowActivity shadowActivity = shadowOf(mActivity);
        MenuItem refreshMenuItem = shadowActivity.getOptionsMenu().findItem(R.id.menu_progress);
        refreshMenuItem.setVisible(false);

        mActivity.showLoading();

        assertThat(refreshMenuItem).isVisible();
    }

    @Test
    public void showLoading_shouldHideRefreshButton() {
        ShadowActivity shadowActivity = shadowOf(mActivity);
        MenuItem refreshMenuItem = shadowActivity.getOptionsMenu().findItem(R.id.action_refresh);
        refreshMenuItem.setVisible(true);

        mActivity.showLoading();

        assertThat(refreshMenuItem).isNotVisible();
    }
}