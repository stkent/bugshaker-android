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
package com.github.stkent.bugshaker.screenshots;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Func1;

abstract class BaseScreenshotProvider implements ScreenshotProvider {

    @NonNull
    private final Context applicationContext;

    BaseScreenshotProvider(@NonNull final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @NonNull
    abstract Observable<Bitmap> getScreenshotBitmap(@NonNull final Activity activity);

    @NonNull
    @Override
    public final Observable<Uri> getScreenshotUri(@NonNull final Activity activity) {
        return getScreenshotBitmap(activity)
                .flatMap(new Func1<Bitmap, Observable<Uri>>() {
                    @Override
                    public Observable<Uri> call(final Bitmap bitmap) {
                        return ScreenshotUriObservable.create(applicationContext, bitmap);
                    }
                });
    }

    protected final Observable<Bitmap> getNonMapViewsBitmap(@NonNull final Activity activity) {
        return NonMapViewsBitmapObservable.create(activity);
    }

}
