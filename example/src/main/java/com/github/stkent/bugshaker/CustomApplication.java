package com.github.stkent.bugshaker;

import android.app.Application;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
//            final BugShaker bugShaker = new BugShaker(this, "stuart@detroitlabs.com");
//            bugShaker.start();
        }
    }

}
