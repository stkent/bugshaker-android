package com.github.stkent.bugshaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;

import com.squareup.seismic.ShakeDetector;

import java.lang.ref.WeakReference;

public class CustomApplication extends Application implements ShakeDetector.Listener {

    @Nullable
    private WeakReference<Activity> wActivity;

    @Nullable
    private AlertDialog bugShakerAlertDialog;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            registerActivityLifecycleCallbacks(simpleActivityLifecycleCallbacks);

            final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            final ShakeDetector shakeDetector = new ShakeDetector(this);
            shakeDetector.start(sensorManager);
        }
    }

    @Override
    public void hearShake() {
        // TODO: prompt user; did they really want to submit a bug report?
        // TODO: better to capture screenshot before or after prompt?
        // TODO: how do we handle Maps?
        // TODO: how do we handle activities that do not allow screenshots (for security purposes)?

        if (BuildConfig.DEBUG) {
            showDialog();
        }
    }

    private void showDialog() {
        if (bugShakerAlertDialog != null && bugShakerAlertDialog.isShowing()) {
            return;
        }

        if (wActivity == null) {
            return;
        }

        final Activity currentActivity = wActivity.get();

        if (currentActivity == null) {
            return;
        }

        bugShakerAlertDialog = new AlertDialog.Builder(currentActivity)
                .setTitle("yo!")
                .setMessage("how's it?")
                .setPositiveButton("yey", null)
                .setNegativeButton("cray", null)
                .setCancelable(false)
                .show();
    }

    private SimpleActivityLifecycleCallbacks simpleActivityLifecycleCallbacks = new SimpleActivityLifecycleCallbacks() {
        @Override
        public void onActivityResumed(final Activity activity) {
            wActivity = new WeakReference<>(activity);
        }
    };

}
