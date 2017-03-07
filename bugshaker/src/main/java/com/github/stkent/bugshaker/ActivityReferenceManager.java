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
package com.github.stkent.bugshaker;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

public final class ActivityReferenceManager {

    @Nullable
    private WeakReference<Activity> wActivity;

    public void setActivity(@NonNull final Activity activity) {
        this.wActivity = new WeakReference<>(activity);
    }

    @Nullable
    public Activity getValidatedActivity() {
        if (wActivity == null) {
            return null;
        }

        final Activity activity = wActivity.get();
        if (!isActivityValid(activity)) {
            return null;
        }

        return activity;
    }

    private boolean isActivityValid(@Nullable final Activity activity) {
        if (activity == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !activity.isFinishing() && !activity.isDestroyed();
        } else {
            return !activity.isFinishing();
        }
    }

}
