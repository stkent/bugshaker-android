package com.github.stkent.bugshaker;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.stkent.bugshaker.flow.email.screenshot.BasicScreenShotProvider;
import com.github.stkent.bugshaker.flow.email.screenshot.ScreenshotProvider;
import com.github.stkent.bugshaker.flow.email.screenshot.maps.MapScreenshotProvider;
import com.github.stkent.bugshaker.utilities.Logger;


public class ScreenshotUtil {

	private static final String SCREENSHOTS_DIRECTORY_NAME = "bug-reports";
	private static final String SCREENSHOT_FILE_NAME = "latest-screenshot.jpg";
	public static final String IMAGE_FILE_NAME = "Screenshot.png";
	public static final String IMAGE_DESCRIPTION = "drawing";

	public static File getScreenshotFile(@NonNull final Context applicationContext) {
		final File screenshotsDir = new File(
			applicationContext.getFilesDir(), SCREENSHOTS_DIRECTORY_NAME);

		screenshotsDir.mkdirs();

		return new File(screenshotsDir, SCREENSHOT_FILE_NAME);
	}

	public static ScreenshotProvider getScreenshotProvider(Application application) {
		Logger logger = new Logger(true);
		try {
			Class.forName(
				"com.google.android.gms.maps.GoogleMap",
				false,
				BugShaker.class.getClassLoader());

			logger.d("Detected that embedding app includes Google Maps as a dependency.");

			return new MapScreenshotProvider(application, logger);
		}
		catch (final ClassNotFoundException e) {
			logger.d("Detected that embedding app does not include Google Maps as a dependency.");

			return new BasicScreenShotProvider(application, logger);
		}
	}
}
