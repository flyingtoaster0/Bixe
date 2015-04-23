package com.flyingtoaster.bixe.activities;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingtoaster.bixe.fragments.base.AbsMarkerCallbackMapFragment;
import com.flyingtoaster.bixe.interpolators.MaterialInterpolator;
import com.flyingtoaster.bixe.R;
import com.flyingtoaster.bixe.models.Station;
import com.flyingtoaster.bixe.fragments.BixeMapFragment;
import com.flyingtoaster.bixe.utils.StringUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.FloatingActionButtonLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {

    private AbsMarkerCallbackMapFragment mTorontoFragment;

    private ImageButton mNavigateButton;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private View dragView;
    private LinearLayout mSlidingContentView;
    private LinearLayout mContentLayout;

    private int mBikes;
    private int mDocks;
    private int mTotalDocks;

    private View mCollapsedContentView;

    private TextView mStationNameTextView;
    private TextView mBikesAmountTextView;
    private TextView mDocksAmountTextView;

    private View mBikesAmountLayout;
    private View mDocksAmountLayout;

    private View mBikesLabelView;
    private View mDocksLabelView;

    private View mSaveButton;
    private View mShareButton;

    private LatLng mStationLatLng;
    private String mStationName;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private FloatingActionButton mLocationFab;
    private FloatingActionButtonLayout mFloatingButtonLayout;

    private Station mLastSelectedStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open_content_description, R.string.drawer_close_content_description) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mActionBarDrawerToggle.syncState();

        mBikesAmountLayout = findViewById(R.id.bikes_amount_layout);
        mDocksAmountLayout = findViewById(R.id.docks_amount_layout);

        mBikesLabelView = findViewById(R.id.bikes_amount_label);
        mDocksLabelView = findViewById(R.id.docks_amount_label);

        mLocationFab = (FloatingActionButton) findViewById(R.id.sliding_menu_floating_button_location);

        mLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTorontoFragment.latchMyLocation();
            }
        });

        mContentLayout = (LinearLayout) findViewById(R.id.content_layout);

        mSaveButton = findViewById(R.id.slidemenu_button_save);
        mShareButton = findViewById(R.id.slidemenu_button_share);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastSelectedStation == null) {
                    throw new IllegalStateException("A station must be selected before sharing");
                }

                Intent sendIntent = new Intent();

                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, StringUtils.getShareText(mLastSelectedStation));
                sendIntent.setType("text/plain");

                startActivity(sendIntent);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastSelectedStation == null) {
                    throw new IllegalStateException("A station must be selected before sharing");
                }
            }
        });

        mCollapsedContentView = findViewById(R.id.slidemenu_collapsed_content);
        mStationNameTextView = (TextView) findViewById(R.id.station_name_text_view);
        mBikesAmountTextView = (TextView) findViewById(R.id.bikes_amount_textview);
        mDocksAmountTextView = (TextView) findViewById(R.id.docks_amount_textview);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mSlidingUpPanelLayout.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.sliding_panel_collapsed_height));
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mSlidingUpPanelLayout.setTouchEnabled(false);
        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelCollapsedStateY(View panel, boolean reached) {
                if (reached) {
                    fadePanelToLight();
                } else {
                    fadePanelToDark();
                }
            }
        });
        dragView = findViewById(R.id.sliding_content_view);

        mSlidingContentView = (LinearLayout) findViewById(R.id.sliding_content_view);
        mFloatingButtonLayout = (FloatingActionButtonLayout) findViewById(R.id.floating_button_layout);

        mNavigateButton = (ImageButton) findViewById(R.id.navigate_button);

        // This sets the "handle" of the SlidingUpPanel
        mSlidingUpPanelLayout.setDragView(dragView);

        // Prevent presses from "bleeding" through below the SlidingUpPanel
        mSlidingContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastSelectedStation == null) {
                    return;
                }

                SlidingUpPanelLayout.PanelState nextState;

                switch (mSlidingUpPanelLayout.getPanelState()) {
                    case COLLAPSED:
                    case HIDDEN:
                        nextState = SlidingUpPanelLayout.PanelState.EXPANDED;
                        break;
                    case EXPANDED:
                    case ANCHORED:
                    default:
                        nextState= SlidingUpPanelLayout.PanelState.COLLAPSED;
                        break;
                }

                mSlidingUpPanelLayout.setPanelState(nextState);
            }
        });


        if (mTorontoFragment == null) {
            mTorontoFragment = new BixeMapFragment();
        }
        setupMapFragment();
    }

    protected void setupMapFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.toronto_fragment, mTorontoFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        mTorontoFragment.setOnMarkerClickListener(this);
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

    private void fadePanelToLight() {
        TextView bikeLabel = (TextView) findViewById(R.id.bikes_amount_label);
        TextView dockLabel = (TextView) findViewById(R.id.docks_amount_label);

        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.green_white_animator);
        animator.setTarget(mCollapsedContentView);
        animator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator2 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator2.setTarget(mStationNameTextView);
        animator2.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator3 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator3.setTarget(bikeLabel);
        animator3.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator4 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator4.setTarget(dockLabel);
        animator4.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator5 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator5.setTarget(mDocksAmountTextView);
        animator5.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator6 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_textcolor_animator);
        animator6.setTarget(mBikesAmountTextView);
        animator6.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator7 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.fab_white_green_animator);
        animator7.setTarget(mLocationFab);
        animator7.setEvaluator(new ArgbEvaluator());

        animator.start();
        animator2.start();
        animator3.start();
        animator4.start();
        animator5.start();
        animator6.start();
        animator7.start();
        mLocationFab.setColorPressedResId(R.color.green_dark_highlight);

        TransitionDrawable transitionDrawable = (TransitionDrawable) mLocationFab.getDrawable();
        transitionDrawable.reverseTransition(250);
    }

    private void fadePanelToDark() {
        TextView bikeLabel = (TextView) findViewById(R.id.bikes_amount_label);
        TextView dockLabel = (TextView) findViewById(R.id.docks_amount_label);

        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.white_green_animator);
        animator.setTarget(mCollapsedContentView);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();

        ObjectAnimator animator2 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator2.setTarget(mStationNameTextView);
        animator2.setEvaluator(new ArgbEvaluator());
        animator2.start();

        ObjectAnimator animator3 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator3.setTarget(bikeLabel);
        animator3.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator4 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator4.setTarget(dockLabel);
        animator4.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator5 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator5.setTarget(mDocksAmountTextView);
        animator5.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator6 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.textcolor_white_animator);
        animator6.setTarget(mBikesAmountTextView);
        animator6.setEvaluator(new ArgbEvaluator());

        ObjectAnimator animator7 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.fab_green_white_animator);
        animator7.setTarget(mLocationFab);
        animator7.setEvaluator(new ArgbEvaluator());

        animator.start();
        animator2.start();
        animator3.start();
        animator4.start();
        animator5.start();
        animator6.start();
        animator7.start();
        mLocationFab.setColorPressedResId(R.color.white_button_press);

        TransitionDrawable transitionDrawable = (TransitionDrawable) mLocationFab.getDrawable();
        transitionDrawable.startTransition(250);
    }

    private void updateStationInfoView(Station station) {

        String stationName = station.getStationName();
        Integer availableBikes = station.getAvailableBikes();
        Integer availableDocks = station.getAvailableDocks();
        Integer totalDocks = station.getTotalDocks();
        mStationLatLng = station.getLatLng();
        mStationName = station.getStationName();

        mBikes = availableBikes;
        mDocks = availableDocks;
        mTotalDocks = totalDocks;

        mStationNameTextView.setText(stationName);
        mBikesAmountTextView.setText(availableBikes.toString());
        mDocksAmountTextView.setText(availableDocks.toString());

        mSlidingUpPanelLayout.setTouchEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
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
}
