package com.flyingtoaster.bixe.stationmap.ui.map;

import com.flyingtoaster.bixe.BuildConfig;
import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StationMapFragmentTest {

    private StationMapFragment mFragment;
    private GoogleApiClient mGoogleApiClient;

    @Before
    public void setup() {
        mFragment = new StationMapFragment();
        startFragment(mFragment);

        mGoogleApiClient = mFragment.mGoogleApiClient;
    }

    @Test
    public void onResume_googleApiClientShouldConnect() {
        verify(mGoogleApiClient).connect();
    }

    @Test
    public void onPause_googleApiClientShouldDisconnect() {
        mFragment.onPause();

        verify(mGoogleApiClient).disconnect();
    }
}