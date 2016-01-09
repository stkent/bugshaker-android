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
import android.app.Application;
import android.os.Bundle;

public abstract class ActivityResumedCallback implements Application.ActivityLifecycleCallbacks {

    @Override
    public final void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {

    }

    @Override
    public final void onActivityStarted(final Activity activity) {

    }

    @Override
    public final void onActivityPaused(final Activity activity) {

    }

    @Override
    public final void onActivityStopped(final Activity activity) {

    }

    @Override
    public final void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {

    }

    @Override
    public final void onActivityDestroyed(final Activity activity) {

    }

}
