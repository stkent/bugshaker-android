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
package com.github.stkent.bugshaker.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class FeedbackUtils {

    @NonNull
    private final ApplicationDataProvider applicationDataProvider;

    @NonNull
    private final Logger logger;

    public FeedbackUtils(
            @NonNull final ApplicationDataProvider applicationDataProvider,
            @NonNull final Logger logger) {

        this.applicationDataProvider = applicationDataProvider;
        this.logger = logger;
    }

    public void showFeedbackEmailChooser(
            @Nullable final Activity activity,
            @NonNull final String[] emailAddresses,
            @NonNull final String emailSubjectLine) {

        if (ActivityStateUtils.isActivityValid(activity)) {
            final Intent feedbackEmailIntent = getFeedbackEmailIntent(emailAddresses, emailSubjectLine);

            activity.startActivity(Intent.createChooser(feedbackEmailIntent, "Choose an email provider:"));
            activity.overridePendingTransition(0, 0);
        }
    }

    @NonNull
    public Intent getDummyFeedbackEmailIntent() {
        return getFeedbackEmailIntent(new String[] { "test@example.com" }, "Test Subject Line");
    }

    @NonNull
    private Intent getFeedbackEmailIntent(
            @NonNull final String[] emailAddresses,
            @NonNull final String emailSubjectLine) {

        final String appInfo = getApplicationInfoString();

        final Intent result = new Intent(Intent.ACTION_SENDTO);
        result.putExtra(Intent.EXTRA_EMAIL,   emailAddresses);
        result.putExtra(Intent.EXTRA_SUBJECT, emailSubjectLine);
        result.putExtra(Intent.EXTRA_TEXT,    appInfo);

        return result;
    }

    @NonNull
    private String getApplicationInfoString() {
        String applicationVersionDisplayString;

        try {
            applicationVersionDisplayString = applicationDataProvider.getVersionDisplayString();
        } catch (PackageManager.NameNotFoundException e) {
            applicationVersionDisplayString = "Unknown";
        }

        return    "\n\n\n"
                + "---------------------"
                + "\n"
                + "Device: " + applicationDataProvider.getDeviceName()
                + "\n"
                + "App Version: " + applicationVersionDisplayString
                + "\n"
                + "Android OS Version: " + getAndroidOsVersionDisplayString()
                + "\n"
                + "Date: " + System.currentTimeMillis();
    }

    private String getAndroidOsVersionDisplayString() {
        return Build.VERSION.RELEASE + " - " + Build.VERSION.SDK_INT;
    }

}
