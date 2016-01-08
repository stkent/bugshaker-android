package com.github.stkent.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.stkent.library.interfaces.IEnvironmentCapabilitiesProvider;
import com.github.stkent.library.interfaces.ILogger;
import com.github.stkent.library.utils.ApplicationDataProvider;
import com.github.stkent.library.utils.EnvironmentCapabilitiesProvider;
import com.github.stkent.library.utils.FeedbackUtils;
import com.github.stkent.library.utils.Logger;
import com.squareup.seismic.ShakeDetector;

import java.lang.ref.WeakReference;

import static android.content.Context.SENSOR_SERVICE;

public final class BugShaker implements ShakeDetector.Listener {

    private final Application application;
    private final String emailAddress;

    private final ILogger logger;
    private final Context applicationContext;
    private final FeedbackUtils feedbackUtils;

    @Nullable
    private AlertDialog bugShakerAlertDialog;

    @Nullable
    private WeakReference<Activity> wActivity;


    @NonNull
    private ActivityResumedCallback activityResumedCallback = new ActivityResumedCallback() {
        @Override
        public void onActivityResumed(final Activity activity) {
            wActivity = new WeakReference<>(activity);
        }
    };

    @NonNull
    private DialogInterface.OnClickListener reportBugClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            if (wActivity == null) {
                return;
            }

            final Activity activity = wActivity.get();

            if (activity == null) {
                return;
            }

            feedbackUtils.showFeedbackEmailChooser(activity, emailAddress);
        }
    };

    // Constructors

    public BugShaker(@NonNull final Application application, @NonNull final String emailAddress) {
        this.application = application;
        this.emailAddress = emailAddress;

        this.applicationContext = application.getApplicationContext();

        this.logger = new Logger();
        this.feedbackUtils = new FeedbackUtils(new ApplicationDataProvider(applicationContext), logger);
    }

    // Public methods

    public final void start() {
        final IEnvironmentCapabilitiesProvider environmentCapabilitiesProvider
                = new EnvironmentCapabilitiesProvider(applicationContext);

        if (environmentCapabilitiesProvider.canHandleIntent(feedbackUtils.getDummyFeedbackEmailIntent())) {
            application.registerActivityLifecycleCallbacks(activityResumedCallback);

            final SensorManager sensorManager = (SensorManager) applicationContext.getSystemService(SENSOR_SERVICE);
            final ShakeDetector shakeDetector = new ShakeDetector(this);

            final boolean didStart = shakeDetector.start(sensorManager);

            if (didStart) {
                logger.d("Shake detection successfully started!");
            } else {
                logger.e("Error starting shake detection: device hardware does not support detection.");
            }
        } else {
            logger.e("Error starting shake detection: device cannot send emails.");
        }
    }

    // Private implementation

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
                .setPositiveButton("yey", reportBugClickListener)
                .setNegativeButton("cray", null)
                .setCancelable(false)
                .show();
    }

    // ShakeDetector.Listener methods:

    @Override
    public final void hearShake() {
        showDialog();
    }

}
