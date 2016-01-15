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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

final class EnvironmentCapabilitiesProvider {

    @NonNull
    private final PackageManager packageManager;

    @NonNull
    private final GenericEmailIntentProvider genericEmailIntentProvider;

    EnvironmentCapabilitiesProvider(
            @NonNull final PackageManager packageManager,
            @NonNull final GenericEmailIntentProvider genericEmailIntentProvider) {

        this.packageManager = packageManager;
        this.genericEmailIntentProvider = genericEmailIntentProvider;
    }

    boolean canSendEmails() {
        Logger.d("Checking for email apps...");

        final List<ResolveInfo> emailAppInfoList = getEmailAppList();

        if (emailAppInfoList.isEmpty()) {
            Logger.d("No email apps found.");
            return false;
        }

        logEmailAppNames("Available email apps: ", emailAppInfoList);
        return true;
    }

    boolean canSendEmailsWithAttachments() {
        Logger.d("Checking for email apps that can send attachments...");

        final List<ResolveInfo> emailAppInfoList = getEmailWithAttachmentAppList();

        if (emailAppInfoList.isEmpty()) {
            Logger.d("No email apps can send attachments.");
            return false;
        }

        logEmailAppNames("Available email apps that can send attachments: ", emailAppInfoList);
        return true;
    }

    @NonNull
    private List<ResolveInfo> getEmailAppList() {
        return packageManager.queryIntentActivities(
                getBasicPlaceholderEmailIntent(), PackageManager.MATCH_DEFAULT_ONLY);
    }

    @NonNull
    private List<ResolveInfo> getEmailWithAttachmentAppList() {
        final Intent placeholderIntent = getBasicPlaceholderEmailIntent();
        placeholderIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<Uri>());

        return packageManager.queryIntentActivities(
                placeholderIntent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    @NonNull
    private Intent getBasicPlaceholderEmailIntent() {
        return genericEmailIntentProvider.getBasicEmailIntent(
                new String[] {"someone@example.com"}, "Any Subject", "Any Body");
    }

    private void logEmailAppNames(
            @NonNull final String prefix,
            @NonNull final List<ResolveInfo> emailAppInfoList) {

        final List<CharSequence> emailAppNames = new ArrayList<>();
        for (final ResolveInfo emailAppInfo: emailAppInfoList) {
            emailAppNames.add(emailAppInfo.loadLabel(packageManager));
        }

        final String emailAppInfoString = TextUtils.join(", ", emailAppNames);
        Logger.d(prefix + emailAppInfoString);
    }

}
