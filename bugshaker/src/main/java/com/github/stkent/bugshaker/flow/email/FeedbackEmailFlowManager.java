/*
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
package com.github.stkent.bugshaker.flow.email;

import android.app.Activity;
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
import com.github.stkent.bugshaker.flow.dialog.DialogProvider;
import com.github.stkent.bugshaker.flow.email.screenshot.ScreenshotProvider;
import com.github.stkent.bugshaker.utilities.ActivityUtils;
import com.github.stkent.bugshaker.utilities.Logger;
import com.github.stkent.bugshaker.utilities.Toaster;

import java.util.Arrays;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public final class FeedbackEmailFlowManager {

    private static final int FLAG_SECURE_VALUE = 0x00002000;

    @NonNull
    private final Context applicationContext;

    @NonNull
    private final Toaster toaster;

    @NonNull
    private final ActivityReferenceManager activityReferenceManager;

    @NonNull
    private final EmailCapabilitiesProvider emailCapabilitiesProvider;

    @NonNull
    private final FeedbackEmailIntentProvider feedbackEmailIntentProvider;

    @NonNull
    private final ScreenshotProvider screenshotProvider;

    @NonNull
    private final DialogProvider alertDialogProvider;

    @NonNull
    private final Logger logger;

    @Nullable
    private Dialog alertDialog;

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
                if (emailCapabilitiesProvider.canSendEmailsWithAttachments()) {
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
                logger.d(warningString);

                sendEmailWithoutScreenshot(activity);
            }
        }
    };

    public FeedbackEmailFlowManager(
            @NonNull final Context applicationContext,
            @NonNull final EmailCapabilitiesProvider emailCapabilitiesProvider,
            @NonNull final Toaster toaster,
            @NonNull final ActivityReferenceManager activityReferenceManager,
            @NonNull final FeedbackEmailIntentProvider feedbackEmailIntentProvider,
            @NonNull final ScreenshotProvider screenshotProvider,
            @NonNull final DialogProvider alertDialogProvider,
            @NonNull final Logger logger) {

        this.applicationContext = applicationContext;
        this.emailCapabilitiesProvider = emailCapabilitiesProvider;
        this.toaster = toaster;
        this.activityReferenceManager = activityReferenceManager;
        this.feedbackEmailIntentProvider = feedbackEmailIntentProvider;
        this.screenshotProvider = screenshotProvider;
        this.alertDialogProvider = alertDialogProvider;
        this.logger = logger;
    }

    public void onActivityResumed(@NonNull final Activity activity) {
        dismissDialog();
        activityReferenceManager.setActivity(activity);
    }

    public void onActivityStopped() {
        dismissDialog();
    }

    public void startFlowIfNeeded(
            @NonNull final String[] emailAddresses,
            @Nullable final String emailSubjectLine,
            final boolean ignoreFlagSecure) {

        if (isFeedbackFlowStarted()) {
            logger.d("Feedback flow already started; ignoring shake.");
            return;
        }

        this.emailAddresses = Arrays.copyOf(emailAddresses, emailAddresses.length);
        this.emailSubjectLine = emailSubjectLine;
        this.ignoreFlagSecure = ignoreFlagSecure;

        showDialog();
    }

    private boolean isFeedbackFlowStarted() {
        return alertDialog != null && alertDialog.isShowing();
    }

    private void showDialog() {
        final Activity currentActivity = activityReferenceManager.getValidatedActivity();
        if (currentActivity == null) {
            return;
        }

        alertDialog = alertDialogProvider.getAlertDialog(currentActivity, reportBugClickListener);
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

        if (!isWindowSecured) {
            logger.d("Window is not secured; should attempt to capture screenshot.");
        } else {
            if (ignoreFlagSecure) {
                logger.d("Window is secured, but we're ignoring that.");
            } else {
                logger.d("Window is secured, and we're respecting that.");
            }
        }

        return result;
    }

    private void sendEmailWithScreenshot(
            @NonNull final Activity activity,
            @NonNull final Uri screenshotUri) {

        final Intent feedbackEmailIntent = feedbackEmailIntentProvider.getFeedbackEmailIntent(
                emailAddresses,
                emailSubjectLine,
                screenshotUri);

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

        logger.d("Sending email with screenshot.");
    }

    private void sendEmailWithoutScreenshot(@NonNull final Activity activity) {
        final Intent feedbackEmailIntent = feedbackEmailIntentProvider.getFeedbackEmailIntent(
                emailAddresses,
                emailSubjectLine);

        activity.startActivity(feedbackEmailIntent);

        logger.d("Sending email with no screenshot.");
    }

}
