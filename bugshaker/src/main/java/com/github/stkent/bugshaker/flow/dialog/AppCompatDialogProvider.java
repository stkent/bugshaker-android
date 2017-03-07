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
package com.github.stkent.bugshaker.flow.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public final class AppCompatDialogProvider implements DialogProvider {

    @NonNull
    @Override
    public Dialog getAlertDialog(
            @NonNull final Activity activity,
            @NonNull final DialogInterface.OnClickListener reportBugClickListener) {

        return new AlertDialog.Builder(activity)
                .setTitle(ALERT_DIALOG_TITLE)
                .setMessage(ALERT_DIALOG_MESSAGE)
                .setPositiveButton(ALERT_DIALOG_POSITIVE_BUTTON, reportBugClickListener)
                .setNegativeButton(ALERT_DIALOG_NEGATIVE_BUTTON, null)
                .setCancelable(ALERT_DIALOG_CANCELABLE)
                .create();
    }

}
