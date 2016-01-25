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
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

final class MapScreenshotProvider extends BaseScreenshotProvider {

    MapScreenshotProvider(@NonNull final Context applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void getScreenshotBitmap(
            @NonNull final Activity activity,
            @NonNull final ScreenshotBitmapCallback callback) {

        final View view = getRootView(activity);
        final List<MapView> mapViews = locateMapViewsInHierarchy(view);

        if (mapViews.isEmpty()) {
            try {
                callback.onSuccess(createBitmapOfNonMapViews(activity));
            } catch (final InvalidActivitySizeException e) {
                Logger.printStackTrace(e);
                callback.onFailure();
            }
        } else {
            mapViews.get(0).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(final Bitmap bitmap) {
                            callback.onSuccess(bitmap);
                        }
                    });
                }
            });
        }
    }

    @NonNull
    private List<MapView> locateMapViewsInHierarchy(@NonNull final View view) {
        final List<MapView> result = new ArrayList<>();

        if (view instanceof MapView && view.getVisibility() == View.VISIBLE) {
            // Yes, MapView is a ViewGroup, but I never want to see anyone nesting a MapView
            // inside another MapView...
            result.add((MapView) view);
        } else if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;

            for (int childIndex = 0; childIndex < viewGroup.getChildCount(); childIndex++) {
                result.addAll(locateMapViewsInHierarchy(viewGroup.getChildAt(childIndex)));
            }
        }

        return result;
    }

}
