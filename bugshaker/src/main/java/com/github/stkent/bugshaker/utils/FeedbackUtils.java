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
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        result.setData(Uri.parse("mailto:"));

        result.putExtra(Intent.EXTRA_EMAIL,   emailAddresses);
        result.putExtra(Intent.EXTRA_SUBJECT, emailSubjectLine);
        result.putExtra(Intent.EXTRA_TEXT,    appInfo);

        return result;
    }

    @NonNull
    private String getApplicationInfoString() {
        return    "Device: " + applicationDataProvider.getDeviceName()
                + "\n"
                + "App Version: " + applicationDataProvider.getVersionDisplayString()
                + "\n"
                + "Android OS Version: " + getAndroidOsVersionDisplayString()
                + "\n"
                + "Date: " + getCurrentUtcTimeString()
                + "\n"
                + "---------------------"
                + "\n\n\n";
    }

    private String getAndroidOsVersionDisplayString() {
        return Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")";
    }

    @SuppressWarnings("SpellCheckingInspection")
    private String getCurrentUtcTimeString() {
        final SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("MMM d, yyyy - h:mm:ss a (z)", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        final Date currentDate = new Date();

        return simpleDateFormat.format(currentDate);
    }

}
