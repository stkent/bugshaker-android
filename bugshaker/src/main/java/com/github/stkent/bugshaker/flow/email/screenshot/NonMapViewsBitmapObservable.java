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
package com.github.stkent.bugshaker.flow.email.screenshot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.Window;

import com.github.stkent.bugshaker.utilities.ActivityUtils;

import rx.Observable;
import rx.Subscriber;

final class NonMapViewsBitmapObservable {

    private static final Paint VIEW_PAINT = new Paint();

    static {
        VIEW_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    @NonNull
    /* default */ static Observable<Bitmap> create(@NonNull final Activity activity) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                try {
                    final Bitmap screenBitmap = getScreenSizedBitmap(activity);

                    drawPositionedViewOnScreenBitmap(
                            ActivityUtils.getRootView(activity), screenBitmap);

                    subscriber.onNext(screenBitmap);
                    subscriber.onCompleted();
                } catch (final IllegalArgumentException e) {
                    final Exception exception = new InvalidActivitySizeException(e);
                    subscriber.onError(exception);
                }
            }
        });
    }

    private static void drawPositionedViewOnScreenBitmap(
            @NonNull final View view,
            @NonNull final Bitmap screenBitmap) {

        final Bitmap viewBitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas viewCanvas = new Canvas(viewBitmap);
        view.draw(viewCanvas);

        final int[] viewLocationOnScreen = new int[] {0, 0};
        view.getLocationOnScreen(viewLocationOnScreen);

        final Canvas screenCanvas = new Canvas(screenBitmap);
        screenCanvas.drawBitmap(
                viewBitmap,
                viewLocationOnScreen[0],
                viewLocationOnScreen[1],
                VIEW_PAINT);
    }

    @NonNull
    private static Bitmap getScreenSizedBitmap(@NonNull final Activity activity) {
        final Window window = ActivityUtils.getWindow(activity);
        final Display screen = window.getWindowManager().getDefaultDisplay();
        final Point screenSize = new Point();
        screen.getSize(screenSize);

        return Bitmap.createBitmap(screenSize.x, screenSize.y, Bitmap.Config.ARGB_8888);
    }

    private NonMapViewsBitmapObservable() {

    }

}
