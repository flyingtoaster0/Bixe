package com.flyingtoaster.bixe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by tim on 2014-08-18.
 */
public class BannerAdFragment extends Fragment {
    private View mRootView;
    private ImageView mX;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_banner_ad, container, false);
        mX = (ImageView) mRootView.findViewById(R.id.x_view);
        mAdView = (AdView) mRootView.findViewById(R.id.ad_view);

        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(getString(R.string.note3_id))
                    .build();

            mAdView.loadAd(adRequest);
        }

        mX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.remove_ads_dialog_text));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((MainActivity)getActivity()).launchPurchaseFlow();
                    }
                });

                builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // No op for negative button
                    }
                });

                builder.create().show();
            }
        });

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mAdView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAdView.destroy();
    }
}
