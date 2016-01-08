package com.github.stkent.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

public class ApplicationDataProvider {

    @NonNull
    private final Context applicationContext;

    public ApplicationDataProvider(@NonNull final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @NonNull
    public String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        String deviceName;

        if (model.startsWith(manufacturer)) {
            deviceName = StringUtils.capitalize(model);
        } else {
            deviceName = StringUtils.capitalize(manufacturer) + " " + model;
        }

        return deviceName == null ? "Unknown Device" : deviceName;
    }

    @NonNull
    public String getVersionDisplayString() throws PackageManager.NameNotFoundException {
        final PackageInfo packageInfo = ApplicationUtils.getPackageInfo(applicationContext);
        final String applicationVersionName = packageInfo.versionName;
        final int applicationVersionCode = packageInfo.versionCode;

        return String.format("%s (%s)", applicationVersionName, applicationVersionCode);
    }

}
