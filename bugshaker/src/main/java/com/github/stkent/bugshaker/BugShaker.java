/**
 * Copyright 2016 Stuart Kent
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * <p/>
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.stkent.bugshaker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.github.stkent.bugshaker.flow.dialog.AlertDialogType;
import com.github.stkent.bugshaker.flow.dialog.AppCompatDialogProvider;
import com.github.stkent.bugshaker.flow.dialog.DialogProvider;
import com.github.stkent.bugshaker.flow.dialog.NativeDialogProvider;
import com.github.stkent.bugshaker.flow.email.EmailCapabilitiesProvider;
import com.github.stkent.bugshaker.flow.email.FeedbackEmailFlowManager;
import com.github.stkent.bugshaker.utilities.Logger;
import com.github.stkent.bugshaker.utilities.SharedPreferencesUtil;
import com.squareup.seismic.ShakeDetector;

import static android.content.Context.SENSOR_SERVICE;

/**
 * The main interaction point for library users. Encapsulates all shake detection. Setters allow
 * users to customize some aspects (recipients, subject line) of bug report emails.
 */
public final class BugShaker implements ShakeDetector.Listener {

	private static final String RECONFIGURATION_EXCEPTION_MESSAGE =
		"Configuration must be completed before calling assemble or start";

	private static BugShaker sharedInstance;

//	private static final String EMAIL_ADDRESS = "emailAddress";
//	private static final String EMAIL_ADDRESS2 = "emailAddress2";
//
//	private static final String KEY_FOR_EMAIL_ADDRESS = "keyForEmailAddress";
//	private static final String KEY_FOR_EMAIL_ADDRESS2 = "keyForEmailAddress2";

	private final Application application;
	private FeedbackEmailFlowManager feedbackEmailFlowManager;
	private Logger logger;
//	public static String[] emailAddresses;
//	public static String emailSubjectLine;
	private AlertDialogType alertDialogType = AlertDialogType.NATIVE;
	private boolean loggingEnabled = false;

	// Instance configuration state:
	private boolean assembled = false;
	private boolean startAttempted = false;


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



	public BugShaker(@NonNull final Application application) {
		this.application = application;
//
//		SharedPreferences settings = application.getApplicationContext()
//			.getSharedPreferences(EMAIL_ADDRESS, Context.MODE_PRIVATE);

	}

	/**
	 * (Required) Defines one or more email addresses to send bug reports to. This method MUST be
	 * called before calling <code>assemble</code>. This method CANNOT be called after calling
	 * <code>assemble</code> or <code>start</code>.
	 *
	 * @param emailAddresses one or more email addresses
	 * @return the current <code>BugShaker</code> instance (to allow for method chaining)
	 */
	public BugShaker setEmailAddresses(@NonNull final String... emailAddresses) {
		if (assembled || startAttempted) {
			throw new IllegalStateException(
				"Configuration must be complete before calling assemble or start");
		}
		Set<String> stringSet = new HashSet<String>(Arrays.asList(emailAddresses));

//		this.emailAddresses = emailAddresses;
		SharedPreferencesUtil.save(application.getApplicationContext(), stringSet);
		return this;
	}

	/**
	 * (Optional) Defines a custom subject line to use for all bug reports. By default, reports will
	 * use the string defined in <code>DEFAULT_SUBJECT_LINE</code>. This method CANNOT be called
	 * after calling <code>assemble</code> or <code>start</code>.
	 *
	 * @param emailSubjectLine a custom email subject line
	 * @return the current <code>BugShaker</code> instance (to allow for method chaining)
	 */
	public BugShaker setEmailSubjectLine(@NonNull final String emailSubjectLine) {
		if (assembled || startAttempted) {
			throw new IllegalStateException(RECONFIGURATION_EXCEPTION_MESSAGE);
		}

//		this.emailSubjectLine = emailSubjectLine;
		SharedPreferencesUtil.save(application.getApplicationContext(), emailSubjectLine);
		return this;
	}

	/**
	 * (Optional) Defines a dialog type (native/material) to present when a shake is detected.
	 * Native dialogs are used by default. This method CANNOT be called after calling
	 * <code>assemble</code> or <code>start</code>.
	 *
	 * @param alertDialogType the dialog type to present
	 * @return the current <code>BugShaker</code> instance (to allow for method chaining)
	 */
	public BugShaker setAlertDialogType(@NonNull final AlertDialogType alertDialogType) {
		if (assembled || startAttempted) {
			throw new IllegalStateException(RECONFIGURATION_EXCEPTION_MESSAGE);
		}

		this.alertDialogType = alertDialogType;
		return this;
	}

	/**
	 * (Optional) Enables debug and error log messages. Logging is disabled by default. This method
	 * CANNOT be called after calling <code>assemble</code> or <code>start</code>.
	 *
	 * @param loggingEnabled true if logging should be enabled; false otherwise
	 * @return the current <code>BugShaker</code> instance (to allow for method chaining)
	 */
	public BugShaker setLoggingEnabled(final boolean loggingEnabled) {
		if (assembled || startAttempted) {
			throw new IllegalStateException(RECONFIGURATION_EXCEPTION_MESSAGE);
		}

		this.loggingEnabled = loggingEnabled;
		return this;
	}

	/**
	 * (Required) Assembles dependencies based on provided configuration information. This method
	 * CANNOT be called more than once. This method CANNOT be called after calling
	 * <code>start</code>.
	 *
	 * @return the current <code>BugShaker</code> instance (to allow for method chaining)
	 */
	public BugShaker assemble() {
		if (assembled) {
			logger.d("You have already assembled this BugShaker instance. Calling assemble again "
				+ "is a no-op.");

			return this;
		}

		if (startAttempted) {
			throw new IllegalStateException("You can only call assemble before calling start.");
		}

		logger = new Logger(loggingEnabled);

		feedbackEmailFlowManager = new FeedbackEmailFlowManager(
			ScreenshotUtil.getScreenshotProvider(application), application);

		assembled = true;
		return this;
	}

	/**
	 * (Required) Start listening for device shaking. You MUST call <code>assemble</code> before
	 * calling this method.
	 */
	public void start() {
		logger = new Logger(loggingEnabled);
		if (!assembled) {
			throw new IllegalStateException("You MUST call assemble before calling start.");
		}

		if (startAttempted) {
			logger.d("You have already attempted to start this BugShaker instance. Calling start "
				+ "again is a no-op.");

			return;
		}
		if (EmailCapabilitiesProvider.canSendEmails(application.getPackageManager())) {


			application.registerActivityLifecycleCallbacks(simpleActivityLifecycleCallback);

			final SensorManager sensorManager
				= (SensorManager) application.getSystemService(SENSOR_SERVICE);
			final ShakeDetector shakeDetector = new ShakeDetector(this);

			final boolean didStart = shakeDetector.start(sensorManager);

			if (didStart) {
				logger.d("Shake detection successfully started!");
			}
			else {
				logger.e("Error starting shake detection: hardware does not support detection.");
			}
		}
		else {

			logger.e("Error starting shake detection: device cannot send emails.");
		}
		startAttempted = true;
	}

	@Override
	public void hearShake() {
		logger.d("Shake detected!");


		feedbackEmailFlowManager.startFlowIfNeeded(application,
			false);
	}

	private DialogProvider getAlertDialogProvider() {
		if (alertDialogType == AlertDialogType.APP_COMPAT) {
			try {
				Class.forName(
					"android.support.v7.app.AlertDialog",
					false,
					BugShaker.class.getClassLoader());

				logger.d("Using AppCompat dialogs as requested.");

				return new AppCompatDialogProvider();
			}
			catch (final ClassNotFoundException e) {
				logger.e("AppCompat dialogs requested, but class not found.");
				logger.e("Falling back to native dialogs.");

				return new NativeDialogProvider();
			}
		}
		else {
			logger.d("Using native dialogs as requested.");

			return new NativeDialogProvider();
		}
	}

}
