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
package com.github.stkent.bugshaker.flow.email;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.github.stkent.bugshaker.ActivityReferenceManager;
import com.github.stkent.bugshaker.MainActivity;
import com.github.stkent.bugshaker.R;
import com.github.stkent.bugshaker.ScreenshotUtil;
import com.github.stkent.bugshaker.flow.email.screenshot.ScreenshotProvider;
import com.github.stkent.bugshaker.utilities.ActivityUtils;
import com.github.stkent.bugshaker.utilities.FeedbackEmailIntentUtil;
import com.github.stkent.bugshaker.utilities.LogcatUtil;
import com.github.stkent.bugshaker.utilities.Logger;
import com.github.stkent.bugshaker.utilities.Toaster;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public final class FeedbackEmailFlowManager {

	private static final int FLAG_SECURE_VALUE = 0x00002000;

	@Nullable
	private Dialog alertDialog;
	public static String[] emailAddresses;
	public static String emailSubjectLine;
	private boolean ignoreFlagSecure;
	private ScreenshotProvider screenshotProvider;
	private ActivityReferenceManager activityReferenceManager;

	public FeedbackEmailFlowManager(ScreenshotProvider screenshotProvider, Application application) {
		this.screenshotProvider = ScreenshotUtil.getScreenshotProvider(application);
		activityReferenceManager = new ActivityReferenceManager();

	}

	private final OnClickListener screenshotListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialogInterface, int i) {

			final Activity activity = activityReferenceManager.getValidatedActivity();
			final Context context = activity.getBaseContext();
			final Toaster toaster = new Toaster(activity);
			final Logger logger = new Logger(true);
			if (activity == null) {
				return;
			}

			if (shouldAttemptToCaptureScreenshot(activity)) {
				if (EmailCapabilitiesProvider.canSendEmailsWithAttachments(
					activityReferenceManager.getValidatedActivity().getPackageManager())) {
					screenshotProvider.getScreenshotUri(activity)
						.single()
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<Uri>() {
							@Override
							public void onCompleted() {
								// This method intentionally left blank.
							}

							@Override
							public void onError(final Throwable e) {
								final String errorString = "Screenshot capture failed";
								toaster.toast(errorString);
								logger.e(errorString);
								logger.printStackTrace(e);
							}

							@Override
							public void onNext(final Uri uri) {
								startActivity(context);
							}
						});
				}
			}
			else {
				final String warningString = "Window is secured; no screenshot taken";
				toaster.toast(warningString);
				logger.d(warningString);
			}

		}

	};

	private final OnClickListener reportBugClickListener = new OnClickListener() {
		@Override
		public void onClick(final DialogInterface dialog, final int which) {
			final Activity activity = activityReferenceManager.getValidatedActivity();
			final Context context = activity.getBaseContext();
			final Toaster toaster = new Toaster(activity);
			final Logger logger = new Logger(true);
			if (activity == null) {
				return;
			}

			if (shouldAttemptToCaptureScreenshot(activity)) {
				if (EmailCapabilitiesProvider.canSendEmailsWithAttachments
					(activityReferenceManager.getValidatedActivity().getPackageManager())) {
					screenshotProvider.getScreenshotUri(activity)
						.single()
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<Uri>() {
							@Override
							public void onCompleted() {
								// This method intentionally left blank.
							}

							@Override
							public void onError(final Throwable e) {
								final String errorString = "Screenshot capture failed";
								toaster.toast(errorString);
								logger.e(errorString);

								logger.printStackTrace(e);

								sendEmailWithoutScreenshot(context, activity);
							}

							@Override
							public void onNext(final Uri uri) {
								LogcatUtil.saveLogcatToFile(context);
								sendEmailWithScreenshot(activity, uri, Uri.fromFile(LogcatUtil.getLogFile()));
							}


						});
				}
				else {
					sendEmailWithoutScreenshot(context, activity);
				}
			}
			else {
				final String warningString = "Window is secured; no screenshot taken";
				toaster.toast(warningString);
				logger.d(warningString);

				sendEmailWithoutScreenshot(context, activity);
			}
		}
	};

	private void startActivity(Context applicationContext) {
		File screenshotFile = ScreenshotUtil.getScreenshotFile(applicationContext);
		String screenshotFileAbsolutePath = screenshotFile.getAbsolutePath();

		if (screenshotFile.exists()) {
			Intent goToMainActivityIntent = new Intent(applicationContext, MainActivity.class);
			goToMainActivityIntent.putExtra("uri", screenshotFileAbsolutePath);
			goToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			applicationContext.startActivity(goToMainActivityIntent);
		}
		else {
			throw new RuntimeException();
		}
	}

	public void onActivityResumed(@NonNull final Activity activity) {
		dismissDialog();
		activityReferenceManager.setActivity(activity);
	}

	public void onActivityStopped() {
		dismissDialog();
	}

	public void startFlowIfNeeded(Context context,
		final boolean ignoreFlagSecure
	) {
		final Logger logger = new Logger(true);

		if (isFeedbackFlowStarted()) {
			logger.d("Feedback flow already started; ignoring shake.");
			return;
		}

		this.ignoreFlagSecure = ignoreFlagSecure;

		showDialog(context);
	}

	private boolean isFeedbackFlowStarted() {
		return alertDialog != null && alertDialog.isShowing();
	}

	private void showDialog(Context applicationContext) {

		final Activity currentActivity = activityReferenceManager.getValidatedActivity();
		if (currentActivity == null) {
			return;
		}

		AlertDialog.Builder bugAlertBuilder = new AlertDialog.Builder(currentActivity);
		bugAlertBuilder.setMessage(applicationContext.getString(R.string.shake_detected) + '\n' + applicationContext
			.getString(R.string.report_a_bug));
		bugAlertBuilder.setCancelable(false);

		bugAlertBuilder.setPositiveButton(applicationContext.getString(R.string.report), reportBugClickListener);
		bugAlertBuilder.setNegativeButton(applicationContext.getString(R.string.cancel), null);
		bugAlertBuilder
			.setNeutralButton(applicationContext.getString(R.string.annotate_and_report), screenshotListener);

		AlertDialog alertDialog = bugAlertBuilder.create();
		alertDialog.show();

	}

	private void dismissDialog() {
		if (alertDialog != null) {
			alertDialog.dismiss();
			alertDialog = null;
		}
	}

	private boolean shouldAttemptToCaptureScreenshot(@NonNull final Activity activity) {
		final int windowFlags = ActivityUtils.getWindow(activity).getAttributes().flags;

		final boolean isWindowSecured =
			(windowFlags & WindowManager.LayoutParams.FLAG_SECURE) == FLAG_SECURE_VALUE;

		final boolean result = ignoreFlagSecure || !isWindowSecured;
		final Logger logger = new Logger(true);

		if (!isWindowSecured) {
			logger.d("Window is not secured; should attempt to capture screenshot.");
		}
		else {
			if (ignoreFlagSecure) {
				logger.d("Window is secured, but we're ignoring that.");
			}
			else {
				logger.d("Window is secured, and we're respecting that.");
			}
		}

		return result;
	}

	public void sendEmailWithScreenshot(
		@NonNull final Context context,
		@NonNull final Uri screenshotUri, final Uri file) {
		final Intent feedbackEmailIntent = FeedbackEmailIntentUtil
			.getFeedbackEmailIntent(context, emailAddresses, emailSubjectLine, screenshotUri, file);

		final List<ResolveInfo> resolveInfoList = context.getPackageManager()
			.queryIntentActivities(feedbackEmailIntent, PackageManager.MATCH_DEFAULT_ONLY);

		for (final ResolveInfo receivingApplicationInfo : resolveInfoList) {
			context.grantUriPermission(
				receivingApplicationInfo.activityInfo.packageName,
				screenshotUri,
				Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		Intent sendEmailIntent = Intent.createChooser(feedbackEmailIntent, "Send email");
		sendEmailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(sendEmailIntent);
		LogcatUtil.getLogFile().delete();
	}

	private void sendEmailWithoutScreenshot(Context applicationContext, @NonNull final Activity activity) {
		final Intent feedbackEmailIntent = FeedbackEmailIntentUtil
			.getFeedbackEmailIntent(applicationContext,
				emailAddresses, emailSubjectLine);
		final Logger logger = new Logger(true);

		activity.startActivity(feedbackEmailIntent);

		logger.d("Sending email with no screenshot.");
	}
}