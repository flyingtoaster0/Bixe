package com.flyingtoaster.bixe.stationmap.ui;

import android.view.MenuItem;

import com.flyingtoaster.bixe.BuildConfig;
import com.flyingtoaster.bixe.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StationMapActivityTest {

    StationMapPresenter mPresenter;

    ActivityController<StationMapActivity> mController;
    StationMapActivity mActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mController = Robolectric.buildActivity(StationMapActivity.class);
        mController.create().visible();
        mActivity = mController.get();
        mPresenter = mActivity.mPresenter;
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
        MenuItem refreshButtonItem = shadowActivity.getOptionsMenu().findItem(R.id.action_refresh);
        refreshButtonItem.setVisible(true);

        mActivity.showLoading();

        assertThat(refreshButtonItem).isNotVisible();
    }

    @Test
    public void hideLoading_shouldShowRefreshButton() {
        ShadowActivity shadowActivity = shadowOf(mActivity);
        MenuItem refreshButtonItem = shadowActivity.getOptionsMenu().findItem(R.id.action_refresh);
        refreshButtonItem.setVisible(false);

        mActivity.hideLoading();

        assertThat(refreshButtonItem).isVisible();
    }

    @Test
    public void hideLoading_shouldHideLoadingSpinner() {
        ShadowActivity shadowActivity = shadowOf(mActivity);
        MenuItem refreshMenuItem = shadowActivity.getOptionsMenu().findItem(R.id.menu_progress);
        refreshMenuItem.setVisible(true);

        mActivity.hideLoading();

        assertThat(refreshMenuItem).isNotVisible();
    }

    @Test
    public void onResume_presenterShouldAttachView() {
        mController.start().resume();

        verify(mPresenter).attachView(mActivity);
    }

    @Test
    public void onResume_presenterShouldRefreshStations() {
        mController.start().resume();

        verify(mPresenter).refreshStations();
    }
}