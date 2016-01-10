package com.github.stkent.bugshaker;

import android.app.Application;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final BugShaker bugShaker = new BugShaker(this, "someone@example.com");
        bugShaker.start();
    }

}
