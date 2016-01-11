/**
 * Copyright 2016 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.stkent.bugshaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.stkent.bugshaker.utils.ApplicationDataProvider;
import com.github.stkent.bugshaker.utils.EnvironmentCapabilitiesProvider;
import com.github.stkent.bugshaker.utils.FeedbackUtils;
import com.github.stkent.bugshaker.utils.Logger;
import com.squareup.seismic.ShakeDetector;

import java.lang.ref.WeakReference;

import static android.content.Context.SENSOR_SERVICE;

public final class BugShaker implements ShakeDetector.Listener {

    private static final String DEFAULT_EMAIL_SUBJECT_LINE = "Android App Feedback";

    private static BugShaker sharedInstance;

    @NonNull
    private final Logger logger = new Logger();

    @NonNull
    private final Application application;

    @NonNull
    private final Context applicationContext;

    @NonNull
    private final FeedbackUtils feedbackUtils;

    private boolean isConfigured = false;
    private String[] emailAddresses;
    private String emailSubjectLine;

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

            feedbackUtils.showFeedbackEmailChooser(activity, emailAddresses, emailSubjectLine);
        }
    };

    public static BugShaker get(@NonNull final Application application) {
        if (sharedInstance == null) {
            synchronized (BugShaker.class) {
                if (sharedInstance == null) {
                    sharedInstance = new BugShaker(application);
                }
            }
        }

        return sharedInstance;
    }

    private BugShaker(@NonNull final Application application) {
        this.application = application;
        this.applicationContext = application.getApplicationContext();
        this.feedbackUtils
                = new FeedbackUtils(new ApplicationDataProvider(applicationContext), logger);
    }

    // Configuration methods

    public BugShaker configure(@NonNull final String emailAddress) {
        return configure(new String[] { emailAddress }, null);
    }

    public BugShaker configure(@NonNull final String[] emailAddresses) {
        return configure(emailAddresses, null);
    }

    public BugShaker configure(
            @NonNull final String emailAddress,
            @Nullable final String emailSubjectLine) {

        return configure(new String[] { emailAddress }, emailSubjectLine);
    }

    public BugShaker configure(
            @NonNull final String[] emailAddresses,
            @Nullable final String emailSubjectLine) {

        this.emailAddresses   = emailAddresses;
        this.emailSubjectLine = emailSubjectLine;
        this.isConfigured     = true;
        return this;
    }

    // Public methods

    public final void start() {
        if (!isConfigured) {
            throw new IllegalStateException("You must call configure before calling start.");
        }

        final EnvironmentCapabilitiesProvider environmentCapabilitiesProvider
                = new EnvironmentCapabilitiesProvider(applicationContext);

        if (environmentCapabilitiesProvider.canHandleIntent(feedbackUtils.getDummyFeedbackEmailIntent())) {
            application.registerActivityLifecycleCallbacks(activityResumedCallback);

            final SensorManager sensorManager
                    = (SensorManager) applicationContext.getSystemService(SENSOR_SERVICE);
            final ShakeDetector shakeDetector = new ShakeDetector(this);

            final boolean didStart = shakeDetector.start(sensorManager);

            if (didStart) {
                logger.d("Shake detection successfully started!");
            } else {
                logger.e("Error starting shake detection: hardware does not support detection.");
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
                .setTitle("Shake detected!")
                .setMessage("Would you like to report a bug?")
                .setPositiveButton("Report", reportBugClickListener)
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }

    // ShakeDetector.Listener methods:

    @Override
    public final void hearShake() {
        showDialog();
    }

}
