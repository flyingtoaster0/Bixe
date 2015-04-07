package com.flyingtoaster.bixe.activities;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;

import com.flyingtoaster.bixe.datasets.BixeContentProvider;

public class ContentResolverActivity extends Activity {

    private boolean updated = false;
    private ContentObserver mContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                updated = true;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        getContentResolver().registerContentObserver(BixeContentProvider.CONTENT_URI, false, mContentObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    public boolean didUpdate() {
        return updated;
    }

    public ContentObserver getContentObserver() {
        return mContentObserver;
    }
}
