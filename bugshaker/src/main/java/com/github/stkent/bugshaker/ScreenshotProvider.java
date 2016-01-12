package com.github.stkent.bugshaker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public final class ScreenshotProvider {

    @NonNull
    private final Logger logger;

    public ScreenshotProvider(@NonNull final Logger logger) {
        this.logger = logger;
    }

    @Nullable
    public Uri getScreenshotUri(@NonNull final Activity activity) {
        final File screenshotFile = new File(getScreenshotFilePath());
        final Bitmap screenshotBitmap = getBitmapFromRootView(activity);

        try {
            final OutputStream fileOutputStream = new FileOutputStream(screenshotFile);
            screenshotBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return Uri.fromFile(screenshotFile);
        } catch (final Exception exception) {
            logger.e(exception.getMessage());

            return null;
        }
    }
    private Bitmap getBitmapFromRootView(@NonNull final Activity activity) {
        final View view = activity.getWindow().getDecorView().getRootView();

        Bitmap screenshotBitmap
                = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(screenshotBitmap);
        view.draw(canvas);

        return screenshotBitmap;
    }

    private String getScreenshotFilePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "BugShaker-Screenshot.jpg";
    }

}
