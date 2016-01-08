package com.github.stkent.library.interfaces;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface IEnvironmentCapabilitiesProvider {

    boolean canHandleIntent(@NonNull final Intent intent);

}
