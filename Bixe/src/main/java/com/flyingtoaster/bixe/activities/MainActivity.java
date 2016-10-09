package com.flyingtoaster.bixe.activities;

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
import com.flyingtoaster.bixe.components.DaggerStationComponent;
import com.flyingtoaster.bixe.components.StationComponent;
import com.flyingtoaster.bixe.fragments.BixeMapFragment;
import com.flyingtoaster.bixe.interpolators.MaterialInterpolator;
import com.flyingtoaster.bixe.models.Station;
import com.flyingtoaster.bixe.modules.StationModule;
import com.flyingtoaster.bixe.providers.StationProvider;
import com.flyingtoaster.bixe.utils.StringUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {

    @BindView(R.id.station_name_text_view)
    TextView mStationNameTextView;

    @BindView(R.id.bikes_amount_textview)
    TextView mBikesAmountTextView;

    @BindView(R.id.docks_amount_textview)
    TextView mDocksAmountTextView;

    @BindView(R.id.bikes_amount_layout)
    View mBikesAmountLayout;

    @BindView(R.id.docks_amount_layout)
    View mDocksAmountLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    StationProvider mStationProvider;

    private MenuItem mRefreshButtonItem;
    private MenuItem mRefreshProgressBarItem;

    private BixeMapFragment mMapFragment;

    private Station mLastSelectedStation;

    private StationComponent mStationComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStationComponent = DaggerStationComponent.builder()
                .stationModule(new StationModule())
                .build();
        mStationComponent.inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setupMapFragment();

        loadStoredMarkers();
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapFragment.setOnMarkerClickListener(this);
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

    @OnClick(R.id.sliding_menu_floating_button_location)
    void onLocationFabClick() {
        mMapFragment.latchMyLocation();
    }

    @OnClick(R.id.sliding_content_view)
    void onSlidingContentClick() {
        // NO-OP
        // Prevent presses from "bleeding" through to the map Fragment
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
        if (mMapFragment == null) {
            mMapFragment = new BixeMapFragment();
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.toronto_fragment, mMapFragment);
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
        mLastSelectedStation = mMapFragment.getStationForMarker(marker);

        updateStationInfoView(mLastSelectedStation);

        if (mBikesAmountLayout.getVisibility() != View.VISIBLE) {
            showAmounts();
        }
        return true;
    }

    private void updateStationInfoView(Station station) {
        String stationName = StringUtil.fixStationName(station.getStationName());
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
        mStationProvider.getStationsLocal().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Station>>() {
            @Override
            public void accept(List<Station> stations) throws Exception {
                mMapFragment.updateMarkers(stations);
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

        mStationProvider.getStations().flatMap(new Function<List<Station>, ObservableSource<List<Station>>>() {
            @Override
            public ObservableSource<List<Station>> apply(List<Station> stations) throws Exception {
                return mStationProvider.putStationsLocal(stations);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Station>>() {
            @Override
            public void accept(List<Station> stations) throws Exception {
                mMapFragment.updateMarkers(stations);
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