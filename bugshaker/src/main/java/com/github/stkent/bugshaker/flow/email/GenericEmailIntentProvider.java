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
package com.github.stkent.bugshaker.flow.email;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public final class GenericEmailIntentProvider {

    @NonNull
    /* default */ Intent getEmailIntent(
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

    @NonNull
    /* default */ Intent getEmailWithAttachmentIntent(
        @NonNull final String[] emailAddresses,
        @NonNull final String emailSubjectLine,
        @NonNull final String emailBody,
        @NonNull final Uri attachmentUri) {

        final Intent result = getEmailIntent(emailAddresses, emailSubjectLine, emailBody);

        result.putExtra(Intent.EXTRA_STREAM, attachmentUri);

        return result;
    }

}
