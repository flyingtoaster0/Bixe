package com.flyingtoaster.bixe.stationmap.ui;

import android.view.MenuItem;
import android.widget.TextView;

import com.flyingtoaster.bixe.BuildConfig;
import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.stationmap.models.Station;
import com.flyingtoaster.bixe.stationmap.ui.map.StationMapFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StationMapActivityTest {

    StationMapPresenter mPresenter;
    StationMapFragment mMapFragment;

    @BindView(R.id.station_name_text_view)
    TextView mStationNameTextView;

    @BindView(R.id.bikes_amount_textview)
    TextView mBikesAmountTextView;

    @BindView(R.id.docks_amount_textview)
    TextView mDocksAmountTextView;

    @Mock
    Station mStation;

    ActivityController<StationMapActivity> mController;
    StationMapActivity mActivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mController = Robolectric.buildActivity(StationMapActivity.class);
        mController.create().visible();
        mActivity = mController.get();
        ButterKnife.bind(this, mActivity);
        mPresenter = mActivity.mPresenter;
        mMapFragment = mActivity.mMapFragment;
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

    @Test
    public void onPause_presenterShouldDetachView() {
        mController.start().resume().pause();

        verify(mPresenter).detachView();
    }

    @Test
    public void whenRefreshButtonIsClicked_presenterShouldRefreshStations() {
        ShadowActivity shadowActivity = shadowOf(mActivity);
        MenuItem refreshButtonItem = shadowActivity.getOptionsMenu().findItem(R.id.action_refresh);

        mActivity.onOptionsItemSelected(refreshButtonItem);

        verify(mPresenter).refreshStations();
    }

    @Test
    public void updateMarkers_fragmentShouldUpdateMarkers() {
        mController.start().resume();

        List<Station> stations = Collections.emptyList();
        mActivity.updateMarkers(stations);

        verify(mMapFragment).updateMarkers(stations);
    }

    @Test
    public void onStationSelect_shouldNotifyPresenter() {
        mController.start().resume();

        mActivity.onStationSelect(mStation);

        verify(mPresenter).onStationSelect(mStation);
    }

    @Test
    public void updateSelectedStationView_shouldPopulate_name_availableBikes_availableDocks() {
        mController.start().resume();

        mActivity.updateSelectedStationView("King and Spadina", "6", "4");

        assertThat(mStationNameTextView).hasText("King and Spadina");
        assertThat(mBikesAmountTextView).hasText("6");
        assertThat(mDocksAmountTextView).hasText("4");
    }
}