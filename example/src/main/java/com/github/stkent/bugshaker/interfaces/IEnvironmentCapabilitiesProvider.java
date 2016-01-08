package com.github.stkent.bugshaker.interfaces;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface IEnvironmentCapabilitiesProvider {

    boolean canHandleIntent(@NonNull final Intent intent);

}
