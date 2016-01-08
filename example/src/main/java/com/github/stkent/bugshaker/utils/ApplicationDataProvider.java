package com.github.stkent.bugshaker.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.github.stkent.bugshaker.interfaces.IApplicationDataProvider;

public class ApplicationDataProvider implements IApplicationDataProvider {

    @NonNull
    private final Context applicationContext;

    public ApplicationDataProvider(@NonNull final Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @NonNull
    @Override
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
    @Override
    public String getVersionDisplayString() throws PackageManager.NameNotFoundException {
        final PackageInfo packageInfo = ApplicationUtils.getPackageInfo(applicationContext);
        final String applicationVersionName = packageInfo.versionName;
        final int applicationVersionCode = packageInfo.versionCode;

        return String.format("%s (%s)", applicationVersionName, applicationVersionCode);
    }

}
