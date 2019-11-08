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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.stkent.bugshaker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class FeedbackEmailIntentProvider {

    @NonNull
    private final Context context;

    @NonNull
    private final GenericEmailIntentProvider genericEmailIntentProvider;

    @NonNull
    private final App app;

    @NonNull
    private final Environment environment;

    @NonNull
    private final Device device;

    public FeedbackEmailIntentProvider(
            @NonNull final Context context,
            @NonNull final GenericEmailIntentProvider genericEmailIntentProvider) {

        this.context = context;
        this.genericEmailIntentProvider = genericEmailIntentProvider;
        this.app = new App(context);
        this.environment = new Environment();
        this.device = new Device(context);
    }

    @NonNull
    /* default */ Intent getFeedbackEmailIntent(
            @NonNull final String[] emailAddresses,
            @Nullable final String userProvidedEmailSubjectLine) {

        final String emailSubjectLine = getEmailSubjectLine(userProvidedEmailSubjectLine);
        final String emailBody = getApplicationInfoString(app, environment, device);

        return genericEmailIntentProvider
                .getEmailIntent(emailAddresses, emailSubjectLine, emailBody);
    }

    @NonNull
    /* default */ Intent getFeedbackEmailIntent(
            @NonNull final String[] emailAddresses,
            @Nullable final String userProvidedEmailSubjectLine,
            @NonNull final Uri screenshotUri) {

        final String emailSubjectLine = getEmailSubjectLine(userProvidedEmailSubjectLine);
        final String emailBody = getApplicationInfoString(app, environment, device);

        return genericEmailIntentProvider
                .getEmailWithAttachmentIntent(
                        emailAddresses, emailSubjectLine, emailBody, screenshotUri);
    }

    @NonNull
    private String getApplicationInfoString(
            @NonNull final App app,
            @NonNull final Environment environment,
            @NonNull final Device device) {

        final String androidVersionString = String.format(
                "%s (%s)",
                environment.getAndroidVersionName(),
                environment.getAndroidVersionCode()
        );

        final String appVersionString = String.format(
                "%s (%s)",
                app.getVersionName(),
                app.getVersionCode()
        );

        return context.getString(
                R.string.bugshaker_email_body_time_stamp,
                getCurrentUtcTimeStringForDate(new Date())
        ) + context.getString(
                R.string.bugshaker_email_body_app_version,
                appVersionString
        ) + context.getString(
                R.string.bugshaker_email_body_install_source,
                app.getInstallSource()
        ) + context.getString(
                R.string.bugshaker_email_body_android_version,
                androidVersionString
        ) + context.getString(
                R.string.bugshaker_email_body_device_manufacturer,
                device.getManufacturer()
        ) + context.getString(
                R.string.bugshaker_email_body_device_model,
                device.getModel()
        ) + context.getString(
                R.string.bugshaker_email_body_display_resolution,
                device.getResolution()
        ) + context.getString(
                R.string.bugshaker_email_body_display_density_actual,
                device.getActualDensity()
        ) + context.getString(
                R.string.bugshaker_email_body_display_density_bucket,
                device.getDensityBucket()
        ) + "---------------------\n\n";
    }

    @NonNull
    private String getEmailSubjectLine(@Nullable final String userProvidedEmailSubjectLine) {
        if (userProvidedEmailSubjectLine != null) {
            return userProvidedEmailSubjectLine;
        }

        return context.getString(
                R.string.bugshaker_email_default_subject_line,
                app.getName()
        );
    }

    @NonNull
    private String getCurrentUtcTimeStringForDate(final Date date) {
        final SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z", Locale.getDefault());

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(date);
    }

}
