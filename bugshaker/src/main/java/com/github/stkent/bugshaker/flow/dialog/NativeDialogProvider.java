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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;

import com.github.stkent.bugshaker.R;

public final class NativeDialogProvider implements DialogProvider {

    @NonNull
    @Override
    public Dialog getAlertDialog(
            @NonNull final Activity activity,
            @NonNull final DialogInterface.OnClickListener reportBugClickListener) {

        return new AlertDialog.Builder(activity)
                .setTitle(R.string.bugshaker_dialog_title)
                .setMessage(R.string.bugshaker_dialog_message)
                .setPositiveButton(R.string.bugshaker_dialog_positive_action, reportBugClickListener)
                .setNegativeButton(R.string.bugshaker_dialog_negative_action, null)
                .setCancelable(false)
                .create();
    }

}
