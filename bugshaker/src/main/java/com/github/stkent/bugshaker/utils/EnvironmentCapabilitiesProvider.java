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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public final class EnvironmentCapabilitiesProvider {

    @NonNull
    private final PackageManager packageManager;

    @NonNull
    private final EmailIntentProvider emailIntentProvider;

    @NonNull
    private final Logger logger;

    public EnvironmentCapabilitiesProvider(
            @NonNull final PackageManager packageManager,
            @NonNull final EmailIntentProvider emailIntentProvider,
            @NonNull final Logger logger) {

        this.packageManager = packageManager;
        this.emailIntentProvider = emailIntentProvider;
        this.logger = logger;
    }

    public boolean canSendEmails() {
        logger.d("Checking for email apps...");

        final List<ResolveInfo> emailAppInfoList = getEmailAppList();

        if (emailAppInfoList.isEmpty()) {
            logger.d("No email apps found.");
            return false;
        }

        logEmailAppNames("Available email apps: ", emailAppInfoList);
        return true;
    }

    public boolean canSendEmailsWithAttachments() {
        logger.d("Checking for email apps that can send attachments...");

        final List<ResolveInfo> emailAppInfoList = getEmailWithAttachmentAppList();

        if (emailAppInfoList.isEmpty()) {
            logger.d("No email apps can send attachments.");
            return false;
        }

        logEmailAppNames("Available email apps that can send attachments: ", emailAppInfoList);
        return true;
    }

    @NonNull
    public List<ResolveInfo> getEmailAppList() {
        return packageManager.queryIntentActivities(
                getBasicPlaceholderEmailIntent(), PackageManager.MATCH_DEFAULT_ONLY);
    }

    @NonNull
    public List<ResolveInfo> getEmailWithAttachmentAppList() {
        final Intent placeholderIntent = getBasicPlaceholderEmailIntent();
        placeholderIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<Uri>());

        return packageManager.queryIntentActivities(
                placeholderIntent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    // Private Implementation

    @NonNull
    private Intent getBasicPlaceholderEmailIntent() {
        return emailIntentProvider.getBasicEmailIntent(
                new String[] { "someone@example.com" }, "Any Subject", "Any Body");
    }

    private void logEmailAppNames(
            @NonNull final String prefix,
            @NonNull final List<ResolveInfo> emailAppInfoList) {

        final List<CharSequence> emailAppNames = new ArrayList<>();
        for (final ResolveInfo emailAppInfo: emailAppInfoList) {
            emailAppNames.add(emailAppInfo.loadLabel(packageManager));
        }

        final String emailAppInfoString = TextUtils.join(", ", emailAppNames);
        logger.d(prefix + emailAppInfoString);
    }

}
