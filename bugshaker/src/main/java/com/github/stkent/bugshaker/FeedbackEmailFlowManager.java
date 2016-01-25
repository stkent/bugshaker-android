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

import java.util.Arrays;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

final class FeedbackEmailFlowManager {

    private static final int FLAG_SECURE_VALUE = 0x00002000;

    @NonNull
    private final Context applicationContext;

    @NonNull
    private final Toaster toaster;

    @NonNull
    private final ActivityReferenceManager activityReferenceManager;

    @NonNull
    private final EnvironmentCapabilitiesProvider environmentCapabilitiesProvider;

    @NonNull
    private final FeedbackEmailIntentProvider feedbackEmailIntentProvider;

    @NonNull
    private final ScreenshotProvider screenshotProvider;

    @Nullable
    private AlertDialog bugShakerAlertDialog;

    private String[] emailAddresses;
    private String emailSubjectLine;
    private boolean ignoreFlagSecure;

    private final OnClickListener reportBugClickListener = new OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            final Activity activity = activityReferenceManager.getValidatedActivity();
            if (activity == null) {
                return;
            }

            if (shouldAttemptToCaptureScreenshot(activity)) {
                if (environmentCapabilitiesProvider.canSendEmailsWithAttachments()) {
                    screenshotProvider.getScreenshotUri(activity)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Subscriber<Uri>() {
                                @Override
                                public void onCompleted() {
                                    // This method intentionally left blank.
                                }

                                @Override
                                public void onError(final Throwable e) {
                                    final String errorString = "Screenshot capture failed";
                                    toaster.toast(errorString);
                                    Logger.e(errorString);

                                    sendEmailWithoutScreenshot(activity);
                                }

                                @Override
                                public void onNext(final Uri uri) {
                                    sendEmailWithScreenshot(activity, uri);
                                }
                            });
                } else {
                    sendEmailWithoutScreenshot(activity);
                }
            } else {
                final String warningString = "Window is secured; no screenshot taken";

                toaster.toast(warningString);
                Logger.d(warningString);

                sendEmailWithoutScreenshot(activity);
            }
        }
    };

    FeedbackEmailFlowManager(
            @NonNull final Context applicationContext,
            @NonNull final EnvironmentCapabilitiesProvider environmentCapabilitiesProvider,
            @NonNull final Toaster toaster,
            @NonNull final ActivityReferenceManager activityReferenceManager,
            @NonNull final FeedbackEmailIntentProvider feedbackEmailIntentProvider,
            @NonNull final ScreenshotProvider screenshotProvider) {

        this.applicationContext = applicationContext;
        this.environmentCapabilitiesProvider = environmentCapabilitiesProvider;
        this.toaster = toaster;
        this.activityReferenceManager = activityReferenceManager;
        this.feedbackEmailIntentProvider = feedbackEmailIntentProvider;
        this.screenshotProvider = screenshotProvider;
    }

    void onActivityResumed(@NonNull final Activity activity) {
        dismissDialog();
        activityReferenceManager.setActivity(activity);
    }

    void onActivityStopped() {
        dismissDialog();
    }

    void startFlowIfNeeded(
            @NonNull final String[] emailAddresses,
            @Nullable final String emailSubjectLine,
            final boolean ignoreFlagSecure) {

        if (isFeedbackFlowStarted()) {
            Logger.d("Feedback flow already started; ignoring shake.");
            return;
        }

        this.emailAddresses = Arrays.copyOf(emailAddresses, emailAddresses.length);
        this.emailSubjectLine = emailSubjectLine;
        this.ignoreFlagSecure = ignoreFlagSecure;

        showDialog();
    }

    private boolean isFeedbackFlowStarted() {
        return bugShakerAlertDialog != null && bugShakerAlertDialog.isShowing();
    }

    private void showDialog() {
        final Activity currentActivity = activityReferenceManager.getValidatedActivity();
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

    private void dismissDialog() {
        if (bugShakerAlertDialog != null) {
            bugShakerAlertDialog.dismiss();
            bugShakerAlertDialog = null;
        }
    }

    private boolean shouldAttemptToCaptureScreenshot(@NonNull final Activity activity) {
        final int windowFlags = ActivityUtils.getWindow(activity).getAttributes().flags;

        final boolean isWindowSecured =
                (windowFlags & WindowManager.LayoutParams.FLAG_SECURE) == FLAG_SECURE_VALUE;

        final boolean result = ignoreFlagSecure || !isWindowSecured;

        if (!isWindowSecured) {
            Logger.d("Window is not secured; should attempt to capture screenshot.");
        } else {
            if (ignoreFlagSecure) {
                Logger.d("Window is secured, but we're ignoring that.");
            } else {
                Logger.d("Window is secured, and we're respecting that.");
            }
        }

        return result;
    }

    private void sendEmailWithScreenshot(
            @NonNull final Activity activity,
            @NonNull final Uri screenshotUri) {

        final Intent feedbackEmailIntent = feedbackEmailIntentProvider
                .getFeedbackEmailIntent(emailAddresses, emailSubjectLine, screenshotUri);

        final List<ResolveInfo> resolveInfoList = applicationContext.getPackageManager()
                .queryIntentActivities(feedbackEmailIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (final ResolveInfo receivingApplicationInfo: resolveInfoList) {
            // FIXME: revoke these permissions at some point!
            applicationContext.grantUriPermission(
                    receivingApplicationInfo.activityInfo.packageName,
                    screenshotUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        activity.startActivity(feedbackEmailIntent);

        Logger.d("Sending email with screenshot.");
    }

    private void sendEmailWithoutScreenshot(@NonNull final Activity activity) {
        final Intent feedbackEmailIntent = feedbackEmailIntentProvider
                .getFeedbackEmailIntent(emailAddresses, emailSubjectLine);

        activity.startActivity(feedbackEmailIntent);

        Logger.d("Sending email with no screenshot.");
    }

}
