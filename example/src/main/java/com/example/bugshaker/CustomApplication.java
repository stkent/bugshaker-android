package com.example.bugshaker;

import android.app.Application;

import com.github.stkent.bugshaker.BugShaker;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BugShaker.get(this)
                 .setEmailAddresses("someone@example.com")   // required
                 .setEmailSubjectLine("Custom Subject Line") // optional
                 .setLoggingEnabled(BuildConfig.DEBUG)       // optional
                 .setIgnoreFlagSecure(true)                  // optional
                 .start();                                   // required
    }

}
