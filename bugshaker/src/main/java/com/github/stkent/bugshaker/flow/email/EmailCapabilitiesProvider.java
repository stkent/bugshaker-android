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

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.stkent.bugshaker.utilities.GenericEmailIntentUtil;
import com.github.stkent.bugshaker.utilities.Logger;

public final class EmailCapabilitiesProvider {

	private static final String[] DUMMY_EMAIL_ADDRESSES = new String[] { "someone@example.com" };
	private static final String DUMMY_EMAIL_SUBJECT_LINE = "Any Subject Line";
	private static final String DUMMY_EMAIL_BODY = "Any Body";
	private static final Uri DUMMY_EMAIL_URI = Uri.EMPTY;

	@NonNull
	private static List<ResolveInfo> getEmailAppList(PackageManager packageManager) {
		final Intent queryIntent = GenericEmailIntentUtil.getEmailIntent(
			DUMMY_EMAIL_ADDRESSES, DUMMY_EMAIL_SUBJECT_LINE, DUMMY_EMAIL_BODY);
		return packageManager.queryIntentActivities(
			queryIntent, PackageManager.MATCH_DEFAULT_ONLY);
	}

	public static boolean canSendEmails(PackageManager packageManager) {
		Logger logger = new Logger(true);
		logger.d("Checking for email apps...");
		final List<ResolveInfo> emailAppInfoList = getEmailAppList(packageManager);
		if (emailAppInfoList.isEmpty()) {
			logger.d("No email apps found.");
			return false;
		}
		logEmailAppNames(packageManager, "Available email apps: ", emailAppInfoList);
		return true;
	}

	static boolean canSendEmailsWithAttachments(PackageManager packageManager) {
		Logger logger = new Logger(true);
		logger.d("Checking for email apps that can send attachments...");

		final List<ResolveInfo> emailAppInfoList = getEmailWithAttachmentAppList(packageManager);

		if (emailAppInfoList.isEmpty()) {
			logger.d("No email apps can send attachments.");
			return false;
		}

		logEmailAppNames(packageManager, "Available email apps that can send attachments: ", emailAppInfoList);
		return true;
	}

	@NonNull
	static private List<ResolveInfo> getEmailWithAttachmentAppList(PackageManager packageManager) {
		final Intent queryIntent = GenericEmailIntentUtil.getEmailWithAttachmentIntent(
			DUMMY_EMAIL_ADDRESSES, DUMMY_EMAIL_SUBJECT_LINE, DUMMY_EMAIL_BODY, DUMMY_EMAIL_URI);

		return packageManager.queryIntentActivities(
			queryIntent, PackageManager.MATCH_DEFAULT_ONLY);
	}

	static private void logEmailAppNames(PackageManager packageManager,
		@NonNull final String prefix,
		@NonNull final List<ResolveInfo> emailAppInfoList) {

		final List<CharSequence> emailAppNames = new ArrayList<>();
		for (final ResolveInfo emailAppInfo : emailAppInfoList) {
			emailAppNames.add(emailAppInfo.loadLabel(packageManager));
		}

		final String emailAppInfoString = TextUtils.join(", ", emailAppNames);
		Logger logger = new Logger(true);
		logger.d(prefix + emailAppInfoString);
	}

}
