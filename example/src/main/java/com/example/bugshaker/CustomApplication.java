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
package com.example.bugshaker;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.github.stkent.bugshaker.BugShaker;

import io.fabric.sdk.android.Fabric;

public final class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enabled Crashlytics for release builds only.
        final Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);

        // Perform BugShaker library initialization.
        BugShaker.get(this)
                 .setEmailAddresses("someone@example.com")
                 .setLoggingEnabled(BuildConfig.DEBUG)
                 .start();
    }

}
