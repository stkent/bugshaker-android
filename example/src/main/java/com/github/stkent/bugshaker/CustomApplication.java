package com.github.stkent.bugshaker;

import android.app.AlertDialog;
import android.app.Application;
import android.hardware.SensorManager;
import android.view.WindowManager;

import com.squareup.seismic.ShakeDetector;

public class CustomApplication extends Application implements ShakeDetector.Listener {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
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
        final AlertDialog alertDialog =
                new AlertDialog.Builder(this)
                        .setTitle("Hello, world!")
                        .setMessage("You rang?")
                        .setPositiveButton("Yes, I Rang", null)
                        .setNegativeButton("No, you crazy", null)
                        .setCancelable(false)
                        .create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        alertDialog.show();
    }

}
