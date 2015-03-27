package com.flyingtoaster.bixe;

import android.app.Application;
import android.content.Context;

import com.flyingtoaster.bixe.datasets.BixeContentProvider;
import com.flyingtoaster.bixe.datasets.StationDatabaseHelper;

public class BixeApplication extends Application {
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;

        new BixeContentProvider().onCreate();
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
