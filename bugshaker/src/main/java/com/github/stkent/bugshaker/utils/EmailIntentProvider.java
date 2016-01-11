package com.github.stkent.bugshaker.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public final class EmailIntentProvider {

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
