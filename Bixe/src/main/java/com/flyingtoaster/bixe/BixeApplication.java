package com.flyingtoaster.bixe;

import android.app.Application;
import android.content.Context;

public class BixeApplication extends Application {
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
    }

    // TODO: Holy crap, get rid of this
    public static Context getAppContext() {
        return sAppContext;
    }
}
