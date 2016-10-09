package com.flyingtoaster.bixe;

import android.app.Application;
import android.content.Context;

public class BixeApplication extends Application {
    private static BixeApplication sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }

    public static BixeApplication getAppContext() {
        return sApplication;
    }
}
