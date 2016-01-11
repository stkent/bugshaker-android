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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class FeedbackEmailIntentProvider {

    @NonNull
    private final Context applicationContext;

    @NonNull
    private final EmailIntentProvider emailIntentProvider;

    public FeedbackEmailIntentProvider(
            @NonNull final Context applicationContext,
            @NonNull final EmailIntentProvider emailIntentProvider) {

        this.applicationContext = applicationContext;
        this.emailIntentProvider = emailIntentProvider;
    }

    @NonNull
    public Intent getFeedbackEmailIntent(
            @NonNull final String[] emailAddresses,
            @NonNull final String emailSubjectLine) {

        final String appInfo = getApplicationInfoString();

        return emailIntentProvider.getBasicEmailIntent(emailAddresses, emailSubjectLine, appInfo);
    }

    @NonNull
    private String getApplicationInfoString() {
        return    "Device: " + getDeviceName()
                + "\n"
                + "App Version: " + getVersionDisplayString()
                + "\n"
                + "Android OS Version: " + getAndroidOsVersionDisplayString()
                + "\n"
                + "Date: " + getCurrentUtcTimeStringForDate(new Date())
                + "\n"
                + "---------------------"
                + "\n\n\n";
    }

    @NonNull
    private String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        String deviceName;

        if (model.startsWith(manufacturer)) {
            deviceName = StringUtils.capitalize(model);
        } else {
            deviceName = StringUtils.capitalize(manufacturer) + " " + model;
        }

        return deviceName == null ? "Unknown Device" : deviceName;
    }

    @NonNull
    private String getVersionDisplayString() {
        try {
            final PackageManager packageManager = applicationContext.getPackageManager();
            final PackageInfo packageInfo
                    = packageManager.getPackageInfo(applicationContext.getPackageName(), 0);

            final String applicationVersionName = packageInfo.versionName;
            final int applicationVersionCode = packageInfo.versionCode;

            return String.format("%s (%s)", applicationVersionName, applicationVersionCode);
        } catch (final PackageManager.NameNotFoundException e) {
            return "Unknown Version";
        }
    }

    @NonNull
    private String getAndroidOsVersionDisplayString() {
        return Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")";
    }

    @NonNull
    private String getCurrentUtcTimeStringForDate(final Date date) {
        final SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("MMM d, yyyy - h:mm:ss a (z)", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(date);
    }

}
