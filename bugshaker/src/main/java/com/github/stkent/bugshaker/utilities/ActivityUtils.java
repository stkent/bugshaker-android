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
package com.github.stkent.bugshaker.utilities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

public final class ActivityUtils {

    public static Window getWindow(@NonNull final Activity activity) {
        return activity.getWindow();
    }

    public static View getRootView(@NonNull final Activity activity) {
        return getWindow(activity).getDecorView().getRootView();
    }

    private ActivityUtils() {

    }

}
