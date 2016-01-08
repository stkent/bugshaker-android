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
package com.github.stkent.library.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.stkent.library.interfaces.IApplicationDataProvider;
import com.github.stkent.library.interfaces.ILogger;
import com.github.stkent.library.utils.time.SystemTimeProviderWrapper;

public final class FeedbackUtils {

    @NonNull
    private final IApplicationDataProvider applicationDataProvider;

    @NonNull
    private final ILogger logger;

    public FeedbackUtils(
            @NonNull final IApplicationDataProvider applicationDataProvider,
            @NonNull final ILogger logger) {
        this.applicationDataProvider = applicationDataProvider;
        this.logger = logger;
    }

    public void showFeedbackEmailChooser(@Nullable final Activity activity, @NonNull final String emailAddress) {
        if (ActivityStateUtils.isActivityValid(activity)) {
            final Intent feedbackEmailIntent = getFeedbackEmailIntent(emailAddress);

            activity.startActivity(Intent.createChooser(feedbackEmailIntent, "Choose an email provider:"));
            activity.overridePendingTransition(0, 0);
        }
    }

    @NonNull
    public Intent getDummyFeedbackEmailIntent() {
        return getFeedbackEmailIntent("");
    }

    @NonNull
    private Intent getFeedbackEmailIntent(@NonNull final String emailAddress) {
        final String feedbackEmailSubject = Uri.encode("Android App Feedback", "UTF-8");
        final String appInfo = getApplicationInfoString();

        // Uri.Builder is not useful here; see http://stackoverflow.com/a/12035226/2911458
        final String uriString =
                "mailto:" + emailAddress +
                "?subject=" + feedbackEmailSubject +
                "&body=" + appInfo;

        final Uri uri = Uri.parse(uriString);

        return new Intent(Intent.ACTION_SENDTO, uri);
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
                + "Date: " + SystemTimeProviderWrapper.currentTimeMillis();
    }

    private String getAndroidOsVersionDisplayString() {
        return Build.VERSION.RELEASE + " - " + Build.VERSION.SDK_INT;
    }

}
