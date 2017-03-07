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

import android.support.annotation.NonNull;
import android.util.Log;

public final class Logger {

    private static final String TAG = "BugShaker-Library";

    private final boolean loggingEnabled;

    public Logger(final boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public void d(@NonNull final CharSequence message) {
        if (loggingEnabled) {
            Log.d(TAG, message.toString());
        }
    }

    public void e(@NonNull final CharSequence message) {
        if (loggingEnabled) {
            Log.e(TAG, message.toString());
        }
    }

    public void printStackTrace(@NonNull final Throwable throwable) {
        if (loggingEnabled) {
            Log.e(TAG, "Logging caught exception", throwable);
        }
    }

}
