package com.flyingtoaster.bixe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.clients.StationClient;
import com.flyingtoaster.bixe.datasets.StationDataSource;
import com.flyingtoaster.bixe.fragments.BixeMapFragment;
import com.flyingtoaster.bixe.interpolators.MaterialInterpolator;
import com.flyingtoaster.bixe.models.Station;
import com.flyingtoaster.bixe.providers.LocalStationProvider;
import com.flyingtoaster.bixe.providers.StationProvider;
import com.flyingtoaster.bixe.utils.StationUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {

    private BixeMapFragment mTorontoFragment;

    private TextView mStationNameTextView;
    private TextView mBikesAmountTextView;
    private TextView mDocksAmountTextView;
    private View mBikesAmountLayout;
    private View mDocksAmountLayout;
    private View mShareButton;
    private View mLocationFab;
    private View mSlidingContentView;

    private MenuItem mRefreshProgressBarItem;
    private MenuItem mRefreshButtonItem;

    private Toolbar mToolbar;

    private Station mLastSelectedStation;

    private StationClient mStationClient;
    private StationProvider mStationProvider;

    private StationDataSource mStationDataSource;
    private LocalStationProvider mLocalStationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mStationNameTextView = (TextView) findViewById(R.id.station_name_text_view);
        mBikesAmountTextView = (TextView) findViewById(R.id.bikes_amount_textview);
        mDocksAmountTextView = (TextView) findViewById(R.id.docks_amount_textview);
        mBikesAmountLayout = findViewById(R.id.bikes_amount_layout);
        mDocksAmountLayout = findViewById(R.id.docks_amount_layout);
        mLocationFab = findViewById(R.id.sliding_menu_floating_button_location);
        mShareButton = findViewById(R.id.slidemenu_button_share);
        mSlidingContentView = findViewById(R.id.sliding_content_view);

        setSupportActionBar(mToolbar);
        
        mLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTorontoFragment.latchMyLocation();
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastSelectedStation == null) {
                    throw new IllegalStateException("A station must be selected before sharing");
                }

                Intent sendIntent = new Intent();

                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, StationUtils.getShareText(mLastSelectedStation));
                sendIntent.setType("text/plain");

                startActivity(sendIntent);
            }
        });

        mSlidingContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NO-OP
                // Prevent presses from "bleeding" through to the map Fragment
            }
        });

        mStationClient = new StationClient();
        mStationProvider = new StationProvider(mStationClient);

        mStationDataSource = new StationDataSource(this);
        mLocalStationProvider = new LocalStationProvider(mStationDataSource);

        if (mTorontoFragment == null) {
            mTorontoFragment = new BixeMapFragment();
        }
        setupMapFragment();

        loadStoredMarkers();
    }

    @Override
    public void onResume() {
        super.onResume();

        mTorontoFragment.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mRefreshProgressBarItem = menu.findItem(R.id.menu_progress);
        mRefreshButtonItem = menu.findItem(R.id.action_refresh);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshMarkers();
                return true;
        }

        return false;
    }

    private void showLoading(boolean visible) {
        if (mRefreshButtonItem != null) {
            mRefreshButtonItem.setVisible(!visible);
        }

        if (mRefreshProgressBarItem != null) {
            mRefreshProgressBarItem.setVisible(visible);
        }
    }

    protected void setupMapFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.toronto_fragment, mTorontoFragment);
        ft.commit();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int keycode = event.getKeyCode();
        final int action = event.getAction();
        if (keycode == KeyEvent.KEYCODE_MENU && action == KeyEvent.ACTION_UP) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mLastSelectedStation = mTorontoFragment.getStationForMarker(marker);

        updateStationInfoView(mLastSelectedStation);

        if (mBikesAmountLayout.getVisibility() != View.VISIBLE) {
            showAmounts();
        }
        return true;
    }

    private void updateStationInfoView(Station station) {
        String stationName = station.getStationName();
        Integer availableBikes = station.getAvailableBikes();
        Integer availableDocks = station.getAvailableDocks();

        mStationNameTextView.setText(stationName);
        mBikesAmountTextView.setText(availableBikes.toString());
        mDocksAmountTextView.setText(availableDocks.toString());
    }

    private void showAmounts() {
        circularRevealView(mBikesAmountLayout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                circularRevealView(mDocksAmountLayout);
            }
        }, 75);
    }

    private void circularRevealView(View view) {
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
        view.setTranslationX(0.25f);
        view.setTranslationY(0.25f);

        view.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).translationX(0.0f).translationY(0.0f).setDuration(200).setInterpolator(new MaterialInterpolator()).start();
    }

    private void loadStoredMarkers() {
        mLocalStationProvider.getStations().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Station>>() {
            @Override
            public void accept(List<Station> stations) throws Exception {
                mTorontoFragment.updateMarkers(stations);
                refreshMarkers();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    private void refreshMarkers() {
        showLoading(true);

        mStationProvider.getStations().doOnNext(new Consumer<List<Station>>() {
            @Override
            public void accept(List<Station> stations) throws Exception {
                mLocalStationProvider.putStations(stations);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Station>>() {
            @Override
            public void accept(List<Station> stations) throws Exception {
                mTorontoFragment.updateMarkers(stations);
                showLoading(false);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                showLoading(false);
            }
        });
    }
}