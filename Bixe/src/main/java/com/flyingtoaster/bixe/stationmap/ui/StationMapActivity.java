package com.flyingtoaster.bixe.stationmap.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flyingtoaster.bixe.BixeApplication;
import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.interpolators.MaterialInterpolator;
import com.flyingtoaster.bixe.stationmap.models.Station;
import com.flyingtoaster.bixe.stationmap.ui.map.StationMapFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StationMapActivity extends AppCompatActivity implements StationMapContract.View, StationMapFragment.OnStationSelectListener {

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
    StationMapPresenter mPresenter;

    @Inject
    StationMapFragment mMapFragment;

    private MenuItem mRefreshButtonItem;
    private MenuItem mRefreshProgressBarItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BixeApplication.getApplication().getStationComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.map_fragment, mMapFragment);
        ft.commit();
        mMapFragment.setOnStationSelectListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.attachView(this);
        mPresenter.refreshStations();
    }

    @Override
    protected void onPause() {
        mPresenter.detachView();

        super.onPause();
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
                mPresenter.refreshStations();
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
    public void showLoading() {
        if (mRefreshProgressBarItem != null && mRefreshButtonItem != null) {
            mRefreshProgressBarItem.setVisible(true);
            mRefreshButtonItem.setVisible(false);
        }
    }

    @Override
    public void hideLoading() {
        if (mRefreshProgressBarItem != null && mRefreshButtonItem != null) {
            mRefreshProgressBarItem.setVisible(false);
            mRefreshButtonItem.setVisible(true);
        }
    }

    @Override
    public void updateMarkers(List<Station> stations) {
        mMapFragment.updateMarkers(stations);
    }

    @Override
    public void updateSelectedStationView(String stationName, String availableBikes, String availableDocks) {
        mStationNameTextView.setText(stationName);
        mBikesAmountTextView.setText(availableBikes);
        mDocksAmountTextView.setText(availableDocks);

        if (mBikesAmountLayout.getVisibility() != View.VISIBLE) {
            showAmounts();
        }
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

    @Override
    public void onStationSelect(Station station) {
        mPresenter.onStationSelect(station);
    }
}