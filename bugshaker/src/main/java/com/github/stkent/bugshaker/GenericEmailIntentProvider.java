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

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public final class GenericEmailIntentProvider {

    @NonNull
    public Intent getBasicEmailIntent(
            @NonNull final String[] emailAddresses,
            @NonNull final String emailSubjectLine,
            @NonNull final String emailBody) {

        final Intent result = new Intent(Intent.ACTION_SENDTO);
        result.setData(Uri.parse("mailto:"));
        result.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
        result.putExtra(Intent.EXTRA_SUBJECT, emailSubjectLine);
        result.putExtra(Intent.EXTRA_TEXT, emailBody);
        return result;
    }

}
