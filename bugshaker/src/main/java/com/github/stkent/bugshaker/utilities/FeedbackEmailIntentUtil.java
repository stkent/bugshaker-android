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
package com.github.stkent.bugshaker.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.stkent.bugshaker.R;

public final class FeedbackEmailIntentUtil {

	private static final String DEFAULT_EMAIL_SUBJECT_LINE_SUFFIX = " Android App Feedback";

	@NonNull
	public static Intent getFeedbackEmailIntent(
		Context applicationContext,
		@NonNull final String[] emailAddresses,
		@Nullable final String userProvidedEmailSubjectLine) {

		final String appInfo = getApplicationInfoString(applicationContext);
		final String emailSubjectLine = getEmailSubjectLine(applicationContext, userProvidedEmailSubjectLine);

		return GenericEmailIntentUtil
			.getEmailIntent(emailAddresses, emailSubjectLine, appInfo);
	}

	@NonNull
	public static Intent getFeedbackEmailIntent(
		Context applicationContext,
		@NonNull final String[] emailAddresses,
		@Nullable final String userProvidedEmailSubjectLine,
		@NonNull final Uri screenshotUri,
		@NonNull final Uri fileName
	) {

		final String appInfo = getApplicationInfoString(applicationContext);
		final String emailSubjectLine = getEmailSubjectLine(applicationContext, userProvidedEmailSubjectLine);

		return GenericEmailIntentUtil
			.getEmailWithAttachmentIntent(
				emailAddresses, emailSubjectLine, appInfo, screenshotUri, fileName);
	}


	@NonNull
	public static CharSequence getApplicationName(Context applicationContext) {
		return applicationContext.getApplicationInfo()
			.loadLabel(applicationContext.getPackageManager());
	}

	@NonNull
	public static String getApplicationInfoString(Context applicationContext) {
		return applicationContext.getString(R.string.my_device) + getDeviceName()
			+ "\n"
			+ applicationContext.getString(R.string.app_version) + getVersionDisplayString(applicationContext)
			+ "\n"
			+ applicationContext.getString(R.string.android_version) + getAndroidOsVersionDisplayString()
			+ "\n"
			+ applicationContext.getString(R.string.time_stamp) + getCurrentUtcTimeStringForDate(new Date())
			+ "\n"
			+ applicationContext.getString(R.string.id) + Build.ID
			+ "\n"
			+ applicationContext.getString(R.string.display) + Build.DISPLAY
			+ "---------------------"
			+ "\n\n";
	}

	@NonNull
	public static String getEmailSubjectLine(Context applicationContext,
		@Nullable final String userProvidedEmailSubjectLine) {
		if (userProvidedEmailSubjectLine != null) {
			return userProvidedEmailSubjectLine;
		}

		return getApplicationName(applicationContext) + DEFAULT_EMAIL_SUBJECT_LINE_SUFFIX;
	}

	@NonNull
	public static String getDeviceName() {
		final String manufacturer = Build.MANUFACTURER;
		final String model = Build.MODEL;

		String deviceName;

		if (model.startsWith(manufacturer)) {
			deviceName = model;
		}
		else {
			deviceName = manufacturer + " " + model;
		}

		return StringUtils.capitalizeFully(deviceName);
	}

	@NonNull
	public static String getVersionDisplayString(Context applicationContext) {
		try {
			final PackageManager packageManager = applicationContext.getPackageManager();
			final PackageInfo packageInfo
				= packageManager.getPackageInfo(applicationContext.getPackageName(), 0);

			final String applicationVersionName = packageInfo.versionName;
			final int applicationVersionCode = packageInfo.versionCode;

			return String.format("%s (%s)", applicationVersionName, applicationVersionCode);
		}
		catch (final PackageManager.NameNotFoundException e) {
			return "Unknown Version";
		}
	}

	@NonNull
	public static String getAndroidOsVersionDisplayString() {
		return Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")";
	}

	@NonNull
	public static String getCurrentUtcTimeStringForDate(final Date date) {
		final SimpleDateFormat simpleDateFormat
			= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z", Locale.getDefault());

		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		return simpleDateFormat.format(date);
	}

}
