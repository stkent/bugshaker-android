package com.github.stkent.bugshaker;

import android.app.Application;
import android.hardware.SensorManager;
import android.widget.Toast;

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
        // startActivity();

        Toast.makeText(this, "Shake detected", Toast.LENGTH_SHORT).show();
    }

}
