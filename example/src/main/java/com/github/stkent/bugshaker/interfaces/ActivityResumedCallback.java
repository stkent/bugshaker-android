package com.github.stkent.bugshaker.interfaces;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public abstract class ActivityResumedCallback implements Application.ActivityLifecycleCallbacks {

    @Override
    public final void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {

    }

    @Override
    public final void onActivityStarted(final Activity activity) {

    }

    @Override
    public final void onActivityPaused(final Activity activity) {

    }

    @Override
    public final void onActivityStopped(final Activity activity) {

    }

    @Override
    public final void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {

    }

    @Override
    public final void onActivityDestroyed(final Activity activity) {

    }

}
