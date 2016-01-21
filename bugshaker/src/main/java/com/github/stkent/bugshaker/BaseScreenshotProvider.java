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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

abstract class BaseScreenshotProvider implements ScreenshotProvider {

    private static final String AUTHORITY_SUFFIX = ".bugshaker.fileprovider";
    private static final String SCREENSHOTS_DIRECTORY_NAME = "bug-reports";
    private static final String SCREENSHOT_FILE_NAME = "latest-screenshot.jpg";
    private static final int JPEG_COMPRESSION_QUALITY = 90;

    @NonNull
    private final Context applicationContext;

    BaseScreenshotProvider(@NonNull final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    abstract Bitmap getScreenshotBitmap(
            @NonNull final Activity activity) throws InvalidActivitySizeException;

    @Override
    public final void getScreenshotUri(
            @NonNull final Activity activity,
            @NonNull final ScreenshotUriCallback callback) {

        OutputStream fileOutputStream = null;

        try {
            final Bitmap screenshotBitmap = getScreenshotBitmap(activity);

            final File screenshotFile = getScreenshotFile();

            fileOutputStream = new BufferedOutputStream(new FileOutputStream(screenshotFile));
            screenshotBitmap.compress(
                    Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION_QUALITY, fileOutputStream);

            fileOutputStream.flush();

            Logger.d("Screenshot successfully saved to file: " + screenshotFile.getAbsolutePath());

            final Uri result = FileProvider.getUriForFile(
                    applicationContext,
                    applicationContext.getPackageName() + AUTHORITY_SUFFIX,
                    screenshotFile);

            Logger.d("URI for screenshot file successfully created: " + result);

            callback.onSuccess(result);
        } catch (final IOException | InvalidActivitySizeException e) {
            callback.onFailure();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (final IOException e) {
                    // We did our best...
                    Logger.printStackTrace(e);
                }
            }
        }
    }

    protected final Bitmap createBitmapOfNonMapViews(
            @NonNull final Activity activity) throws InvalidActivitySizeException {

        final View view = getRootView(activity);

        try {
            final Bitmap screenshotBitmap = Bitmap.createBitmap(
                    view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

            final Canvas canvas = new Canvas(screenshotBitmap);
            view.draw(canvas);

            return screenshotBitmap;
        } catch (final IllegalArgumentException e) {
            throw new InvalidActivitySizeException(e);
        }
    }

    @NonNull
    protected View getRootView(@NonNull final Activity activity) {
        return activity.getWindow().getDecorView().getRootView();
    }

    private File getScreenshotFile() {
        final File screenshotsDir = new File(
                applicationContext.getFilesDir(), SCREENSHOTS_DIRECTORY_NAME);

        //noinspection ResultOfMethodCallIgnored
        screenshotsDir.mkdirs();

        return new File(screenshotsDir, SCREENSHOT_FILE_NAME);
    }

}
