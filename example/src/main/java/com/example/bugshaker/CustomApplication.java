package com.example.bugshaker;

import android.app.Application;

import com.github.stkent.bugshaker.BugShaker;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BugShaker.get(this)
                 .setEmailAddresses("someone@example.com", "someone.else@example.com")
                 .setEmailSubjectLine("Custom Subject Line")
                 .setLoggingEnabled(BuildConfig.DEBUG)
                 .setIgnoreFlagSecure(true)
                 .start();
    }

}
