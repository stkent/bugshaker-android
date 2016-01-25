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
import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.squareup.seismic.ShakeDetector;

import static android.content.Context.SENSOR_SERVICE;

/**
 * The main interaction point for library users. Encapsulates all shake detection. Setters allow
 * users to customize some aspects (recipients, subject line) of bug report emails.
 */
public final class BugShaker implements ShakeDetector.Listener {

    private static BugShaker sharedInstance;

    private final Application application;
    private final Context applicationContext;
    private final EnvironmentCapabilitiesProvider environmentCapabilitiesProvider;
    private final FeedbackEmailFlowManager feedbackEmailFlowManager;

    private boolean isConfigured = false;
    private String[] emailAddresses;
    private String emailSubjectLine;
    private boolean ignoreFlagSecure = false;

    private final SimpleActivityLifecycleCallback simpleActivityLifecycleCallback
            = new SimpleActivityLifecycleCallback() {

        @Override
        public void onActivityResumed(final Activity activity) {
            feedbackEmailFlowManager.onActivityResumed(activity);
        }

        @Override
        public void onActivityStopped(final Activity activity) {
            feedbackEmailFlowManager.onActivityStopped();
        }
    };

    /**
     * @param application the embedding application
     * @return the singleton <code>BugShaker</code> instance
     */
    public static BugShaker get(@NonNull final Application application) {
        synchronized (BugShaker.class) {
            if (sharedInstance == null) {
                sharedInstance = new BugShaker(application);
            }
        }

        return sharedInstance;
    }

    private BugShaker(@NonNull final Application application) {
        this.application = application;
        this.applicationContext = application.getApplicationContext();

        final GenericEmailIntentProvider genericEmailIntentProvider
                = new GenericEmailIntentProvider();

        this.environmentCapabilitiesProvider = new EnvironmentCapabilitiesProvider(
                applicationContext.getPackageManager(), genericEmailIntentProvider);

        this.feedbackEmailFlowManager = new FeedbackEmailFlowManager(
                applicationContext,
                environmentCapabilitiesProvider,
                new Toaster(applicationContext),
                new ActivityReferenceManager(),
                new FeedbackEmailIntentProvider(applicationContext, genericEmailIntentProvider),
                getScreenshotProvider());
    }

    /**
     * Defines one or more email addresses to send bug reports to. This method MUST be called before
     * calling <code>start</code>.
     *
     * @param emailAddresses one or more email addresses
     * @return the current <code>BugShaker</code> instance (to allow for method chaining)
     */
    public BugShaker setEmailAddresses(@NonNull final String... emailAddresses) {
        this.emailAddresses   = emailAddresses;
        this.isConfigured     = true;
        return this;
    }

    /**
     * (Optionally) defines a custom subject line to use for all bug reports. By default, reports
     * will use the string defined in <code>DEFAULT_SUBJECT_LINE</code>.
     *
     * @param emailSubjectLine a custom email subject line
     * @return the current <code>BugShaker</code> instance (to allow for method chaining)
     */
    public BugShaker setEmailSubjectLine(@NonNull final String emailSubjectLine) {
        this.emailSubjectLine = emailSubjectLine;
        return this;
    }

    /**
     * (Optionally) enables debug and error log messages. Logging is disabled by default.
     *
     * @param enabled true if logging should be enabled; false otherwise
     * @return the current <code>BugShaker</code> instance (to allow for method chaining)
     */
    public BugShaker setLoggingEnabled(final boolean enabled) {
        Logger.setLoggingEnabled(enabled);
        return this;
    }

    /**
     * @param ignoreFlagSecure true if screenshots should be allowed even when
     *                         <code>FLAG_SECURE</code> is set on the current <code>Window</code>;
     *                         false otherwise
     * @return the current <code>BugShaker</code> instance (to allow for method chaining)
     */
    public BugShaker setIgnoreFlagSecure(final boolean ignoreFlagSecure) {
        this.ignoreFlagSecure = ignoreFlagSecure;
        return this;
    }

    /**
     * Start listening for shakes. You MUST call <code>setEmailAddresses</code> before calling this
     * method.
     */
    public void start() {
        if (!isConfigured) {
            throw new IllegalStateException(
                    "You MUST call setEmailAddresses before calling start.");
        }

        if (environmentCapabilitiesProvider.canSendEmails()) {
            application.registerActivityLifecycleCallbacks(simpleActivityLifecycleCallback);

            final SensorManager sensorManager
                    = (SensorManager) applicationContext.getSystemService(SENSOR_SERVICE);
            final ShakeDetector shakeDetector = new ShakeDetector(this);

            final boolean didStart = shakeDetector.start(sensorManager);

            if (didStart) {
                Logger.d("Shake detection successfully started!");
            } else {
                Logger.e("Error starting shake detection: hardware does not support detection.");
            }
        } else {
            Logger.e("Error starting shake detection: device cannot send emails.");
        }
    }

    @Override
    public void hearShake() {
        Logger.d("Shake detected!");

        feedbackEmailFlowManager.startFlowIfNeeded(
                emailAddresses,
                emailSubjectLine,
                ignoreFlagSecure);
    }

    /**
     * @return a MapScreenshotProvider if the embedding application utilizes the Google Maps Android
     *         API, and a BasicScreenshotProvider otherwise
     */
    private ScreenshotProvider getScreenshotProvider() {
        // See http://stackoverflow.com/a/3466596/2911458
        try {
            Class.forName(
                    "com.google.android.gms.maps.GoogleMap",
                    false,
                    BugShaker.class.getClassLoader());

            Logger.d("Detected that embedding app includes Google Maps as a dependency.");

            return new MapScreenshotProvider(applicationContext);
        } catch (final ClassNotFoundException e) {
            Logger.d("Detected that embedding app does not include Google Maps as a dependency.");

            return new BasicScreenShotProvider(applicationContext);
        }
    }

}
